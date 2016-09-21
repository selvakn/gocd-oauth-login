package com.tw.go.plugin;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.common.base.Function;
import com.google.gson.reflect.TypeToken;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.GoPlugin;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.GoApiRequest;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.tw.go.plugin.util.FieldValidator;
import com.tw.go.plugin.util.JSONUtils;
import org.apache.commons.io.IOUtils;
import com.tw.go.plugin.provider.Provider;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.*;

import static com.google.common.collect.Lists.transform;

@Extension
public class OAuthLoginPlugin implements GoPlugin {
    private static Logger LOGGER = Logger.getLoggerFor(OAuthLoginPlugin.class);

    private static final String EXTENSION_NAME = "authentication";
    private static final List<String> goSupportedVersions = Collections.singletonList("1.0");

    private static final String PLUGIN_SETTINGS_SERVER_BASE_URL = "server_base_url";
    private static final String PLUGIN_SETTINGS_CONSUMER_KEY = "consumer_key";
    private static final String PLUGIN_SETTINGS_CONSUMER_SECRET = "consumer_secret";
    private static final String PLUGIN_SETTINGS_PRIVATE_TOKEN = "private_token";
    private static final String PLUGIN_SETTINGS_OAUTH_SERVER = "oauth_server_base_url";

    private static final String PLUGIN_SETTINGS_GET_CONFIGURATION = "go.plugin-settings.get-configuration";
    private static final String PLUGIN_SETTINGS_GET_VIEW = "go.plugin-settings.get-view";
    private static final String PLUGIN_SETTINGS_VALIDATE_CONFIGURATION = "go.plugin-settings.validate-configuration";
    private static final String PLUGIN_CONFIGURATION = "go.authentication.plugin-configuration";
    private static final String SEARCH_USER = "go.authentication.search-user";
    private static final String WEB_REQUEST_INDEX = "index";
    private static final String WEB_REQUEST_AUTHENTICATE = "authenticate";

    private static final String GET_PLUGIN_SETTINGS = "go.processor.plugin-settings.get";

    private static final String GO_REQUEST_SESSION_PUT = "go.processor.session.put";
    private static final String GO_REQUEST_SESSION_GET = "go.processor.session.get";
    private static final String GO_REQUEST_SESSION_REMOVE = "go.processor.session.remove";
    private static final String GO_REQUEST_AUTHENTICATE_USER = "go.processor.authentication.authenticate-user";

    private static final int SUCCESS_RESPONSE_CODE = 200;
    private static final int REDIRECT_RESPONSE_CODE = 302;
    private static final int NOT_FOUND_ERROR_RESPONSE_CODE = 404;
    private static final int INTERNAL_ERROR_RESPONSE_CODE = 500;

    private Provider provider;
    private GoApplicationAccessor goApplicationAccessor;

    public OAuthLoginPlugin() {
        try {
            Properties properties = new Properties();
            properties.load(getClass().getResourceAsStream("/defaults.properties"));
            Class<?> providerClass = Class.forName(properties.getProperty("provider"));
            Constructor<?> constructor = providerClass.getConstructor();
            provider = (Provider) constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("could not create provider", e);
        }
    }

    @Override
    public void initializeGoApplicationAccessor(GoApplicationAccessor goApplicationAccessor) {
        this.goApplicationAccessor = goApplicationAccessor;
    }

    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest goPluginApiRequest) {
        String requestName = goPluginApiRequest.requestName();
        switch (requestName) {
            case PLUGIN_SETTINGS_GET_CONFIGURATION:
                return handleGetPluginSettingsConfiguration();
            case PLUGIN_SETTINGS_GET_VIEW:
                return handleGetPluginSettingsView();
            case PLUGIN_SETTINGS_VALIDATE_CONFIGURATION:
                return handleValidatePluginSettingsConfiguration(goPluginApiRequest);
            case PLUGIN_CONFIGURATION:
                return renderJSON(SUCCESS_RESPONSE_CODE, getPluginConfiguration());
            case SEARCH_USER:
                return handleSearchUserRequest(goPluginApiRequest);
            case WEB_REQUEST_INDEX:
                return handleSetupLoginWebRequest();
            case WEB_REQUEST_AUTHENTICATE:
                return handleAuthenticateWebRequest(goPluginApiRequest);
        }
        return renderJSON(NOT_FOUND_ERROR_RESPONSE_CODE, null);
    }

    @Override
    public GoPluginIdentifier pluginIdentifier() {
        return getGoPluginIdentifier();
    }

    private GoPluginApiResponse handleGetPluginSettingsConfiguration() {
        Map<String, Object> response = new HashMap<>();
        response.put(PLUGIN_SETTINGS_SERVER_BASE_URL, createField("Server Base URL", null, true, false, "0"));
        response.put(PLUGIN_SETTINGS_CONSUMER_KEY, createField("OAuth Client ID", null, true, false, "1"));
        response.put(PLUGIN_SETTINGS_CONSUMER_SECRET, createField("OAuth Client Secret", null, true, false, "2"));
        return renderJSON(SUCCESS_RESPONSE_CODE, response);
    }

    private Map<String, Object> createField(String displayName, String defaultValue, boolean isRequired, boolean isSecure, String displayOrder) {
        Map<String, Object> fieldProperties = new HashMap<>();
        fieldProperties.put("display-name", displayName);
        fieldProperties.put("default-value", defaultValue);
        fieldProperties.put("required", isRequired);
        fieldProperties.put("secure", isSecure);
        fieldProperties.put("display-order", displayOrder);
        return fieldProperties;
    }

    private GoPluginApiResponse handleGetPluginSettingsView() {
        try {
            String template = IOUtils.toString(getClass().getResourceAsStream("/plugin-settings.template.html"), "UTF-8");
            return renderJSON(SUCCESS_RESPONSE_CODE, map("template", template));
        } catch (IOException e) {
            return renderJSON(500, String.format("Failed to find template: %s", e.getMessage()));
        }
    }

    private GoPluginApiResponse handleValidatePluginSettingsConfiguration(GoPluginApiRequest goPluginApiRequest) {
        Map<String, Object> responseMap = JSONUtils.fromJSON(goPluginApiRequest.requestBody(), new TypeToken<Map<String, Object>>() {
        }.getType());
        final Map<String, String> configuration = keyValuePairs(responseMap, "plugin-settings");
        List<Map<String, Object>> response = new ArrayList<>();

        validate(response, new FieldValidator() {
            @Override
            public void validate(Map<String, Object> fieldValidation) {
                validateRequiredField(configuration, fieldValidation, "server_base_url", "Server Base URL");
            }
        });

        validate(response, new FieldValidator() {
            @Override
            public void validate(Map<String, Object> fieldValidation) {
                validateRequiredField(configuration, fieldValidation, "consumer_key", "OAuth Client ID");
            }
        });

        validate(response, new FieldValidator() {
            @Override
            public void validate(Map<String, Object> fieldValidation) {
                validateRequiredField(configuration, fieldValidation, "consumer_secret", "OAuth Client Secret");
            }
        });

        return renderJSON(SUCCESS_RESPONSE_CODE, response);
    }

    private void validate(List<Map<String, Object>> response, FieldValidator fieldValidator) {
        Map<String, Object> fieldValidation = new HashMap<>();
        fieldValidator.validate(fieldValidation);
        if (!fieldValidation.isEmpty()) {
            response.add(fieldValidation);
        }
    }

    private void validateRequiredField(Map<String, String> configuration, Map<String, Object> fieldMap, String key, String name) {
        if (configuration.get(key) == null || configuration.get(key).isEmpty()) {
            fieldMap.put("key", key);
            fieldMap.put("message", String.format("'%s' is a required field", name));
        }
    }

    private Map<String, Object> getPluginConfiguration() {
        Map<String, Object> configuration = new HashMap<>();
        configuration.put("display-name", provider.getName());
        configuration.put("display-image-url", provider.getImageURL());
        configuration.put("supports-web-based-authentication", true);
        configuration.put("supports-password-based-authentication", false);
        return configuration;
    }

    private GoPluginApiResponse handleSearchUserRequest(GoPluginApiRequest goPluginApiRequest) {
        if (!provider.isSearchUserEnabled()) {
            return renderJSON(SUCCESS_RESPONSE_CODE, null);
        }

        Map<String, String> requestBodyMap = JSONUtils.asMapOfStrings(goPluginApiRequest.requestBody());
        String searchTerm = requestBodyMap.get("search-term");
        PluginSettings pluginSettings = getPluginSettings();
        try {
            List<GoCDUser> users = provider.searchUser(getOauth20Service(), pluginSettings, searchTerm);
            List<Map<String, String>> searchResults = transform(users, new Function<GoCDUser, Map<String, String>>() {
                @Override
                public Map<String, String> apply(GoCDUser goCDUser) {
                    return getUserMap(goCDUser);
                }
            });
            return renderJSON(SUCCESS_RESPONSE_CODE, searchResults);
        } catch (IOException e) {
            return renderJSON(SUCCESS_RESPONSE_CODE, null);
        }
    }

    private GoPluginApiResponse handleSetupLoginWebRequest() {
        try {
            return renderJSON(REDIRECT_RESPONSE_CODE, map("Location", getAuthorizationUrl()), null);
        } catch (Exception e) {
            LOGGER.error("Error occurred while OAuth setup.", e);
            return renderJSON(INTERNAL_ERROR_RESPONSE_CODE, null);
        }
    }

    private PluginSettings getPluginSettings() {
        GoApiResponse response = goApplicationAccessor.submit(
                createGoApiRequest(
                        GET_PLUGIN_SETTINGS,
                        JSONUtils.toJSON(map("plugin-id", provider.getPluginId()))
                ));
        if (response.responseBody() == null || response.responseBody().trim().isEmpty()) {
            throw new RuntimeException("plugin is not configured. please provide plugin settings.");
        }
        Map<String, String> responseBodyMap = JSONUtils.asMapOfStrings(response.responseBody());
        return new PluginSettings(responseBodyMap.get(PLUGIN_SETTINGS_SERVER_BASE_URL), responseBodyMap.get(PLUGIN_SETTINGS_CONSUMER_KEY),
                responseBodyMap.get(PLUGIN_SETTINGS_CONSUMER_SECRET),
                responseBodyMap.get(PLUGIN_SETTINGS_PRIVATE_TOKEN),
                responseBodyMap.get(PLUGIN_SETTINGS_OAUTH_SERVER)
        );
    }

    private GoPluginApiResponse handleAuthenticateWebRequest(final GoPluginApiRequest goPluginApiRequest) {
        try {
            PluginSettings pluginSettings = getPluginSettings();

            GoCDUser user = authenticate(goPluginApiRequest);

            setUserSessionAs(user);

            return renderJSON(REDIRECT_RESPONSE_CODE, map("Location", pluginSettings.getServerBaseURL()), null);
        } catch (Exception e) {
            LOGGER.error("Error occurred while OAuth authenticate.", e);
            return renderJSON(INTERNAL_ERROR_RESPONSE_CODE, null);
        }
    }

    private GoCDUser authenticate(GoPluginApiRequest goPluginApiRequest) throws IOException {
        String code = goPluginApiRequest.requestParameters().get("code");
        String secretState = goPluginApiRequest.requestParameters().get("state");
        OAuth20Service service = getOauth20Service(secretState);
        String accessToken = service.getAccessToken(code).getAccessToken();
        saveInSession("oauthAccessToken", accessToken);
        return provider.getUser(accessToken, service, getPluginSettings());
    }

    private String getAuthorizationUrl() {
        return getOauth20Service().getAuthorizationUrl();
    }

    private OAuth20Service getOauth20Service() {
        final String secretState = "secret" + new Random().nextInt(999_999);
        return getOauth20Service(secretState);
    }

    private OAuth20Service getOauth20Service(String secretState) {
        String clientId = getPluginSettings().getConsumerKey();
        String clientSecret = getPluginSettings().getConsumerSecret();
        ServiceBuilder serviceBuilder = new ServiceBuilder()
                .apiKey(clientId)
                .apiSecret(clientSecret)
                .state(secretState)
                .callback(getURL(getPluginSettings().getServerBaseURL()));
        if (provider.getScope() != null)
            serviceBuilder.scope(provider.getScope());
        return serviceBuilder.build(provider.oauthService(getPluginSettings()));
    }

    private void saveInSession(String key, String value) {
        GoApiRequest goApiRequest = createGoApiRequest(
                GO_REQUEST_SESSION_PUT, JSONUtils.toJSON(map(
                        "plugin-id", provider.getPluginId(), "session-data", map(key, value))
                ));
        goApplicationAccessor.submit(goApiRequest);
    }

    private String getFromSession(String key) {
        GoApiRequest goApiRequest = createGoApiRequest(
                GO_REQUEST_SESSION_GET, JSONUtils.toJSON(map("plugin-id", provider.getPluginId()))
        );
        return JSONUtils.asMapOfStrings(
                goApplicationAccessor.submit(goApiRequest).responseBody()
        ).get(key);
    }

    private void setUserSessionAs(GoCDUser user) {
        GoApiRequest authenticateUserRequest = createGoApiRequest(
                GO_REQUEST_AUTHENTICATE_USER,
                JSONUtils.toJSON(map("user", getUserMap(user)))
        );
        goApplicationAccessor.submit(authenticateUserRequest);
    }

    private String getURL(String serverBaseURL) {
        return String.format("%s/go/plugin/interact/%s/authenticate", serverBaseURL, provider.getPluginId());
    }

    private Map<String, String> getUserMap(GoCDUser user) {
        Map<String, String> userMap = new HashMap<>();
        userMap.put("username", user.getUsername());
        userMap.put("display-name", user.getDisplayName());
        userMap.put("email-id", user.getEmailId());
        return userMap;
    }

    private Map<String, String> keyValuePairs(Map<String, Object> map, String mainKey) {
        Map<String, String> keyValuePairs = new HashMap<>();
        Map<String, Object> fieldsMap = (Map<String, Object>) map.get(mainKey);
        for (String field : fieldsMap.keySet()) {
            Map<String, Object> fieldProperties = (Map<String, Object>) fieldsMap.get(field);
            String value = (String) fieldProperties.get("value");
            keyValuePairs.put(field, value);
        }
        return keyValuePairs;
    }

    private GoPluginIdentifier getGoPluginIdentifier() {
        return new GoPluginIdentifier(EXTENSION_NAME, goSupportedVersions);
    }

    private GoApiRequest createGoApiRequest(final String api, final String requestBody) {
        return new GoApiRequest() {
            @Override
            public String api() {
                return api;
            }

            @Override
            public String apiVersion() {
                return "1.0";
            }

            @Override
            public GoPluginIdentifier pluginIdentifier() {
                return getGoPluginIdentifier();
            }

            @Override
            public Map<String, String> requestParameters() {
                return null;
            }

            @Override
            public Map<String, String> requestHeaders() {
                return null;
            }

            @Override
            public String requestBody() {
                return requestBody;
            }
        };
    }

    private GoPluginApiResponse renderJSON(final int responseCode, Object response) {
        return renderJSON(responseCode, null, response);
    }

    private GoPluginApiResponse renderJSON(final int responseCode, final Map<String, String> responseHeaders, Object response) {
        final String json = response == null ? null : JSONUtils.toJSON(response);
        return new GoPluginApiResponse() {
            @Override
            public int responseCode() {
                return responseCode;
            }

            @Override
            public Map<String, String> responseHeaders() {
                return responseHeaders;
            }

            @Override
            public String responseBody() {
                return json;
            }
        };
    }

    private <T, U> Map<T, U> map(final T key, final U value) {
        Map<T, U> retVal = new HashMap<>();
        retVal.put(key, value);
        return retVal;
    }

    private <T, U> Map<T, U> map(T key1, U value1, T key2, U value2) {
        Map<T, U> retVal = new HashMap<>();
        retVal.put(key1, value1);
        retVal.put(key2, value2);
        return retVal;
    }
}

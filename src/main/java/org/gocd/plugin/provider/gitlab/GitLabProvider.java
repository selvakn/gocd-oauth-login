package org.gocd.plugin.provider.gitlab;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.gson.reflect.TypeToken;
import org.gocd.plugin.PluginSettings;
import org.gocd.plugin.Profile;
import org.gocd.plugin.User;
import org.gocd.plugin.provider.Provider;
import org.gocd.plugin.util.ImageReader;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.gocd.plugin.util.JSONUtils.fromJSON;

public class GitLabProvider implements Provider {

    private static final String IMAGE = ImageReader.readImage("logo_gitlab_64px.png");
    public static final String CURRENT_USER = "%s/api/v3/user";

    @Override
    public String getPluginId() {
        return "gitlab.oauth.login";
    }

    @Override
    public String getName() {
        return "Gitlab";
    }

    @Override
    public String getImageURL() {
        return IMAGE;
    }

    @Override
    public DefaultApi20 oauthService(PluginSettings pluginSettings) {
        return new GitlabApi(pluginSettings.getOauthServerBaseURL());
    }

    @Override
    public User getUser(String accessToken, OAuth20Service service, PluginSettings pluginSettings) throws IOException {
        OAuthRequest request = new OAuthRequest(Verb.GET, String.format(CURRENT_USER, pluginSettings.getOauthServerBaseURL()), service);
        request.addQuerystringParameter("access_token", accessToken);
        Response response = request.send();

        Map<String, Object> responseBody = fromJSON(response.getBody(), new TypeToken<Map<String, Object>>() {
        }.getType());

        return getUser(new Profile((String) responseBody.get("email"), (String) responseBody.get("name")));
    }
}

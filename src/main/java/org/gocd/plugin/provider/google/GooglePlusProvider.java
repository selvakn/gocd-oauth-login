package org.gocd.plugin.provider.google;

import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.gson.reflect.TypeToken;
import com.thoughtworks.go.plugin.api.logging.Logger;
import org.gocd.plugin.GoCDUser;
import org.gocd.plugin.PluginSettings;
import org.gocd.plugin.provider.Provider;
import org.gocd.plugin.util.ImageReader;

import java.io.IOException;

import static org.gocd.plugin.util.JSONUtils.fromJSON;

public class GooglePlusProvider implements Provider {

    private static final String IMAGE = ImageReader.readImage("logo_google_plus_64px.png");
    private static final String CURRENT_USER = "https://www.googleapis.com/plus/v1/people/me";

    @Override
    public String getPluginId() {
        return "google.oauth.login";
    }

    @Override
    public String getName() {
        return "Google";
    }

    @Override
    public String getImageURL() {
        return IMAGE;
    }

    @Override
    public String getScope() {
        return "email";
    }

    @Override
    public boolean isSearchUserEnabled() {
        return false;
    }

    @Override
    public DefaultApi20 oauthService(PluginSettings pluginSettings) {
        return GoogleApi20.instance();
    }

    @Override
    public GoCDUser getUser(String accessToken, OAuth20Service service, PluginSettings pluginSettings) throws IOException {
        OAuthRequest request = new OAuthRequest(Verb.GET, String.format(CURRENT_USER, pluginSettings.getOauthServerBaseURL()), service);
        request.addQuerystringParameter("access_token", accessToken);
        Response response = request.send();

        Logger.getLoggerFor(GooglePlusProvider.class).error(response.getBody());
        GooglePlusUser googleUser = fromJSON(response.getBody(), new TypeToken<GooglePlusUser>() {
        }.getType());
        Logger.getLoggerFor(GooglePlusProvider.class).error(googleUser.toString());

        return googleUser.toUser();
    }
}

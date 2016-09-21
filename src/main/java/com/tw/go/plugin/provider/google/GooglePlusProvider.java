package com.tw.go.plugin.provider.google;

import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.gson.reflect.TypeToken;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.tw.go.plugin.GoCDUser;
import com.tw.go.plugin.PluginSettings;
import com.tw.go.plugin.provider.Provider;
import com.tw.go.plugin.util.ImageReader;
import com.tw.go.plugin.util.JSONUtils;

import java.io.IOException;

public class GooglePlusProvider extends Provider {

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
        OAuthRequest request = new OAuthRequest(Verb.GET, CURRENT_USER, service);
        request.addQuerystringParameter("access_token", accessToken);
        Response response = request.send();

        Logger.getLoggerFor(GooglePlusProvider.class).error(response.getBody());
        GooglePlusUser googleUser = JSONUtils.fromJSON(response.getBody(), new TypeToken<GooglePlusUser>() {
        }.getType());

        return googleUser.toUser();
    }
}

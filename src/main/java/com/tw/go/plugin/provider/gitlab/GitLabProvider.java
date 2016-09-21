package com.tw.go.plugin.provider.gitlab;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.gson.reflect.TypeToken;
import com.tw.go.plugin.GoCDUser;
import com.tw.go.plugin.util.ImageReader;
import com.tw.go.plugin.util.JSONUtils;
import com.tw.go.plugin.PluginSettings;
import com.tw.go.plugin.provider.Provider;

import java.io.IOException;
import java.util.List;

public class GitLabProvider extends Provider {

    private static final String IMAGE = ImageReader.readImage("logo_gitlab_64px.png");
    private static final String CURRENT_USER = "%s/api/v3/user";
    private static final String SEARCH_USERS = "%s/api/v3/users";

    @Override
    public String getPluginId() {
        return "gitlab.oauth.login";
    }

    @Override
    public String getName() {
        return "GitLab";
    }

    @Override
    public String getImageURL() {
        return IMAGE;
    }

    @Override
    public String getScope() {
        return null;
    }

    @Override
    public List<GoCDUser> searchUser(OAuth20Service service, PluginSettings pluginSettings, String searchTerm) throws IOException {
        OAuthRequest request = new OAuthRequest(Verb.GET, String.format(SEARCH_USERS, pluginSettings.getOauthServerBaseURL()), service);
        request.addQuerystringParameter("private_token", pluginSettings.getPrivateToken());
        request.addQuerystringParameter("search", searchTerm);
        Response response = request.send();

        List<GitLabUser> usersResponse = JSONUtils.fromJSON(response.getBody(), new TypeToken<List<GitLabUser>>() {
        }.getType());
        return toUsers(usersResponse);
    }

    @Override
    public DefaultApi20 oauthService(PluginSettings pluginSettings) {
        return new GitLabApi(pluginSettings.getOauthServerBaseURL());
    }

    @Override
    public GoCDUser getUser(String accessToken, OAuth20Service service, PluginSettings pluginSettings) throws IOException {
        OAuthRequest request = new OAuthRequest(Verb.GET, String.format(CURRENT_USER, pluginSettings.getOauthServerBaseURL()), service);
        request.addQuerystringParameter("access_token", accessToken);
        Response response = request.send();

        GitLabUser gitLabUser = JSONUtils.fromJSON(response.getBody(), new TypeToken<GitLabUser>() {
        }.getType());

        return gitLabUser.toUser();
    }
}

package org.gocd.plugin.provider.github;

import com.github.scribejava.apis.GitHubApi;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.gson.reflect.TypeToken;
import com.thoughtworks.go.plugin.api.logging.Logger;
import lombok.Getter;
import org.gocd.plugin.GoCDUser;
import org.gocd.plugin.PluginSettings;
import org.gocd.plugin.provider.Provider;
import org.gocd.plugin.util.ImageReader;

import java.io.IOException;
import java.util.List;

import static org.gocd.plugin.util.JSONUtils.fromJSON;

public class GitHubProvider extends Provider {

    private static final String IMAGE = ImageReader.readImage("logo_github_64px.png");
    private static final String CURRENT_USER = "https://api.github.com/user";
    private static final String SEARCH_USERS = "https://api.github.com/search/users?q=%s";

    @Override
    public String getPluginId() {
        return "github.oauth.login";
    }

    @Override
    public String getName() {
        return "GitHub";
    }

    @Override
    public String getImageURL() {
        return IMAGE;
    }

    @Override
    public String getScope() {
        return "user:email";
    }

    @Override
    public List<GoCDUser> searchUser(OAuth20Service service, PluginSettings pluginSettings, String searchTerm) throws IOException {
        OAuthRequest request = new OAuthRequest(Verb.GET, String.format(SEARCH_USERS, searchTerm), service);
        request.addQuerystringParameter("client_id", pluginSettings.getConsumerKey());
        request.addQuerystringParameter("client_secret", pluginSettings.getConsumerSecret());
        Response response = request.send();

        Logger.getLoggerFor(GitHubProvider.class).error("Search results: " + response);

        UserSearchResults usersResponse = fromJSON(response.getBody(), new TypeToken<UserSearchResults>() {
        }.getType());
        return toUsers(usersResponse.getItems());
    }

    @Override
    public DefaultApi20 oauthService(PluginSettings pluginSettings) {
        return GitHubApi.instance();
    }

    @Override
    public GoCDUser getUser(String accessToken, OAuth20Service service, PluginSettings pluginSettings) throws IOException {
        OAuthRequest request = new OAuthRequest(Verb.GET, CURRENT_USER, service);
        request.addQuerystringParameter("access_token", accessToken);
        Response response = request.send();

        GitHubUser githubUser = fromJSON(response.getBody(), new TypeToken<GitHubUser>() {
        }.getType());

        return githubUser.toUser();
    }

    @Getter
    private static class UserSearchResults {
        private List<GitHubUser> items;
    }
}

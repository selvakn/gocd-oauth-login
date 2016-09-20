package org.gocd.plugin.provider.gitlab;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.gson.reflect.TypeToken;
import org.gocd.plugin.GoCDUser;
import org.gocd.plugin.PluginSettings;
import org.gocd.plugin.provider.Provider;
import org.gocd.plugin.util.ImageReader;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.gocd.plugin.util.JSONUtils.fromJSON;

public class GitLabProvider implements Provider {

    private static final String IMAGE = ImageReader.readImage("logo_gitlab_64px.png");
    private static final String CURRENT_USER = "%s/api/v3/user";
    private static final String SEARCH_USERS = "%s/api/v3/users?search=%s";

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
    public List<GoCDUser> searchUser(OAuth20Service service, PluginSettings pluginSettings, String searchTerm) throws IOException {
        OAuthRequest request = new OAuthRequest(Verb.GET, String.format(SEARCH_USERS, pluginSettings.getOauthServerBaseURL(), searchTerm), service);
        request.addQuerystringParameter("private_token", pluginSettings.getPrivateToken());
        Response response = request.send();

        List<GitLabUser> usersResponse = fromJSON(response.getBody(), new TypeToken<List<GitLabUser>>() {
        }.getType());
        return usersResponse
                .stream()
                .map(GitLabUser::toUser)
                .collect(Collectors.toList());
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

        GitLabUser gitLabUser = fromJSON(response.getBody(), new TypeToken<GitLabUser>() {
        }.getType());

        return gitLabUser.toUser();
    }
}

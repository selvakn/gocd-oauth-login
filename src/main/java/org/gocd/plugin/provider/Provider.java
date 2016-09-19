package org.gocd.plugin.provider;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.gocd.plugin.PluginSettings;
import org.gocd.plugin.Profile;
import org.gocd.plugin.User;

import java.io.IOException;
import java.util.List;

public interface Provider {
    public String getPluginId();

    public String getName();

    public String getImageURL();

    default User getUser(Profile profile) {
        String emailId = profile.getEmail();
        String fullName = profile.getFullName();
        return new User(emailId, fullName, emailId);
    }

    public List<User> searchUser(String accessToken, OAuth20Service service, PluginSettings pluginSettings, String searchTerm) throws IOException;

    public DefaultApi20 oauthService(PluginSettings pluginSettings);

    public User getUser(String accessToken, OAuth20Service service, PluginSettings pluginSettings) throws IOException;
}

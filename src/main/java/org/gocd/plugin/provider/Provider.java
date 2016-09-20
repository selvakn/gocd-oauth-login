package org.gocd.plugin.provider;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.gocd.plugin.GoCDUser;
import org.gocd.plugin.PluginSettings;

import java.io.IOException;
import java.util.List;

public interface Provider {
    public String getPluginId();

    public String getName();

    public String getImageURL();

    default boolean isSearchUserEnabled() {
        return true;
    }

    public List<GoCDUser> searchUser(OAuth20Service service, PluginSettings pluginSettings, String searchTerm) throws IOException;

    public DefaultApi20 oauthService(PluginSettings pluginSettings);

    public GoCDUser getUser(String accessToken, OAuth20Service service, PluginSettings pluginSettings) throws IOException;
}

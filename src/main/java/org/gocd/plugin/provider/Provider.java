package org.gocd.plugin.provider;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.gocd.plugin.GoCDUser;
import org.gocd.plugin.PluginSettings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface Provider {
    String getPluginId();

    String getName();

    String getImageURL();

    String getScope();

    default boolean isSearchUserEnabled() {
        return true;
    }

    default List<GoCDUser> searchUser(OAuth20Service service, PluginSettings pluginSettings, String searchTerm) throws IOException {
        return new ArrayList<>();
    }

    DefaultApi20 oauthService(PluginSettings pluginSettings);

    GoCDUser getUser(String accessToken, OAuth20Service service, PluginSettings pluginSettings) throws IOException;
}

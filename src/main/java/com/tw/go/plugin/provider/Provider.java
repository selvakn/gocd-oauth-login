package com.tw.go.plugin.provider;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.common.base.Function;
import com.tw.go.plugin.GoCDUser;
import com.tw.go.plugin.PluginSettings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.transform;

public abstract class Provider {
    public abstract String getPluginId();

    public abstract String getName();

    public abstract String getImageURL();

    public abstract String getScope();

    public boolean isSearchUserEnabled() {
        return true;
    }

    public List<GoCDUser> searchUser(OAuth20Service service, PluginSettings pluginSettings, String searchTerm) throws IOException{
        return new ArrayList<>();
    }

    public abstract DefaultApi20 oauthService(PluginSettings pluginSettings);

    public abstract GoCDUser getUser(String accessToken, OAuth20Service service, PluginSettings pluginSettings) throws IOException;

    protected <T extends UserConvertable> List<GoCDUser> toUsers(List<T> providerUsers) {
        return  transform(providerUsers, new Function<T, GoCDUser>() {
            @Override
            public GoCDUser apply(T t) {
                return t.toUser();
            }
        });
    }
}

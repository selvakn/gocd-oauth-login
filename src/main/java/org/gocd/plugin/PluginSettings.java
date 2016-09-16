package org.gocd.plugin;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class PluginSettings {
    private String serverBaseURL;
    private String consumerKey;
    private String consumerSecret;
    private String username;
    private String password;
    private String oauthToken;
    private String oauthServerBaseURL;

    public PluginSettings(String serverBaseURL, String consumerKey, String consumerSecret,
                          String username, String password, String oauthToken, String oauthServerBaseURL) {
        this.serverBaseURL = serverBaseURL;
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.username = username;
        this.password = password;
        this.oauthToken = oauthToken;
        this.oauthServerBaseURL = oauthServerBaseURL;
    }

    public String getOauthServerBaseURL() {
        return oauthServerBaseURL;
    }

    public String getServerBaseURL() {
        return serverBaseURL;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getOauthToken() {
        return oauthToken;
    }

    public boolean containsUsernameAndPassword() {
        return !isBlank(username) && !isBlank(password);
    }

    public boolean containsOAuthToken() {
        return !isBlank(oauthToken);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof PluginSettings)) return false;

        PluginSettings that = (PluginSettings) o;

        return new EqualsBuilder()
                .append(serverBaseURL, that.serverBaseURL)
                .append(consumerKey, that.consumerKey)
                .append(consumerSecret, that.consumerSecret)
                .append(username, that.username)
                .append(password, that.password)
                .append(oauthToken, that.oauthToken)
                .append(oauthServerBaseURL, that.oauthServerBaseURL)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(serverBaseURL)
                .append(consumerKey)
                .append(consumerSecret)
                .append(username)
                .append(password)
                .append(oauthToken)
                .append(oauthServerBaseURL)
                .toHashCode();
    }
}

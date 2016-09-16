package org.gocd.plugin;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PluginSettings {
    private String serverBaseURL;
    private String consumerKey;
    private String consumerSecret;
    private String oauthServerBaseURL;

    public PluginSettings(String serverBaseURL, String consumerKey, String consumerSecret, String oauthServerBaseURL) {
        this.serverBaseURL = serverBaseURL;
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof PluginSettings)) return false;

        PluginSettings that = (PluginSettings) o;

        return new EqualsBuilder()
                .append(serverBaseURL, that.serverBaseURL)
                .append(consumerKey, that.consumerKey)
                .append(consumerSecret, that.consumerSecret)
                .append(oauthServerBaseURL, that.oauthServerBaseURL)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(serverBaseURL)
                .append(consumerKey)
                .append(consumerSecret)
                .append(oauthServerBaseURL)
                .toHashCode();
    }
}

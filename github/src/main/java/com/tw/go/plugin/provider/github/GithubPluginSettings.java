package com.tw.go.plugin.provider.github;

import com.tw.go.plugin.PluginSettings;

import static org.apache.commons.lang.StringUtils.isBlank;

public class GithubPluginSettings extends PluginSettings {
    private final String githubOrg;
    private String username;
    private String password;
    private String oauthToken;

    public GithubPluginSettings(String serverBaseURL, String consumerKey, String consumerSecret, String username, String password, String oauthToken, String githubOrg) {
        super(serverBaseURL, consumerKey, consumerSecret);
        this.username = username;
        this.password = password;
        this.oauthToken = oauthToken;
        this.githubOrg = githubOrg;
    }

    public String getGithubOrg() {
        return githubOrg;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOauthToken() {
        return oauthToken;
    }

    public void setOauthToken(String oauthToken) {
        this.oauthToken = oauthToken;
    }

    public boolean containsUsernameAndPassword() {
        return !isBlank(username) && !isBlank(password);
    }

    public boolean containsOAuthToken() {
        return !isBlank(oauthToken);
    }
}

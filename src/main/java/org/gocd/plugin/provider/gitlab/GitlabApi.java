package org.gocd.plugin.provider.gitlab;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.extractors.OAuth2AccessTokenJsonExtractor;
import com.github.scribejava.core.extractors.TokenExtractor;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.Verb;

public class GitlabApi extends DefaultApi20 {

    private String oauthServerBaseURL;

    public GitlabApi(String oauthServerBaseURL) {
        this.oauthServerBaseURL = oauthServerBaseURL;
    }

    @Override
    public Verb getAccessTokenVerb() {
        return Verb.POST;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return String.format("%s/oauth/token", oauthServerBaseURL);
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return String.format("%s/oauth/authorize", oauthServerBaseURL);
    }

    @Override
    public TokenExtractor<OAuth2AccessToken> getAccessTokenExtractor() {
        return OAuth2AccessTokenJsonExtractor.instance();
    }
}

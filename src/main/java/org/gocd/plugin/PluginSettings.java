package org.gocd.plugin;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class PluginSettings {
    private String serverBaseURL;
    private String consumerKey;
    private String consumerSecret;
    private String oauthServerBaseURL;
}

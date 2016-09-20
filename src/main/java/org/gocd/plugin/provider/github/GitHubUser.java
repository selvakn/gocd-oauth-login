package org.gocd.plugin.provider.github;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.gocd.plugin.GoCDUser;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GitHubUser {
    private String login;
    private String name;

    public GoCDUser toUser() {
        return new GoCDUser(login, name, name);
    }
}

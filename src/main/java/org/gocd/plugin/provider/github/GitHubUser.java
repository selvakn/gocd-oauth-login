package org.gocd.plugin.provider.github;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.gocd.plugin.GoCDUser;
import org.gocd.plugin.provider.UserConvertable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GitHubUser implements UserConvertable {
    private String login;
    private String name;
    private String email;

    public GoCDUser toUser() {
        return new GoCDUser(login, name, email);
    }
}

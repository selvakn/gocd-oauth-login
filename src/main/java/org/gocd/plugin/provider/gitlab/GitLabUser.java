package org.gocd.plugin.provider.gitlab;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.gocd.plugin.GoCDUser;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GitLabUser {
    private String email;
    private String name;

    public GoCDUser toUser() {
        return new GoCDUser(email, name, email);
    }
}

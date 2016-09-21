package com.tw.go.plugin.provider.github;

import com.tw.go.plugin.GoCDUser;
import com.tw.go.plugin.provider.UserConvertable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

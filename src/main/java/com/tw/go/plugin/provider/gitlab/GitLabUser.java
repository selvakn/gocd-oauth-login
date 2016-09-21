package com.tw.go.plugin.provider.gitlab;

import com.tw.go.plugin.GoCDUser;
import com.tw.go.plugin.provider.UserConvertable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GitLabUser implements UserConvertable {
    private String email;
    private String name;

    public GoCDUser toUser() {
        return new GoCDUser(email, name, email);
    }
}

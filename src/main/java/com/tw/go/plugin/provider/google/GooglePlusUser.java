package com.tw.go.plugin.provider.google;

import com.tw.go.plugin.GoCDUser;
import com.tw.go.plugin.provider.UserConvertable;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class GooglePlusUser implements UserConvertable {
    private List<Emails> emails;
    private String displayName;

    public GoCDUser toUser() {
        String email = emails.get(0).value;
        return new GoCDUser(email, displayName, email);
    }

    @Getter
    private static class Emails {
        private String value;
    }
}

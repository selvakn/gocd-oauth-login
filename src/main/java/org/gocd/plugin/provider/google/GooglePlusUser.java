package org.gocd.plugin.provider.google;

import lombok.Getter;
import lombok.ToString;
import org.gocd.plugin.GoCDUser;

import java.util.List;

@Getter
@ToString
public class GooglePlusUser {
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

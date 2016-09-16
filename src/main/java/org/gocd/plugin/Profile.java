package org.gocd.plugin;

public class Profile {
    private String fullName;
    private String email;

    public Profile(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }
}

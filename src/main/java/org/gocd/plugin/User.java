package org.gocd.plugin;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class User {
    private String username;
    private String displayName;
    private String emailId;
}

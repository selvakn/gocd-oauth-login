package com.tw.go.plugin;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class GoCDUser {
    private String username;
    private String displayName;
    private String emailId;
}

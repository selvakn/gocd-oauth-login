package org.gocd.plugin.provider;

import org.gocd.plugin.GoCDUser;

public interface UserConvertable {
    GoCDUser toUser();
}

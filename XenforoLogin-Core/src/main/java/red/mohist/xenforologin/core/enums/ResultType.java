/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.core.enums;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public enum ResultType {
    OK, SERVER_ERROR, PASSWORD_INCORRECT, ERROR_NAME, NO_USER, UNKNOWN, USER_EXIST, EMAIL_EXIST, EMAIL_WRONG;

    ImmutableMap<String, String> inheritedObject;
    private boolean shouldLogin;

    ResultType() {
        shouldLogin = false;
    }

    public ResultType inheritedObject(ImmutableMap<String, String> inherited) {
        inheritedObject = inherited;
        return this;
    }

    public ResultType shouldLogin(boolean should) {
        shouldLogin = should;
        return this;
    }

    public Map<String, String> getInheritedObject() {
        return inheritedObject;
    }

    public boolean isShouldLogin() {
        return shouldLogin;
    }
}

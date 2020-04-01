/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.core.enums;

public enum StatusType {
    NEED_CHECK, NEED_LOGIN, NEED_REGISTER_EMAIL, NEED_REGISTER_PASSWORD, NEED_REGISTER_CONFIRM, LOGGED_IN;
    public String email;
    public String password;

    public StatusType setEmail(String t) {
        email = t;
        return this;
    }

    public StatusType setPassword(String t) {
        password = t;
        return this;
    }
}

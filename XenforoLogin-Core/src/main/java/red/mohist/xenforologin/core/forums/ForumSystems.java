/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.core.forums;

import red.mohist.xenforologin.core.XenforoLoginCore;
import red.mohist.xenforologin.core.forums.implementations.*;

import java.util.Objects;

public class ForumSystems {
    private static ForumSystem currentSystem = null;

    public static void reloadConfig() {
        ForumSystem cs;
        switch ((String) Objects.requireNonNull(XenforoLoginCore.instance.api.getConfigValue("api.system", "xenforo"))) {
            case "xenforo":
                cs = new XenforoSystem((String) XenforoLoginCore.instance.api.getConfigValue("api.xenforo.url"),
                        (String) XenforoLoginCore.instance.api.getConfigValue("api.xenforo.key"));
                break;
            case "discuz":
                cs = new DiscuzSystem((String) XenforoLoginCore.instance.api.getConfigValue("api.discuz.url"),
                        (String) XenforoLoginCore.instance.api.getConfigValue("api.discuz.key"));
                break;
            case "sqlite":
                cs = new SqliteSystem((String) XenforoLoginCore.instance.api.getConfigValue("api.sqlite.path"),
                        (boolean) XenforoLoginCore.instance.api.getConfigValue("api.sqlite.absolute", false),
                        (String) XenforoLoginCore.instance.api.getConfigValue("api.sqlite.table_name"),
                        (String) XenforoLoginCore.instance.api.getConfigValue("api.sqlite.email_field"),
                        (String) XenforoLoginCore.instance.api.getConfigValue("api.sqlite.username_field"),
                        (String) XenforoLoginCore.instance.api.getConfigValue("api.sqlite.password_field"),
                        (String) XenforoLoginCore.instance.api.getConfigValue("api.sqlite.salt_field"),
                        XenforoLoginCore.instance.api.getConfigValueInt("api.sqlite.salt_length", 6),
                        (String) XenforoLoginCore.instance.api.getConfigValue("api.sqlite.password_hash"));
                break;
            case "web":
                cs = new WebSystem((String) XenforoLoginCore.instance.api.getConfigValue("api.web.url"),
                        (String) XenforoLoginCore.instance.api.getConfigValue("api.web.key"));
                break;
            case "mysql":
                cs = new MysqlSystem((String) XenforoLoginCore.instance.api.getConfigValue("api.mysql.host"),
                        (String) XenforoLoginCore.instance.api.getConfigValue("api.mysql.username"),
                        (String) XenforoLoginCore.instance.api.getConfigValue("api.mysql.password"),
                        (String) XenforoLoginCore.instance.api.getConfigValue("api.mysql.database"),
                        (String) XenforoLoginCore.instance.api.getConfigValue("api.mysql.table_name"),
                        (String) XenforoLoginCore.instance.api.getConfigValue("api.mysql.email_field"),
                        (String) XenforoLoginCore.instance.api.getConfigValue("api.mysql.username_field"),
                        (String) XenforoLoginCore.instance.api.getConfigValue("api.mysql.password_field"),
                        (String) XenforoLoginCore.instance.api.getConfigValue("api.mysql.salt_field"),
                        XenforoLoginCore.instance.api.getConfigValueInt("api.mysql.salt_length", 6),
                        (String) XenforoLoginCore.instance.api.getConfigValue("api.mysql.password_hash"));
                break;
            default:
                cs = null;
        }
        if (cs == null) throw new NullPointerException();
        currentSystem = cs;
    }

    public static ForumSystem getCurrentSystem() {
        return currentSystem;
    }

}

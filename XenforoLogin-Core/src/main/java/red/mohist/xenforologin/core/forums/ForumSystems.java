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
import red.mohist.xenforologin.core.utils.Config;
import red.mohist.xenforologin.core.utils.Helper;

import java.util.Objects;

public class ForumSystems {
    private static ForumSystem currentSystem = null;

    public static void reloadConfig() {
        ForumSystem cs;
        switch (Config.getString("api.system", "xenforo")) {
            case "xenforo":
                cs = new XenforoSystem(Config.getString("api.xenforo.url"),
                        Config.getString("api.xenforo.key"));
                break;
            case "discuz":
                cs = new DiscuzSystem(Config.getString("api.discuz.url"),
                        Config.getString("api.discuz.key"));
                break;
            case "sqlite":
                cs = new SqliteSystem(Config.getString("api.sqlite.path"),
                        Config.getBoolean("api.sqlite.absolute", false),
                        Config.getString("api.sqlite.table_name"),
                        Config.getString("api.sqlite.email_field"),
                        Config.getString("api.sqlite.username_field"),
                        Config.getString("api.sqlite.password_field"),
                        Config.getString("api.sqlite.salt_field"),
                        Config.getInteger("api.sqlite.salt_length", 6),
                        Config.getString("api.sqlite.password_hash"));
                break;
            case "web":
                cs = new WebSystem(Config.getString("api.web.url"),
                        Config.getString("api.web.key"));
                break;
            case "mysql":
                cs = new MysqlSystem(Config.getString("api.mysql.host"),
                        Config.getString("api.mysql.username"),
                        Config.getString("api.mysql.password"),
                        Config.getString("api.mysql.database"),
                        Config.getString("api.mysql.table_name"),
                        Config.getString("api.mysql.email_field"),
                        Config.getString("api.mysql.username_field"),
                        Config.getString("api.mysql.password_field"),
                        Config.getString("api.mysql.salt_field"),
                        Config.getInteger("api.mysql.salt_length", 6),
                        Config.getString("api.mysql.password_hash"));
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

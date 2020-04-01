package red.mohist.xenforologin.core.forums;

import red.mohist.xenforologin.core.XenforoLoginCore;
import red.mohist.xenforologin.core.forums.implementations.DiscuzSystem;
import red.mohist.xenforologin.core.forums.implementations.SqliteSystem;
import red.mohist.xenforologin.core.forums.implementations.XenforoSystem;

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
                        (boolean) XenforoLoginCore.instance.api.getConfigValue("api.sqlite.absolute",false),
                        (String) XenforoLoginCore.instance.api.getConfigValue("api.sqlite.table_name"),
                        (String) XenforoLoginCore.instance.api.getConfigValue("api.sqlite.email_field"),
                        (String) XenforoLoginCore.instance.api.getConfigValue("api.sqlite.username_field"),
                        (String) XenforoLoginCore.instance.api.getConfigValue("api.sqlite.password_field"),
                        (String) XenforoLoginCore.instance.api.getConfigValue("api.sqlite.password_hash"));
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

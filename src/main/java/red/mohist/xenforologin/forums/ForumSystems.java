package red.mohist.xenforologin.forums;

import red.mohist.xenforologin.XenforoLogin;
import red.mohist.xenforologin.forums.implementations.DiscuzSystem;
import red.mohist.xenforologin.forums.implementations.XenforoSystem;

import java.util.Objects;

public class ForumSystems {
    private static ForumSystem currentSystem = null;

    public static void reloadConfig() {
        ForumSystem cs;
        switch (Objects.requireNonNull(XenforoLogin.instance.config.getString("api.system", "xenforo"))) {
            case "xenforo":
                cs = new XenforoSystem(XenforoLogin.instance.config.getString("api.xenforo.url"),
                        XenforoLogin.instance.config.getString("api.xenforo.key"));
                break;
            case "discuz":
                cs = new DiscuzSystem(XenforoLogin.instance.config.getString("api.discuz.url"),
                        XenforoLogin.instance.config.getString("api.discuz.key"));
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

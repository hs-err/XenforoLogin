package red.mohist.xenforologin.forums;

import red.mohist.xenforologin.XenforoLogin;
import red.mohist.xenforologin.forums.implementations.DiscuzSystem;
import red.mohist.xenforologin.forums.implementations.XenforoSystem;

import java.util.Objects;

public class ForumSystems {
    private static ForumSystem currentSystem = null;

    public static void reloadConfig() {
        ForumSystem cs;
        switch ((String) Objects.requireNonNull(XenforoLogin.instance.api.getConfigValue("api.system", "xenforo"))) {
            case "xenforo":
                cs = new XenforoSystem((String) XenforoLogin.instance.api.getConfigValue("api.xenforo.url"),
                        (String) XenforoLogin.instance.api.getConfigValue("api.xenforo.key"));
                break;
            case "discuz":
                cs = new DiscuzSystem((String) XenforoLogin.instance.api.getConfigValue("api.discuz.url"),
                        (String) XenforoLogin.instance.api.getConfigValue("api.discuz.key"));
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

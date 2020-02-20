package red.mohist.xenforologin.forums;

import red.mohist.xenforologin.XenforoLogin;
import red.mohist.xenforologin.interfaces.ForumSystem;

import java.util.Objects;

public class ForumSystems {
    private static ForumSystem currentSystem = null;

    public static void reloadConfig() {
        ForumSystem cs;
        //noinspection SwitchStatementWithTooFewBranches
        switch (Objects.requireNonNull(XenforoLogin.instance.config.getString("api.system", "xenforo"))) {
            case "xenforo":
                cs = new XenforoSystem(XenforoLogin.instance.config.getString("api.xenforo.url"),
                        XenforoLogin.instance.config.getString("api.xenforo.url"));
                break;
            default:
                cs = null;
        }
        if (cs == null) throw new NullPointerException();
        currentSystem = cs;
    }

}

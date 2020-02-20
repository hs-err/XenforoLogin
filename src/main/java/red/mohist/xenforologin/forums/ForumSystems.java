package red.mohist.xenforologin.forums;

import red.mohist.xenforologin.Main;
import red.mohist.xenforologin.interfaces.ForumSystem;

import java.util.Objects;

public class ForumSystems {
    private static ForumSystem currentSystem = null;

    public static void reloadConfig() {
        ForumSystem cs;
        //noinspection SwitchStatementWithTooFewBranches
        switch (Objects.requireNonNull(Main.instance.config.getString("api.system", "xenforo"))) {
            case "xenforo":
                cs = new XenforoSystem(Main.instance.config.getString("api.xenforo.url"),
                        Main.instance.config.getString("api.xenforo.url"));
                break;
            default:
                cs = null;
        }
        if (cs == null) throw new NullPointerException();
        currentSystem = cs;
    }

}

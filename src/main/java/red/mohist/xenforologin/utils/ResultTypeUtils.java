package red.mohist.xenforologin.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import red.mohist.xenforologin.XenforoLogin;
import red.mohist.xenforologin.enums.ResultType;

public class ResultTypeUtils {

    public static boolean handle(Player player, ResultType resultType) {
        switch (resultType) {
            case OK:
                if (resultType.isShouldLogin()) {
                    XenforoLogin.instance.login(player);
                }
                return true;
            case PASSWORD_INCORRECT:
                Bukkit.getScheduler().runTask(XenforoLogin.instance, () -> player
                        .kickPlayer(XenforoLogin.instance.langFile("errors.password")));
                return false;
            case ERROR_NAME:
                Bukkit.getScheduler().runTask(XenforoLogin.instance, () -> player
                        .kickPlayer(XenforoLogin.instance.langFile("errors.name_incorrect",
                                resultType.getInheritedObject())));
                return false;
            case NO_USER:
                Bukkit.getScheduler().runTask(XenforoLogin.instance, () -> player
                        .kickPlayer(XenforoLogin.instance.langFile("errors.no_user")));
                return false;
            case UNKNOWN:
                Bukkit.getScheduler().runTask(XenforoLogin.instance, () -> player
                        .kickPlayer(XenforoLogin.instance.langFile("errors.unknown",
                                resultType.getInheritedObject())));
                return false;
            case SERVER_ERROR:
                Bukkit.getScheduler().runTask(XenforoLogin.instance, () -> player
                        .kickPlayer(XenforoLogin.instance.langFile("errors.server")));
                return false;
        }
        return false;
    }
}

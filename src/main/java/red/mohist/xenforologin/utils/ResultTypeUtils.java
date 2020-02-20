package red.mohist.xenforologin.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import red.mohist.xenforologin.XenforoLogin;
import red.mohist.xenforologin.enums.ResultType;

public class ResultTypeUtils {

    public static void handle(Player player, ResultType resultType) {
        switch (resultType) {
            case OK:
                if (resultType.isShouldLogin()) {
                    XenforoLogin.instance.login(player);
                }
                break;
            case PASSWORD_INCORRECT:
                Bukkit.getScheduler().runTask(XenforoLogin.instance, () -> player
                        .kickPlayer(XenforoLogin.instance.langFile("errors.password")));
                break;
            case ERROR_NAME:
                Bukkit.getScheduler().runTask(XenforoLogin.instance, () -> player
                        .kickPlayer(XenforoLogin.instance.langFile("errors.name_incorrect",
                                resultType.getInheritedObject())));
                break;
            case NO_USER:
                Bukkit.getScheduler().runTask(XenforoLogin.instance, () -> player
                        .kickPlayer(XenforoLogin.instance.langFile("errors.no_user")));
                break;
            case UNKNOWN:
                Bukkit.getScheduler().runTask(XenforoLogin.instance, () -> player
                        .kickPlayer(XenforoLogin.instance.langFile("errors.unknown",
                                resultType.getInheritedObject())));
                break;
            case SERVER_ERROR:
                Bukkit.getScheduler().runTask(XenforoLogin.instance, () -> player
                        .kickPlayer(XenforoLogin.instance.langFile("errors.server")));
                break;
        }
    }
}

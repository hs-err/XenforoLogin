package red.mohist.xenforologin.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import red.mohist.xenforologin.XenforoLogin;
import red.mohist.xenforologin.enums.ResultType;
import red.mohist.xenforologin.enums.StatusType;

public class ResultTypeUtils {

    public static boolean handle(Player player, ResultType resultType) {
        switch (resultType) {
            case OK:
                if (resultType.isShouldLogin()) {
                    XenforoLogin.instance.login(player);
                }else{
                    XenforoLogin.instance.logged_in.put(player.getName(), StatusType.NEED_LOGIN);
                    XenforoLogin.instance.message(player);
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
                if(XenforoLogin.instance.config.getBoolean("api.register",false)){
                    XenforoLogin.instance.logged_in.put(player.getName(), StatusType.NEED_REGISTER_EMAIL);
                }else{
                    Bukkit.getScheduler().runTask(XenforoLogin.instance, () -> player
                            .kickPlayer(XenforoLogin.instance.langFile("errors.no_user")));
                }
                return true;
            case UNKNOWN:
                Bukkit.getScheduler().runTask(XenforoLogin.instance, () -> player
                        .kickPlayer(XenforoLogin.instance.langFile("errors.unknown",
                                resultType.getInheritedObject())));
                return false;
            case SERVER_ERROR:
                Bukkit.getScheduler().runTask(XenforoLogin.instance, () -> player
                        .kickPlayer(XenforoLogin.instance.langFile("errors.server")));
                return false;
            case USER_EXIST:
                player.sendMessage(XenforoLogin.instance.langFile("errors.user_exist"));
                return false;
            case EMAIL_WRONG:
                player.sendMessage(XenforoLogin.instance.langFile("errors.email"));
                return false;
            case EMAIL_EXIST:
                player.sendMessage(XenforoLogin.instance.langFile("errors.mail_exist"));
                return false;
        }
        return false;
    }
}

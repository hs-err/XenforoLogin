package red.mohist.xenforologin.core.utils;

import red.mohist.xenforologin.core.XenforoLogin;
import red.mohist.xenforologin.core.enums.ResultType;
import red.mohist.xenforologin.core.enums.StatusType;
import red.mohist.xenforologin.core.modules.AbstractPlayer;

public class ResultTypeUtils {

    public static boolean handle(AbstractPlayer player, ResultType resultType) {
        switch (resultType) {
            case OK:
                if (resultType.isShouldLogin()) {
                    XenforoLogin.instance.login(player);
                } else {
                    XenforoLogin.instance.logged_in.put(player.uuid, StatusType.NEED_LOGIN);
                    XenforoLogin.instance.message(player);
                }
                return true;
            case PASSWORD_INCORRECT:
                player.kick(XenforoLogin.instance.langFile("errors.password"));
                return false;
            case ERROR_NAME:
                player.kick(XenforoLogin.instance.langFile("errors.name_incorrect",
                        resultType.getInheritedObject()));
                return false;
            case NO_USER:
                if (XenforoLogin.instance.api.getConfigValue("api.register", "false").equals("true")) {
                    XenforoLogin.instance.logged_in.put(player.uuid, StatusType.NEED_REGISTER_EMAIL);
                } else {
                    player.kick(XenforoLogin.instance.langFile("errors.no_user"));
                }
                return true;
            case UNKNOWN:
                player.kick(XenforoLogin.instance.langFile("errors.unknown",
                        resultType.getInheritedObject()));
                return false;
            case SERVER_ERROR:
                player.kick(XenforoLogin.instance.langFile("errors.server"));
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

package red.mohist.xenforologin.core.utils;

import red.mohist.xenforologin.core.XenforoLoginCore;
import red.mohist.xenforologin.core.enums.ResultType;
import red.mohist.xenforologin.core.enums.StatusType;
import red.mohist.xenforologin.core.modules.AbstractPlayer;

public class ResultTypeUtils {

    public static boolean handle(AbstractPlayer player, ResultType resultType) {
        switch (resultType) {
            case OK:
                if (resultType.isShouldLogin()) {
                    XenforoLoginCore.instance.login(player);
                } else {
                    XenforoLoginCore.instance.logged_in.put(player.getUniqueId(), StatusType.NEED_LOGIN);
                    XenforoLoginCore.instance.message(player);
                }
                return true;
            case PASSWORD_INCORRECT:
                player.kick(XenforoLoginCore.instance.langFile("errors.password"));
                return false;
            case ERROR_NAME:
                player.kick(XenforoLoginCore.instance.langFile("errors.name_incorrect",
                        resultType.getInheritedObject()));
                return false;
            case NO_USER:
                if ((boolean)XenforoLoginCore.instance.api.getConfigValue("api.register", false)) {
                    XenforoLoginCore.instance.logged_in.put(player.getUniqueId(), StatusType.NEED_REGISTER_EMAIL);
                } else {
                    player.kick(XenforoLoginCore.instance.langFile("errors.no_user"));
                }
                return true;
            case UNKNOWN:
                player.kick(XenforoLoginCore.instance.langFile("errors.unknown",
                        resultType.getInheritedObject()));
                return false;
            case SERVER_ERROR:
                player.kick(XenforoLoginCore.instance.langFile("errors.server"));
                return false;
            case USER_EXIST:
                player.sendMessage(XenforoLoginCore.instance.langFile("errors.user_exist"));
                return false;
            case EMAIL_WRONG:
                player.sendMessage(XenforoLoginCore.instance.langFile("errors.email"));
                return false;
            case EMAIL_EXIST:
                player.sendMessage(XenforoLoginCore.instance.langFile("errors.mail_exist"));
                return false;
        }
        return false;
    }
}

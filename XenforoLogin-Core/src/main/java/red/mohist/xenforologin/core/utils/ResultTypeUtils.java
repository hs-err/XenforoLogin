/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

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
                player.kick(Helper.langFile("errors.password"));
                return false;
            case ERROR_NAME:
                player.kick(Helper.langFile("errors.name_incorrect",
                        resultType.getInheritedObject()));
                return false;
            case NO_USER:
                if (Config.getBoolean("api.register", false)) {
                    XenforoLoginCore.instance.logged_in.put(player.getUniqueId(), StatusType.NEED_REGISTER_EMAIL);
                } else {
                    player.kick(Helper.langFile("errors.no_user"));
                }
                return true;
            case UNKNOWN:
                player.kick(Helper.langFile("errors.unknown",
                        resultType.getInheritedObject()));
                return false;
            case SERVER_ERROR:
                player.kick(Helper.langFile("errors.server"));
                return false;
            case USER_EXIST:
                player.kick(Helper.langFile("errors.user_exist"));
                return false;
            case EMAIL_WRONG:
            case EMAIL_EXIST:
                XenforoLoginCore.instance.logged_in.put(player.getUniqueId(), StatusType.NEED_REGISTER_EMAIL);
                return false;
        }
        return false;
    }
}

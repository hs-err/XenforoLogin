/*
 * Copyright 2020 Mohist-Community
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package red.mohist.sodionauth.core.utils;

import red.mohist.sodionauth.core.XenforoLoginCore;
import red.mohist.sodionauth.core.enums.ResultType;
import red.mohist.sodionauth.core.enums.StatusType;
import red.mohist.sodionauth.core.modules.AbstractPlayer;

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

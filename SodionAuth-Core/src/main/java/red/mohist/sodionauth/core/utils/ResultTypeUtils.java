/*
 * Copyright 2021 Mohist-Community
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

import red.mohist.sodionauth.core.enums.ResultType;
import red.mohist.sodionauth.core.enums.StatusType;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.services.Service;

public class ResultTypeUtils {

    public static boolean handle(AbstractPlayer player, ResultType resultType) {
        switch (resultType) {
            case OK:
                if (resultType.isShouldLogin()) {
                    Service.auth.loginAsync(player);
                } else {
                    Service.auth.logged_in.put(player.getUniqueId(), StatusType.NEED_LOGIN);
                    Service.auth.sendTip(player);
                }
                return true;
            case PASSWORD_INCORRECT:
                player.kick(player.getLang().getErrors().getPassword());
                return false;
            case ERROR_NAME:
                player.kick(player.getLang().getErrors().getNameIncorrect(
                        resultType.getInheritedObject()));
                return false;
            case NO_USER:
                if (Config.api.getAllowRegister(false)) {
                    Service.auth.logged_in.put(player.getUniqueId(), StatusType.NEED_REGISTER_EMAIL);
                } else {
                    player.kick(player.getLang().getErrors().getNoUser());
                }
                return true;
            case UNKNOWN:
                player.kick(player.getLang().getErrors().getUnknown(
                        resultType.getInheritedObject()));
                return false;
            case SERVER_ERROR:
                player.kick(player.getLang().getErrors().getServer());
                return false;
            case USER_EXIST:
                player.kick(player.getLang().getErrors().getUserExist());
                return false;
            case EMAIL_WRONG:
                player.sendMessage(player.getLang().getErrors().getEmail());
                Service.auth.logged_in.put(player.getUniqueId(), StatusType.NEED_REGISTER_EMAIL);
                return false;
            case EMAIL_EXIST:
                player.sendMessage(player.getLang().getErrors().getMailExist());
                Service.auth.logged_in.put(player.getUniqueId(), StatusType.NEED_REGISTER_EMAIL);
                return false;
        }
        return false;
    }
}

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

import red.mohist.sodionauth.core.SodionAuthCore;
import red.mohist.sodionauth.core.enums.ResultType;
import red.mohist.sodionauth.core.enums.StatusType;
import red.mohist.sodionauth.core.exception.AuthenticatedException;
import red.mohist.sodionauth.core.modules.AbstractPlayer;

public class ResultTypeUtils {

    public static boolean handle(AbstractPlayer player, ResultType resultType) {
        switch (resultType) {
            case OK:
                if (resultType.isShouldLogin()) {
                    try {
                        SodionAuthCore.instance.login(player);
                    } catch (AuthenticatedException e) {
                        e.printStackTrace();
                    }
                } else {
                    SodionAuthCore.instance.logged_in.put(player.getUniqueId(), StatusType.NEED_LOGIN);
                    SodionAuthCore.instance.message(player);
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
                    SodionAuthCore.instance.logged_in.put(player.getUniqueId(), StatusType.NEED_REGISTER_EMAIL);
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
                SodionAuthCore.instance.logged_in.put(player.getUniqueId(), StatusType.NEED_REGISTER_EMAIL);
                return false;
            case EMAIL_EXIST:
                player.sendMessage(player.getLang().getErrors().getMailExist());
                SodionAuthCore.instance.logged_in.put(player.getUniqueId(), StatusType.NEED_REGISTER_EMAIL);
                return false;
        }
        return false;
    }
}

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

import red.mohist.sodionauth.core.modules.PlayerStatus;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.services.Service;

import javax.annotation.Nonnull;

public class LoginTickPlayer {

    static final int showTipTime = Config.security.showTipsTime;
    int calledTimes = -1;
    int loginTimeout = Config.security.maxLoginTime;
    @Nonnull
    AbstractPlayer player;

    public LoginTickPlayer(@Nonnull AbstractPlayer player) {
        this.player = player;
    }


    public TickResult tick() {
        calledTimes++;
        if (calledTimes % 20 == 0
                && (!player.isOnline() || !Service.auth.needCancelled(player))) {
            return TickResult.DONE;
        }
        if (calledTimes % 20 == 0
                && !Service.auth.logged_in.containsKey(player.getUniqueId())) {
            ;
            Helper.getLogger().info("Player " + player.getName() + " haven't been checked.");
            return TickResult.DONE;
        }
        PlayerStatus status = Service.auth.logged_in.get(player.getUniqueId());
        if (calledTimes / 20 > loginTimeout) {
            if (status == null
                    || Service.auth.logged_in.get(player.getUniqueId()).type.equals(PlayerStatus.StatusType.NEED_LOGIN)) {
                player.kick(player.getLang().errors.timeOut);
                return TickResult.DONE;
            }
        }
        if (calledTimes % ((showTipTime * 20)) == 0) {
            Service.auth.sendTip(player);
        }
        return TickResult.CONTINUE;
    }

    public enum TickResult {
        DONE, CONTINUE
    }


}

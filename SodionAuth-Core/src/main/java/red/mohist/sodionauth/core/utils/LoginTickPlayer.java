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
import red.mohist.sodionauth.core.authbackends.AuthBackendSystems;
import red.mohist.sodionauth.core.enums.StatusType;
import red.mohist.sodionauth.core.modules.AbstractPlayer;

import javax.annotation.Nonnull;

public class LoginTickPlayer {

    static final int showTipTime = Config.getInteger("secure.show_tips_time", 5);
    long startTime = System.currentTimeMillis();
    int loginTimeout = Config.getInteger("secure.max_login_time", 30);
    @Nonnull
    AbstractPlayer player;

    public LoginTickPlayer(@Nonnull AbstractPlayer player) {
        this.player = player;
    }


    public TickResult tick() {
        long now = System.currentTimeMillis();
        if (!XenforoLoginCore.instance.logged_in.containsKey(player.getUniqueId())) {
            boolean result = ResultTypeUtils.handle(player,
                    AuthBackendSystems.getCurrentSystem()
                            .join(player)
                            .shouldLogin(false));
            if (!result) {
                Helper.getLogger().warn(
                        player.getName() + " didn't pass AccountExists test");
                return TickResult.DONE;
            }
            XenforoLoginCore.instance.message(player);
            XenforoLoginCore.instance.api.sendBlankInventoryPacket(player);
        }
        if ((now - startTime) / 1000 > loginTimeout
                && XenforoLoginCore.instance.logged_in.get(player.getUniqueId()) == StatusType.NEED_LOGIN) {
            player.kick(Helper.langFile("errors.time_out"));
            return TickResult.DONE;
        }
        if (!player.isOnline() || !XenforoLoginCore.instance.needCancelled(player)) {
            return TickResult.DONE;
        }

        XenforoLoginCore.instance.message(player);
        XenforoLoginCore.instance.api.sendBlankInventoryPacket(player);
        return TickResult.CONTINUE;
    }

    public enum TickResult {
        DONE, CONTINUE
    }


}

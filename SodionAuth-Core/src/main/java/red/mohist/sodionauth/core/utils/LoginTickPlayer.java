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
import red.mohist.sodionauth.core.authbackends.AuthBackendSystems;
import red.mohist.sodionauth.core.enums.StatusType;
import red.mohist.sodionauth.core.modules.AbstractPlayer;

import javax.annotation.Nonnull;

public class LoginTickPlayer {

    static final int showTipTime = Config.security.getShowTipsTime();
    long startTime = System.currentTimeMillis();
    int loginTimeout = Config.security.getMaxLoginTime();
    @Nonnull
    AbstractPlayer player;

    public LoginTickPlayer(@Nonnull AbstractPlayer player) {
        this.player = player;
    }


    public TickResult tick() {
        long now = System.currentTimeMillis();
        if (!SodionAuthCore.instance.logged_in.containsKey(player.getUniqueId())) {
            boolean result = ResultTypeUtils.handle(player,
                    AuthBackendSystems.getCurrentSystem()
                            .join(player)
                            .shouldLogin(false));
            if (!result) {
                Helper.getLogger().warn(
                        player.getName() + " didn't pass AccountExists test");
                return TickResult.DONE;
            }
            SodionAuthCore.instance.message(player);
            SodionAuthCore.instance.api.sendBlankInventoryPacket(player);
        }
        if ((now - startTime) / 1000 > loginTimeout
                && SodionAuthCore.instance.logged_in.get(player.getUniqueId()) == StatusType.NEED_LOGIN) {
            player.kick(player.getLang().getErrors().getTimeOut());
            return TickResult.DONE;
        }
        if (!player.isOnline() || !SodionAuthCore.instance.needCancelled(player)) {
            return TickResult.DONE;
        }

        SodionAuthCore.instance.message(player);
        SodionAuthCore.instance.api.sendBlankInventoryPacket(player);
        return TickResult.CONTINUE;
    }

    public enum TickResult {
        DONE, CONTINUE
    }


}

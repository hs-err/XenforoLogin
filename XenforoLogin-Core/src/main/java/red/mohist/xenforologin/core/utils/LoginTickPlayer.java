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
import red.mohist.xenforologin.core.enums.StatusType;
import red.mohist.xenforologin.core.forums.ForumSystems;
import red.mohist.xenforologin.core.modules.AbstractPlayer;

import javax.annotation.Nonnull;

public class LoginTickPlayer {

    static final int showTipTime = Config.getInteger("secure.show_tips_time", 5);
    long lastTipTime = 0;
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
                    ForumSystems.getCurrentSystem()
                            .join(player)
                            .shouldLogin(false));
            if (!result) {
                XenforoLoginCore.instance.api.getLogger().warning(
                        player.getName() + " didn't pass AccountExists test");
                return TickResult.DONE;
            }
            XenforoLoginCore.instance.message(player);
        }
        if ((now - startTime) / 1000 > loginTimeout
                && XenforoLoginCore.instance.logged_in.get(player.getUniqueId()) == StatusType.NEED_LOGIN) {
            player.kick(Helper.langFile("errors.time_out"));
            return TickResult.DONE;
        }
        if (!player.isOnline() || !XenforoLoginCore.instance.needCancelled(player)) {
            return TickResult.DONE;
        }
        if ((now - lastTipTime) / 1000 > showTipTime) {
            lastTipTime = now;
            XenforoLoginCore.instance.message(player);
            XenforoLoginCore.instance.api.sendBlankInventoryPacket(player);
        }
        return TickResult.CONTINUE;
    }

    public enum TickResult {
        DONE, CONTINUE
    }


}

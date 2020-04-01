/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.core.utils;

import com.google.common.collect.Sets;
import red.mohist.xenforologin.core.modules.AbstractPlayer;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class LoginTicker {

    public static TimerTask task = null;
    private static Set<LoginTickPlayer> tickers = Sets.newConcurrentHashSet();

    public static void register() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                LoginTicker.run();
            }
        }, 1000, LoginTickPlayer.showTipTime);
    }

    public static void add(AbstractPlayer player) {
        tickers.add(new LoginTickPlayer(player));
    }

    public static void run() {
        tickers.removeIf(current -> current.tick() == LoginTickPlayer.TickResult.DONE);
    }

    public static void unregister() {
        if (task != null)
            task.cancel();
        task = null;
    }
}

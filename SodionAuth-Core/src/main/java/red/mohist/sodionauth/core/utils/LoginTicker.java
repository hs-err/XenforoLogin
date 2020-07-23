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

import com.google.common.collect.Sets;
import red.mohist.sodionauth.core.modules.AbstractPlayer;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class LoginTicker {

    private static final Set<LoginTickPlayer> tickers = Sets.newConcurrentHashSet();
    public static TimerTask task = null;
    private static Timer timer;

    public static void register() {
        task = new TimerTask() {
            @Override
            public void run() {
                LoginTicker.run();
            }
        };
        timer = new Timer();
        timer.schedule(task, 0, LoginTickPlayer.showTipTime * 1000);
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
        if (timer != null)
            timer.cancel();
        timer = null;
    }
}

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

package red.mohist.sodionauth.bukkit.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import red.mohist.sodionauth.bukkit.implementation.BukkitPlayer;
import red.mohist.sodionauth.bukkit.interfaces.BukkitAPIListener;
import red.mohist.sodionauth.core.SodionAuthCore;
import red.mohist.sodionauth.core.modules.AbstractPlayer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ListenerAsyncPlayerPreLoginEvent implements BukkitAPIListener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }

        AbstractPlayer abstractPlayer = new BukkitPlayer(
                event.getName(), event.getUniqueId(), event.getAddress());
        if (!SodionAuthCore.instance.isEnabled()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, abstractPlayer.getLang().getErrors().getServer());
            return;
        }

        if (Bukkit.getPlayerExact(event.getName()) != null) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, abstractPlayer.getLang().getErrors().getLoginExist());
            return;
        }

        if (!SodionAuthCore.instance.logged_in.containsKey(event.getUniqueId())) {
            String canLogin = SodionAuthCore.instance.canLogin(abstractPlayer);
            if (canLogin != null) {
                SodionAuthCore.instance.logged_in.remove(abstractPlayer.getUniqueId());
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, canLogin);
                return;
            }
        }

        SodionAuthCore.instance.canJoinAsync(abstractPlayer).thenWithException((Future<String> future)->{
            String canjoin;
            try {
                canjoin = future.get();
            } catch (InterruptedException | ExecutionException e) {
                SodionAuthCore.instance.logged_in.remove(abstractPlayer.getUniqueId());
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, abstractPlayer.getLang().getErrors().getServer());
                e.printStackTrace();
                return;
            }
            if (canjoin != null) {
                SodionAuthCore.instance.logged_in.remove(abstractPlayer.getUniqueId());
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, canjoin);
            }
        });
    }

    @Override
    public void eventClass() {
        AsyncPlayerPreLoginEvent.class.getName();
    }
}

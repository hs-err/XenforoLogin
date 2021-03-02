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
import org.bukkit.event.player.PlayerPreLoginEvent;
import red.mohist.sodionauth.bukkit.implementation.BukkitPlayer;
import red.mohist.sodionauth.bukkit.interfaces.BukkitAPIListener;
import red.mohist.sodionauth.core.SodionAuthCore;
import red.mohist.sodionauth.core.events.player.CanJoinEvent;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.utils.Helper;

public class ListenerPlayerPreLoginEvent implements BukkitAPIListener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerPreLoginEvent(PlayerPreLoginEvent event) {
        if (event.getResult() != PlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }
        AbstractPlayer player = new BukkitPlayer(
                event.getName(), event.getUniqueId(), event.getAddress());
        if (!SodionAuthCore.instance.isEnabled()) {
            event.setKickMessage(player.getLang().getErrors().getServer());
            return;
        }
        if (Bukkit.getPlayerExact(event.getName()) != null) {
            new Exception().printStackTrace();
            event.setKickMessage(player.getLang().getErrors().getLoginExist());
            return;
        }
        if (!SodionAuthCore.instance.logged_in.containsKey(player.getUniqueId())) {
            Helper.getLogger().warn("AsyncPlayerPreLoginEvent isn't active. It may cause some security problems.");
            Helper.getLogger().warn("It's not a bug. Do NOT report this.");
            CanJoinEvent canJoinEvent = new CanJoinEvent(player);
            if(!canJoinEvent.syncPost()){
                event.disallow(PlayerPreLoginEvent.Result.KICK_WHITELIST, canJoinEvent.getMessage());
            }
        }
    }

    @Override
    public void eventClass() {
        PlayerPreLoginEvent.class.getName();
    }
}

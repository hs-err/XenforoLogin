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

package red.mohist.sodionauth.bukkit.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import red.mohist.sodionauth.bukkit.BukkitLoader;
import red.mohist.sodionauth.bukkit.implementation.BukkitPlayer;
import red.mohist.sodionauth.bukkit.interfaces.BukkitAPIListener;
import red.mohist.sodionauth.core.SodionAuthCore;
import red.mohist.sodionauth.core.events.player.CanJoinEvent;
import red.mohist.sodionauth.core.events.player.JoinEvent;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.services.Service;
import red.mohist.sodionauth.core.utils.Helper;

import java.nio.charset.StandardCharsets;

public class ListenerPlayerJoinEvent implements BukkitAPIListener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void OnPlayerJoinEvent(PlayerJoinEvent event) {
        AbstractPlayer player = new BukkitPlayer(event.getPlayer());
        if (!SodionAuthCore.instance.isEnabled()) {
            event.getPlayer().kickPlayer(player.getLang().getErrors().getServer());
        }

        SodionAuthCore.instance.api.sendBlankInventoryPacket(player);
        if (!Service.auth.logged_in.containsKey(player.getUniqueId())) {

            Helper.getLogger().warn("AsyncPlayerPreLoginEvent and PlayerPreLoginEvent isn't active. It may cause some security problems.");
            Helper.getLogger().warn("It's not a bug. Do NOT report this.");
            CanJoinEvent canJoinEvent = new CanJoinEvent(player);
            if (!canJoinEvent.syncPost()) {
                player.kick(canJoinEvent.getMessage());
            }
        }
        JoinEvent joinEvent = new JoinEvent(player);
        if (!joinEvent.syncPost()) {
            player.kick(joinEvent.getMessage());
        }
        //event.setJoinMessage(null);
    }

    @Override
    public void eventClass() {
        PlayerJoinEvent.class.getName();
    }
}

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

package red.mohist.sodionauth.bungee.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import red.mohist.sodionauth.bungee.implementation.BungeePlayer;
import red.mohist.sodionauth.bungee.interfaces.BungeeAPIListener;
import red.mohist.sodionauth.core.SodionAuthCore;
import red.mohist.sodionauth.core.events.player.CanJoinEvent;
import red.mohist.sodionauth.core.events.player.JoinEvent;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.services.Service;

public class ListenerPostLoginEvent implements BungeeAPIListener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPostLoginEvent(PostLoginEvent event) {
        ProxiedPlayer bungeePlayer = event.getPlayer();
        AbstractPlayer player = new BungeePlayer(bungeePlayer);
        if (!SodionAuthCore.instance.isEnabled()) {
            player.kick(player.getLang().errors.server);
        }

        if (!Service.auth.logged_in.containsKey(player.getUniqueId())) {
            CanJoinEvent canJoinEvent = new CanJoinEvent(player);
            if (!canJoinEvent.post()) {
                player.kick(canJoinEvent.getMessage());
            }
        }
        JoinEvent joinEvent = new JoinEvent(player);
        if (!joinEvent.post()) {
            player.kick(joinEvent.getMessage());
        }
    }

    @Override
    public void eventClass() {
        PostLoginEvent.class.getName();
    }
}
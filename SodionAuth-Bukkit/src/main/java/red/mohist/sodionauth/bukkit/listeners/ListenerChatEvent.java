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
import org.bukkit.event.player.AsyncPlayerChatEvent;
import red.mohist.sodionauth.bukkit.implementation.BukkitPlayer;
import red.mohist.sodionauth.bukkit.interfaces.BukkitAPIListener;
import red.mohist.sodionauth.core.events.player.PlayerChatEvent;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.services.Service;
import red.mohist.sodionauth.core.utils.Config;

public class ListenerChatEvent implements BukkitAPIListener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        AbstractPlayer player = new BukkitPlayer(event.getPlayer());
        if (!event.isCancelled()) {
            if (!Service.auth.needCancelled(player)) {
                if (Config.security.cancelChatAfterLogin) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
            if (!new PlayerChatEvent(player, event.getMessage()).syncPost()) {
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void eventClass() {
        AsyncPlayerChatEvent.class.getName();
    }
}

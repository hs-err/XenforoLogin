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

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import red.mohist.sodionauth.bukkit.BukkitLoader;
import red.mohist.sodionauth.bukkit.interfaces.BukkitAPIListener;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.services.Service;
import red.mohist.sodionauth.core.utils.Config;


public class ListenerPlayerMoveEvent implements BukkitAPIListener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnMove(PlayerMoveEvent event) {
        final AbstractPlayer player = BukkitLoader.instance.player2info(event.getPlayer());
        if (Service.auth.needCancelled(player)) {
            Location location = event.getTo();
            if (Config.teleport.tpSpawnBeforeLogin) {
                location.setX(Service.auth.default_location.x);
                if (Config.security.spectatorLogin) {
                    location.setY(Service.auth.default_location.y);
                }
                location.setZ(Service.auth.default_location.z);
            } else {
                Location back = event.getFrom();
                location.setX(back.getX());
                if (Config.security.spectatorLogin) {
                    location.setY(back.getY());
                }
                location.setZ(back.getZ());
            }
            event.setTo(location);
        }
    }

    @Override
    public void eventClass() {
        PlayerMoveEvent.class.getName();
    }
}

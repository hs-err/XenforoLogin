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

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import red.mohist.sodionauth.bukkit.BukkitLoader;
import red.mohist.sodionauth.bukkit.interfaces.BukkitAPIListener;
import red.mohist.sodionauth.core.XenforoLoginCore;

public class ListenerEntityDamageByEntityEvent implements BukkitAPIListener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            if (XenforoLoginCore.instance.needCancelled(BukkitLoader.instance.player2info((Player) event.getEntity()))) {
                event.setCancelled(true);
            }
        }
        if (event.getDamager().getType() == EntityType.PLAYER) {
            if (XenforoLoginCore.instance.needCancelled(BukkitLoader.instance.player2info((Player) event.getDamager()))) {
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void eventClass() {
        EntityDamageByEntityEvent.class.getName();
    }
}

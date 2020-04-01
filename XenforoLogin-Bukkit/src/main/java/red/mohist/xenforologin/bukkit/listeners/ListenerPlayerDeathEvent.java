/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.bukkit.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import red.mohist.xenforologin.bukkit.BukkitLoader;
import red.mohist.xenforologin.bukkit.interfaces.BukkitAPIListener;
import red.mohist.xenforologin.core.XenforoLoginCore;

public class ListenerPlayerDeathEvent implements BukkitAPIListener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnPlayerDeathEvent(PlayerDeathEvent event) {
        if (XenforoLoginCore.instance.needCancelled(BukkitLoader.instance.player2info(event.getEntity()))) {
            event.setCancelled(true);
        }
    }

    @Override
    public void eventClass() {
        PlayerDeathEvent.class.getName();
    }
}

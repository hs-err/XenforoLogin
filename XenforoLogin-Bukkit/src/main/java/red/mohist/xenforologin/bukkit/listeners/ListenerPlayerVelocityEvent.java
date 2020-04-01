/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.bukkit.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerVelocityEvent;
import red.mohist.xenforologin.bukkit.BukkitLoader;
import red.mohist.xenforologin.bukkit.interfaces.BukkitAPIListener;
import red.mohist.xenforologin.core.XenforoLoginCore;

public class ListenerPlayerVelocityEvent implements BukkitAPIListener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnPlayerVelocityEvent(PlayerVelocityEvent event) {
        if (XenforoLoginCore.instance.needCancelled(BukkitLoader.instance.player2info(event.getPlayer()))) {
            event.setCancelled(true);
        }
    }

    @Override
    public void eventClass() {
        PlayerVelocityEvent.class.getName();
    }
}

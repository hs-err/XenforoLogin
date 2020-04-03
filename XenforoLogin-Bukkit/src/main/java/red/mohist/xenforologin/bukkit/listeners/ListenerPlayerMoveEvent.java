/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.bukkit.listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import red.mohist.xenforologin.bukkit.BukkitLoader;
import red.mohist.xenforologin.bukkit.interfaces.BukkitAPIListener;
import red.mohist.xenforologin.core.XenforoLoginCore;


public class ListenerPlayerMoveEvent implements BukkitAPIListener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnMove(PlayerMoveEvent event) {
        if (XenforoLoginCore.instance.needCancelled(BukkitLoader.instance.player2info(event.getPlayer()))) {
            if ((boolean) BukkitLoader.instance.getConfigValue("teleport.tp_spawn_before_login", true)) {
                Location location = event.getTo();
                location.setX(XenforoLoginCore.instance.default_location.x);
                if ((boolean) BukkitLoader.instance.getConfigValue("secure.spectator_login", true)) {
                    location.setY(XenforoLoginCore.instance.default_location.y);
                }
                location.setZ(XenforoLoginCore.instance.default_location.z);
                event.setTo(location);
            } else {
                Location back = event.getFrom();
                Location location = event.getTo();
                location.setX(back.getX());
                if ((boolean) BukkitLoader.instance.getConfigValue("secure.spectator_login", true)) {
                    location.setY(back.getY());
                }
                location.setZ(back.getZ());
                event.setTo(location);
            }
        }
    }

    @Override
    public void eventClass() {
        PlayerMoveEvent.class.getName();
    }
}

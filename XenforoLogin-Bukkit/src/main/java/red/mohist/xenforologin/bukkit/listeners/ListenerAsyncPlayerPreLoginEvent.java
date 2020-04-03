/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.bukkit.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import red.mohist.xenforologin.bukkit.implementation.BukkitPlainPlayer;
import red.mohist.xenforologin.bukkit.interfaces.BukkitAPIListener;
import red.mohist.xenforologin.core.XenforoLoginCore;

public class ListenerAsyncPlayerPreLoginEvent implements BukkitAPIListener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {
        String canjoin = XenforoLoginCore.instance.canJoin(new BukkitPlainPlayer(
                event.getName(), event.getUniqueId(), event.getAddress()));
        if (canjoin != null) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, canjoin);
        }
    }

    @Override
    public void eventClass() {
        AsyncPlayerPreLoginEvent.class.getName();
    }
}

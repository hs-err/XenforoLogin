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
import org.bukkit.event.player.PlayerJoinEvent;
import red.mohist.xenforologin.bukkit.BukkitLoader;
import red.mohist.xenforologin.bukkit.interfaces.BukkitAPIListener;
import red.mohist.xenforologin.core.XenforoLoginCore;
import red.mohist.xenforologin.core.modules.AbstractPlayer;

public class ListenerPlayerJoinEvent implements BukkitAPIListener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void OnPlayerJoinEvent(PlayerJoinEvent event) {
        AbstractPlayer abstractPlayer=BukkitLoader.instance.player2info(event.getPlayer());
        new Thread(() -> {
            String canjoin = XenforoLoginCore.instance.canJoin(abstractPlayer);
            if (canjoin != null) {
                abstractPlayer.kick(canjoin);
            }
        }).start();
        XenforoLoginCore.instance.onJoin(abstractPlayer);
    }

    @Override
    public void eventClass() {
        PlayerJoinEvent.class.getName();
    }
}

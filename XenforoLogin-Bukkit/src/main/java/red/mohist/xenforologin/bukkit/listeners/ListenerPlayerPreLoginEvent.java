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
import org.bukkit.event.player.PlayerPreLoginEvent;
import red.mohist.xenforologin.bukkit.implementation.BukkitPlainPlayer;
import red.mohist.xenforologin.bukkit.interfaces.BukkitAPIListener;
import red.mohist.xenforologin.core.XenforoLoginCore;
import red.mohist.xenforologin.core.modules.AbstractPlayer;

public class ListenerPlayerPreLoginEvent implements BukkitAPIListener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerPreLoginEvent(PlayerPreLoginEvent event) {
        if(event.getResult() != PlayerPreLoginEvent.Result.ALLOWED){
            return;
        }
        AbstractPlayer abstractPlayer = new BukkitPlainPlayer(
                event.getName(), event.getUniqueId(), event.getAddress());

        if (!XenforoLoginCore.instance.logged_in.containsKey(event.getUniqueId())) {
            String canLogin = XenforoLoginCore.instance.canLogin(abstractPlayer);
            if (canLogin != null) {
                event.setKickMessage(canLogin);
                XenforoLoginCore.instance.logged_in.remove(abstractPlayer.getUniqueId());
            }
        }
    }
    @Override
    public void eventClass() {
        PlayerPreLoginEvent.class.getName();
    }
}

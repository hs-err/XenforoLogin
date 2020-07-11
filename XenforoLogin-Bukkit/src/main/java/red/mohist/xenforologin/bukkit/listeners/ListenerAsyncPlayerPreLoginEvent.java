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
import red.mohist.xenforologin.core.modules.AbstractPlayer;
import red.mohist.xenforologin.core.utils.Helper;

import java.util.concurrent.ExecutionException;

public class ListenerAsyncPlayerPreLoginEvent implements BukkitAPIListener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {
        if(event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED){
            return;
        }
        AbstractPlayer abstractPlayer = new BukkitPlainPlayer(
                event.getName(), event.getUniqueId(), event.getAddress());
        if (!XenforoLoginCore.instance.logged_in.containsKey(event.getUniqueId())) {
            String canLogin = XenforoLoginCore.instance.canLogin(abstractPlayer);
            if (canLogin != null) {
                XenforoLoginCore.instance.logged_in.remove(abstractPlayer.getUniqueId());
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST,canLogin);
                return;
            }
        }

        String canjoin;
        try {
            canjoin = XenforoLoginCore.instance.canJoin(abstractPlayer).get();
        } catch (InterruptedException | ExecutionException e) {
            XenforoLoginCore.instance.logged_in.remove(abstractPlayer.getUniqueId());
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, Helper.langFile("errors.server"));
            e.printStackTrace();
            return;
        }
        if (canjoin != null) {
            XenforoLoginCore.instance.logged_in.remove(abstractPlayer.getUniqueId());
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, canjoin);
        }
    }
    @Override
    public void eventClass() {
        AsyncPlayerPreLoginEvent.class.getName();
    }
}

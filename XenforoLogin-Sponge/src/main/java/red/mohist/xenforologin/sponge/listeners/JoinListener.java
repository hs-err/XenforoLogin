/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.sponge.listeners;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.network.ClientConnectionEvent.Join;
import org.spongepowered.api.text.Text;
import red.mohist.xenforologin.core.XenforoLoginCore;
import red.mohist.xenforologin.core.utils.Helper;
import red.mohist.xenforologin.sponge.implementation.SpongePlayer;
import red.mohist.xenforologin.sponge.interfaces.SpongeAPIListener;

public class JoinListener implements SpongeAPIListener {
    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onJoinEvent(Join event, @First Player spongePlayer) {
        SpongePlayer player=new SpongePlayer(spongePlayer);
        XenforoLoginCore.instance.api.sendBlankInventoryPacket(player);
        if (!XenforoLoginCore.instance.logged_in.containsKey(spongePlayer.getUniqueId())) {
            String canLogin = XenforoLoginCore.instance.canLogin(player);
            if (canLogin != null) {
                spongePlayer.kick(Text.of(canLogin));
                return;
            }

            Helper.getLogger().warn("onAsyncPlayerPreLoginEvent isn't active. It may cause some security problems.");
            Helper.getLogger().warn("It's not a bug. Do NOT report this.");
            new Thread(() -> {
                String canjoin = XenforoLoginCore.instance.canJoin(player);
                if (canjoin != null) {
                    player.kick(canjoin);
                }
            }).start();
        }
        XenforoLoginCore.instance.onJoin(player);
    }

    @Override
    public void eventClass() {
        Join.class.getName();
    }
}

/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.sponge.listeners;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.network.ClientConnectionEvent.Auth;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;
import red.mohist.xenforologin.core.XenforoLoginCore;
import red.mohist.xenforologin.sponge.implementation.SpongePlainPlayer;
import red.mohist.xenforologin.sponge.interfaces.SpongeAPIListener;

public class AuthListener implements SpongeAPIListener {
    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onAsyncPlayerPreLoginEvent(Auth event, @First GameProfile profile) {
        String canLogin = XenforoLoginCore.instance.canLogin(new SpongePlainPlayer(
                event.getProfile().getName().get(), event.getProfile().getUniqueId(), event.getConnection().getAddress().getAddress()));
        if (canLogin != null) {
            event.setMessage(Text.of(canLogin));
            event.setCancelled(true);
            return;
        }
        String canjoin = XenforoLoginCore.instance.canJoin(new SpongePlainPlayer(
                event.getProfile().getName().get(), event.getProfile().getUniqueId(), event.getConnection().getAddress().getAddress()));
        if (canjoin != null) {
            event.setMessage(Text.of(canjoin));
            event.setCancelled(true);
        }
    }

    @Override
    public void eventClass() {
        Auth.class.getName();
    }
}

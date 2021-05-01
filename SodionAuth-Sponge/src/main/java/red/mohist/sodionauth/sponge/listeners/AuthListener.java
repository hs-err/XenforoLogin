/*
 * Copyright 2021 Mohist-Community
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

package red.mohist.sodionauth.sponge.listeners;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.network.ClientConnectionEvent.Auth;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;
import red.mohist.sodionauth.core.SodionAuthCore;
import red.mohist.sodionauth.core.events.player.CanJoinEvent;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.services.Service;
import red.mohist.sodionauth.sponge.implementation.SpongePlayer;
import red.mohist.sodionauth.sponge.interfaces.SpongeAPIListener;

public class AuthListener implements SpongeAPIListener {
    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onAuthEvent(Auth event, @First GameProfile profile) {
        AbstractPlayer player = new SpongePlayer(
                event.getProfile().getName().get(),
                event.getProfile().getUniqueId(),
                event.getConnection().getAddress().getAddress());
        if (!SodionAuthCore.instance.isEnabled()) {
            event.setMessage(Text.of(player.getLang().errors.server));
            event.setCancelled(true);
            return;
        }
        if (Sponge.getServer().getPlayer(player.getName()).isPresent()) {
            event.setMessage(Text.of(player.getLang().errors.loginExist));
            event.setCancelled(true);
            return;
        }

        if (!Service.auth.logged_in.containsKey(player.getUniqueId())) {
            Service.auth.logged_in.remove(player.getUniqueId());
        }

        CanJoinEvent canJoinEvent = new CanJoinEvent(player);
        if (!canJoinEvent.post()) {
            player.kick(canJoinEvent.getMessage());
        }
    }

    @Override
    public void eventClass() {
        Auth.class.getName();
    }
}

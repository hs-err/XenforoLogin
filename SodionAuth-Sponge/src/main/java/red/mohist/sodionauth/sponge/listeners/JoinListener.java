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

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.network.ClientConnectionEvent.Join;
import red.mohist.sodionauth.core.SodionAuthCore;
import red.mohist.sodionauth.core.events.player.CanJoinEvent;
import red.mohist.sodionauth.core.events.player.JoinEvent;
import red.mohist.sodionauth.core.services.Service;
import red.mohist.sodionauth.core.utils.Helper;
import red.mohist.sodionauth.sponge.implementation.SpongePlayer;
import red.mohist.sodionauth.sponge.interfaces.SpongeAPIListener;

public class JoinListener implements SpongeAPIListener {
    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onJoinEvent(Join event, @First Player spongePlayer) {
        SpongePlayer player = new SpongePlayer(spongePlayer);
        if (!SodionAuthCore.instance.isEnabled()) {
            player.kick(player.getLang().getErrors().getServer());
        }
        SodionAuthCore.instance.api.sendBlankInventoryPacket(player);
        if (!Service.auth.logged_in.containsKey(spongePlayer.getUniqueId())) {
            CanJoinEvent canJoinEvent = new CanJoinEvent(player);
            if (!canJoinEvent.syncPost()) {
                player.kick(canJoinEvent.getMessage());
            }

            Helper.getLogger().warn("onAuthEvent isn't active. It may cause some security problems.");
            Helper.getLogger().warn("It's not a bug. Do NOT report this.");
        }
        new JoinEvent(player).post();
    }

    @Override
    public void eventClass() {
        Join.class.getName();
    }
}

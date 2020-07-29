/*
 * Copyright 2020 Mohist-Community
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
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.sponge.implementation.SpongePlayer;
import red.mohist.sodionauth.sponge.interfaces.SpongeAPIListener;

public class AuthListener implements SpongeAPIListener {
    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onAuthEvent(Auth event, @First GameProfile profile) {
        AbstractPlayer abstractPlayer = new SpongePlayer(
                event.getProfile().getName().get(),
                event.getProfile().getUniqueId(),
                event.getConnection().getAddress().getAddress());
        if (!SodionAuthCore.instance.isEnabled()) {
            event.setMessage(Text.of(abstractPlayer.getLang().getErrors().getServer()));
            event.setCancelled(true);
            return;
        }
        if (Sponge.getServer().getPlayer(abstractPlayer.getName()).isPresent()) {
            event.setMessage(Text.of(abstractPlayer.getLang().getErrors().getLoginExist()));
            event.setCancelled(true);
            return;
        }
        String canLogin = SodionAuthCore.instance.canLogin(abstractPlayer);
        if (canLogin != null) {
            event.setMessage(Text.of(canLogin));
            event.setCancelled(true);
            return;
        }
        SodionAuthCore.instance.canJoin(abstractPlayer).thenAccept(result -> {
            if (result != null) {
                abstractPlayer.kick(result);
            }
        });
    }

    @Override
    public void eventClass() {
        Auth.class.getName();
    }
}

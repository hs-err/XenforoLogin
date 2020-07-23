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

package red.mohist.sodionauth.bukkit.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import red.mohist.sodionauth.bukkit.BukkitLoader;
import red.mohist.sodionauth.bukkit.interfaces.BukkitAPIListener;
import red.mohist.sodionauth.core.SodionAuthCore;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.utils.Helper;

public class ListenerPlayerJoinEvent implements BukkitAPIListener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void OnPlayerJoinEvent(PlayerJoinEvent event) {
        AbstractPlayer abstractPlayer = BukkitLoader.instance.player2info(event.getPlayer());
        SodionAuthCore.instance.api.sendBlankInventoryPacket(abstractPlayer);
        if (!SodionAuthCore.instance.logged_in.containsKey(abstractPlayer.getUniqueId())) {

            String canLogin = SodionAuthCore.instance.canLogin(abstractPlayer);
            if (canLogin != null) {
                SodionAuthCore.instance.logged_in.remove(abstractPlayer.getUniqueId());
                event.getPlayer().kickPlayer(canLogin);
                return;
            }

            Helper.getLogger().warn("AsyncPlayerPreLoginEvent isn't active. It may cause some security problems.");
            Helper.getLogger().warn("It's not a bug. Do NOT report this.");
            SodionAuthCore.instance.canJoin(abstractPlayer).thenAccept(result -> {
                if (result != null) {
                    abstractPlayer.kick(result);
                }
            });
        }
        SodionAuthCore.instance.onJoin(abstractPlayer);
    }

    @Override
    public void eventClass() {
        PlayerJoinEvent.class.getName();
    }
}

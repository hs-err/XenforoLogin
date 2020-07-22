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
import org.bukkit.event.player.PlayerPreLoginEvent;
import red.mohist.sodionauth.bukkit.implementation.BukkitPlainPlayer;
import red.mohist.sodionauth.bukkit.interfaces.BukkitAPIListener;
import red.mohist.sodionauth.core.XenforoLoginCore;
import red.mohist.sodionauth.core.modules.AbstractPlayer;

public class ListenerPlayerPreLoginEvent implements BukkitAPIListener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerPreLoginEvent(PlayerPreLoginEvent event) {
        if (event.getResult() != PlayerPreLoginEvent.Result.ALLOWED) {
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

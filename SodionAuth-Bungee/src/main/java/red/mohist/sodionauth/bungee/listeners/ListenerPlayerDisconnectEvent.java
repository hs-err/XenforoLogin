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

package red.mohist.sodionauth.bungee.listeners;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.event.EventHandler;
import red.mohist.sodionauth.bungee.implementation.BungeePlayer;
import red.mohist.sodionauth.bungee.interfaces.BungeeAPIListener;
import red.mohist.sodionauth.core.events.player.QuitEvent;
import red.mohist.sodionauth.core.modules.AbstractPlayer;

public class ListenerPlayerDisconnectEvent implements BungeeAPIListener {
    @EventHandler
    public void onPlayerDisconnectEvent(PlayerDisconnectEvent event){
        AbstractPlayer player=new BungeePlayer(event.getPlayer());
        new QuitEvent(player).post();
    }
    @Override
    public void eventClass() {
        PostLoginEvent.class.getName();
    }
}
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

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.event.EventHandler;
import red.mohist.sodionauth.bungee.implementation.BungeePlayer;
import red.mohist.sodionauth.bungee.interfaces.BungeeAPIListener;
import red.mohist.sodionauth.core.events.player.ClientMessageEvent;
import red.mohist.sodionauth.core.events.player.ServerMessageEvent;


public class ListenerPluginMessageEvent implements BungeeAPIListener {
    @EventHandler
    public void onPluginMessageEvent(PluginMessageEvent event) {
        if (event.getSender() instanceof ProxiedPlayer) {
            new ClientMessageEvent(
                    event.getTag(),
                    event.getData(),
                    new BungeePlayer((ProxiedPlayer) event.getSender())).post();
        } else if (event.getSender() instanceof Server
                && event.getReceiver() instanceof ProxiedPlayer) {
            new ServerMessageEvent(
                    event.getTag(),
                    event.getData(),
                    new BungeePlayer((ProxiedPlayer) event.getReceiver())).post();
        }
    }

    @Override
    public void eventClass() {
        PluginMessageEvent.class.getName();
    }
}

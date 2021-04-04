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

import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import red.mohist.sodionauth.bungee.interfaces.BungeeAPIListener;
import red.mohist.sodionauth.core.services.Service;
import red.mohist.sodionauth.core.utils.channel.proxy.ProxyChannel;
import red.mohist.sodionauth.core.utils.channel.proxy.clientPacket.HelloServerPacket;

public class ListenerServerSwitchEvent implements BungeeAPIListener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerSwitchEvent(ServerSwitchEvent event){
        Service.proxyLogin.serverToken.remove(event.getPlayer().getUniqueId());
        event.getPlayer().getServer().sendData(
                ProxyChannel.name,
                new HelloServerPacket().pack());
    }
    @Override
    public void eventClass() {
        PostLoginEvent.class.getName();
    }
}
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

package red.mohist.sodionauth.core.services;

import com.google.common.eventbus.Subscribe;
import red.mohist.sodionauth.core.SodionAuthCore;
import red.mohist.sodionauth.core.enums.StatusType;
import red.mohist.sodionauth.core.events.player.*;
import red.mohist.sodionauth.core.utils.Helper;
import red.mohist.sodionauth.core.utils.channel.proxy.ProxyChannel;
import red.mohist.sodionauth.core.utils.channel.proxy.clientPacket.ClientPacket;
import red.mohist.sodionauth.core.utils.channel.proxy.clientPacket.HelloServerPacket;
import red.mohist.sodionauth.core.utils.channel.proxy.clientPacket.LoginSuccessPacket;
import red.mohist.sodionauth.core.utils.channel.proxy.serverPacket.ServerPacket;
import red.mohist.sodionauth.core.utils.channel.proxy.serverPacket.ShakeTokenPacket;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ProxyLoginService {

    // as Bungee
    public ConcurrentMap<UUID, String> serverToken;

    // as Bukkit
    public ConcurrentMap<UUID, String> clientToken;

    public ProxyLoginService() {
        Helper.getLogger().info("Initializing proxyLogin service...");
        serverToken = new ConcurrentHashMap<>();
        clientToken = new ConcurrentHashMap<>();

        SodionAuthCore.instance.api.registerPluginMessageChannel(ProxyChannel.name);
    }

    @Subscribe
    public void onJoin(JoinEvent event) {
        //event.getPlayer().sendServerData(ProxyChannel.name,new HelloServerPacket().encode());
    }

    @Subscribe
    public void onLogin(LoginEvent event) {
        if (serverToken.containsKey(event.getPlayer().getUniqueId())) {
            Helper.getLogger().info("Bungee send " + serverToken.get(event.getPlayer().getUniqueId()));
            event.getPlayer().sendServerData(
                    ProxyChannel.name,
                    new LoginSuccessPacket(
                            serverToken.get(event.getPlayer().getUniqueId())
                    ).pack());
        }
    }

    @Subscribe // as Bukkit
    public void onClientMessage(ClientMessageEvent event) {
        if (event.getChannel().equals(ProxyChannel.name)) {
            ClientPacket packet = ProxyChannel.parserClient(event.getData());
            if (packet instanceof HelloServerPacket) {
                String proxyToken = Helper.toStringUuid(UUID.randomUUID());
                Service.auth.logged_in.put(
                        event.getPlayer().getUniqueId(),
                        StatusType.PROXY_HANDLE);
                clientToken.put(event.getPlayer().getUniqueId(), proxyToken);
                event.getPlayer().sendClientData(ProxyChannel.name,
                        new ShakeTokenPacket(proxyToken).pack());
            } else if (packet instanceof LoginSuccessPacket) {
                if (clientToken.containsKey(event.getPlayer().getUniqueId())
                        && clientToken.get(event.getPlayer().getUniqueId())
                        .equals(((LoginSuccessPacket) packet).token)) {
                    Service.auth.login(event.getPlayer());
                }
            }
        }
    }

    @Subscribe // as Bungee
    public void onServerMessage(ServerMessageEvent event) {
        if (event.getChannel().equals(ProxyChannel.name)) {
            ServerPacket packet = ProxyChannel.parserServer(event.getData());
            if (packet instanceof ShakeTokenPacket) {
                serverToken.put(
                        event.getPlayer().getUniqueId(),
                        ((ShakeTokenPacket) packet).token);
                if (!Service.auth.needCancelled(event.getPlayer())) {
                    event.getPlayer().sendServerData(ProxyChannel.name,
                            new LoginSuccessPacket(
                                    serverToken.get(event.getPlayer().getUniqueId())
                            ).pack());
                }
            }
        }
    }

    @Subscribe
    public void onQuit(QuitEvent event) {
        clientToken.remove(event.getPlayer().getUniqueId());
        serverToken.remove(event.getPlayer().getUniqueId());
    }
}

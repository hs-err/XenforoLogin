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

import com.eloli.sodioncore.channel.BadSignException;
import com.eloli.sodioncore.channel.ClientPacket;
import com.eloli.sodioncore.channel.MessageChannel;
import com.eloli.sodioncore.channel.ServerPacket;
import com.eloli.sodioncore.channel.util.ByteUtil;
import com.google.common.eventbus.Subscribe;
import red.mohist.sodionauth.core.SodionAuthCore;
import red.mohist.sodionauth.core.modules.PlayerStatus;
import red.mohist.sodionauth.core.events.player.*;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.Helper;
import red.mohist.sodionauth.core.utils.proxychannel.clientPacket.HelloServerPacket;
import red.mohist.sodionauth.core.utils.proxychannel.clientPacket.LoginSuccessPacket;
import red.mohist.sodionauth.core.utils.proxychannel.serverPacket.ShakeTokenPacket;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ProxyLoginService {

    // as Bungee
    public ConcurrentMap<UUID, String> serverToken;

    // as Bukkit
    public ConcurrentMap<UUID, String> clientToken;

    public MessageChannel channel;

    public ProxyLoginService() {
        Helper.getLogger().info("Initializing proxyLogin service...");
        serverToken = new ConcurrentHashMap<>();
        clientToken = new ConcurrentHashMap<>();

        SodionAuthCore.instance.api.registerPluginMessageChannel(channel.name);
        channel = new MessageChannel("mbt:main",
                ByteUtil.sha256(Config.bungee.serverKey.getBytes(StandardCharsets.UTF_8)),
                ByteUtil.sha256(Config.bungee.clientKey.getBytes(StandardCharsets.UTF_8)))
                // Client packets
                .registerClientPacket(HelloServerPacket.class)
                .registerClientPacket(LoginSuccessPacket.class)
                // Server packets
                .registerServerPacket(ShakeTokenPacket.class);
    }

    @Subscribe
    public void onJoin(JoinEvent event) {
        // do it in adapter now
        // event.getPlayer().sendServerData(ProxyChannel.name,new HelloServerPacket().encode());
    }

    @Subscribe
    public void onLogin(LoginEvent event) {
        if (serverToken.containsKey(event.getPlayer().getUniqueId())) {
            Helper.getLogger().info("Bungee send " + serverToken.get(event.getPlayer().getUniqueId()));
            event.getPlayer().sendServerData(
                    channel.name,
                    channel.getClientFactory(LoginSuccessPacket.class).encode(
                            new LoginSuccessPacket(
                                    serverToken.get(event.getPlayer().getUniqueId())
                            )
                    ));
        }
    }

    @Subscribe // as Bukkit
    public void onClientMessage(ClientMessageEvent event) {
        if (event.getChannel().equals(channel.name)) {
            ClientPacket packet;
            try {
                packet = channel.getClientFactory(event.getData()).parser(event.getData());
            } catch (BadSignException e) {
                Helper.getLogger().warn("BadSignException when dealing player "+event.getPlayer().getName() +"'s packet.");
                return;
            }
            if (packet instanceof HelloServerPacket) {
                String proxyToken = Helper.toStringUuid(UUID.randomUUID());
                Service.auth.logged_in.put(
                        event.getPlayer().getUniqueId(),
                        PlayerStatus.proxyHandle());
                clientToken.put(event.getPlayer().getUniqueId(), proxyToken);
                event.getPlayer().sendClientData(channel.name,
                        channel.getServerFactory(ShakeTokenPacket.class).encode(
                                new ShakeTokenPacket(proxyToken)
                        ));
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
        if (event.getChannel().equals(channel.name)) {
            ServerPacket packet;
            try {
                packet = channel.getServerFactory(event.getData()).parser(event.getData());
            } catch (BadSignException e) {
                Helper.getLogger().warn("BadSignException when dealing player "+event.getPlayer().getName() +"'s packet.");
                return;
            }
            if (packet instanceof ShakeTokenPacket) {
                serverToken.put(
                        event.getPlayer().getUniqueId(),
                        ((ShakeTokenPacket) packet).token);
                if (!Service.auth.needCancelled(event.getPlayer())) {
                    event.getPlayer().sendServerData(channel.name,
                            channel.getClientFactory(LoginSuccessPacket.class).encode(
                                    new LoginSuccessPacket(
                                            serverToken.get(event.getPlayer().getUniqueId())
                                    )
                            ));
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

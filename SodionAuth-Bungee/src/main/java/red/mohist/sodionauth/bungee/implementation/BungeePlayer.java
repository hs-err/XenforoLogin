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

package red.mohist.sodionauth.bungee.implementation;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import red.mohist.sodionauth.core.modules.PlainPlayer;

public class BungeePlayer extends PlainPlayer {
    ProxiedPlayer player;

    public BungeePlayer(ProxiedPlayer player) {
        super(player.getName(), player.getUniqueId(), player.getAddress().getAddress());
        this.player = player;
    }

    @Override
    public void sendMessage(String message) {
        player.sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacyText(message));
    }

    @Override
    public void sendServerData(String channel, byte[] data) {
        player.getServer().sendData(channel, data);
    }

    @Override
    public void sendClientData(String channel, byte[] data) {
        player.sendData(channel, data);
    }

    @Override
    public void kick(String message) {
        player.disconnect(TextComponent.fromLegacyText(message));
    }

    @Override
    public boolean isOnline() {
        return player.isConnected();
    }
}

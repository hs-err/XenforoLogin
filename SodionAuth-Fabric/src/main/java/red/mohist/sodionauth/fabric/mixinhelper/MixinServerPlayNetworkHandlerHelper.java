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

package red.mohist.sodionauth.fabric.mixinhelper;

import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import red.mohist.sodionauth.core.SodionAuthCore;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.fabric.MixinLogger;
import red.mohist.sodionauth.fabric.data.Data;
import red.mohist.sodionauth.fabric.implementation.FabricPlayer;

public class MixinServerPlayNetworkHandlerHelper {
    public static void onGameMessage(ChatMessageC2SPacket packet, CallbackInfo ci, ServerPlayerEntity player) {
        if (Data.serverInstance.isOnThread()) return;
        AbstractPlayer abstractPlayer = new FabricPlayer(player);
        if (!SodionAuthCore.instance.needCancelled(abstractPlayer)) {
            if (Config.security.getCancelChatAfterLogin()) {
                abstractPlayer.sendMessage(abstractPlayer.getLang().getLoggedIn());
                ci.cancel();
            }
            return;
        }
        SodionAuthCore.instance.onChat(abstractPlayer, packet.getChatMessage());
    }

    public static void onInit(ServerPlayerEntity player) {
        MixinLogger.logger.info("Restoring networkHandler to ServerPlayerEntity");
        AbstractPlayer abstractPlayer = new FabricPlayer(player);
        MixinLogger.logger.info("Calling onJoin for " + player.getName().asString());
        SodionAuthCore.instance.onJoin(abstractPlayer);
    }
}

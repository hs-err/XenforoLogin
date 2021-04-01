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

package red.mohist.sodionauth.fabric.mixinhelper;

import com.mojang.authlib.GameProfile;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.knownspace.minitask.ITask;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import red.mohist.sodionauth.core.SodionAuthCore;
import red.mohist.sodionauth.core.events.player.CanJoinEvent;
import red.mohist.sodionauth.core.events.player.JoinEvent;
import red.mohist.sodionauth.core.services.Service;
import red.mohist.sodionauth.fabric.MixinLogger;
import red.mohist.sodionauth.fabric.implementation.FabricPlainPlayer;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;

public class MixinPlayerManagerHelper {
    public static void onCheckCanJoin(InetSocketAddress address, GameProfile profile, CallbackInfoReturnable<Text> cir) {
        if (cir.getReturnValue() != null) return;
        MixinLogger.logger.info("Checking if " + profile.getName() + " can join...");
        FabricPlainPlayer abstractPlayer = new FabricPlainPlayer(profile.getName(),
                profile.getId(), address.getAddress());
        if (!Service.auth.logged_in.containsKey(profile.getId())) {
            CanJoinEvent canJoinEvent = new CanJoinEvent(abstractPlayer);
            canJoinEvent.post();
            if (canJoinEvent.isCancelled()) {
                Service.auth.logged_in.remove(abstractPlayer.getUniqueId());
                MixinLogger.logger.info(profile.getName() + " was refused to login: " + canJoinEvent.getMessage());
                cir.setReturnValue(new LiteralText(canJoinEvent.getMessage()));
                return;
            }
        }


        JoinEvent joinEvent = new JoinEvent(abstractPlayer);
        if (!joinEvent.syncPost()) {
            cir.setReturnValue(new LiteralText(joinEvent.getMessage()));
        }
    }
}
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

import com.mojang.authlib.GameProfile;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import red.mohist.sodionauth.core.SodionAuthCore;
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
        if (!SodionAuthCore.instance.logged_in.containsKey(profile.getId())) {
            String reason = SodionAuthCore.instance.canLogin(abstractPlayer);
            if (reason != null) {
                SodionAuthCore.instance.logged_in.remove(abstractPlayer.getUniqueId());
                MixinLogger.logger.info(profile.getName() + " was refused to login: " + reason);
                cir.setReturnValue(new LiteralText(reason));
                return;
            }
        }


        SodionAuthCore.instance.canJoinAsync(abstractPlayer).thenWithException(future->{
            String reason;
            try {
                reason = future.get();
            } catch (InterruptedException | ExecutionException e) {
                SodionAuthCore.instance.logged_in.remove(abstractPlayer.getUniqueId());
                MixinLogger.logger.error("Error while authenticate " + profile.getName(), e);
                throw new RuntimeException(e);
            }
            if (reason != null) {
                SodionAuthCore.instance.logged_in.remove(abstractPlayer.getUniqueId());
                MixinLogger.logger.info(profile.getName() + " was refused to login: " + reason);
                cir.setReturnValue(new LiteralText(reason));
            }
        });
    }

}
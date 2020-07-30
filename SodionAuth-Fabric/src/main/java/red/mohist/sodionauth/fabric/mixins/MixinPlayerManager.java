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

package red.mohist.sodionauth.fabric.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import red.mohist.sodionauth.fabric.mixinhelper.MixinPlayerManagerHelper;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {

    @Inject(method = "checkCanJoin", at = @At("RETURN"))
    public void onCheckCanJoin(SocketAddress address, GameProfile profile, CallbackInfoReturnable<Text> cir) {
        //MixinPlayerManagerHelper.onCheckCanJoin((InetSocketAddress) address, profile, cir);
        MixinPlayerManagerHelper.onCheckCanJoinAsync((InetSocketAddress)address,profile,cir).thenWithException(future ->{
            try {
                future.get();
            }catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}

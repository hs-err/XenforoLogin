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

package red.mohist.xenforologin.fabric.mixins;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import red.mohist.xenforologin.fabric.FabricLoader;
import red.mohist.xenforologin.fabric.MixinLogger;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {

    private boolean didLoadWorld = false;

    @Inject(method = "loadWorld", at = @At("TAIL"))
    public void onPostWorld(CallbackInfo ci) {
        synchronized (this) {
            if (didLoadWorld) {
                MixinLogger.logger.warn("loadWorld called twice");
                return;
            }
            didLoadWorld = true;
        }
        MixinLogger.logger.info("Calling onServerPostWorld");
        FabricLoader.getInstance().onServerPostWorld();
    }

}

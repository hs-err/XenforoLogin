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

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.dedicated.ServerPropertiesLoader;
import net.minecraft.util.UserCache;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import red.mohist.xenforologin.fabric.FabricLoader;
import red.mohist.xenforologin.fabric.MixinLogger;
import red.mohist.xenforologin.fabric.data.Data;

import java.net.Proxy;

@Mixin(MinecraftDedicatedServer.class)
public abstract class MixinDedicatedServer extends MinecraftServer {

    public MixinDedicatedServer(Thread thread, RegistryTracker.Modifiable modifiable, LevelStorage.Session session, SaveProperties saveProperties, ResourcePackManager<ResourcePackProfile> resourcePackManager, Proxy proxy, DataFixer dataFixer, ServerResourceManager serverResourceManager, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, UserCache userCache, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory) {
        super(thread, modifiable, session, saveProperties, resourcePackManager, proxy, dataFixer, serverResourceManager, minecraftSessionService, gameProfileRepository, userCache, worldGenerationProgressListenerFactory);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onServerPostConstruction(Thread thread, RegistryTracker.Modifiable modifiable, LevelStorage.Session session, ResourcePackManager<ResourcePackProfile> resourcePackManager, ServerResourceManager serverResourceManager, SaveProperties saveProperties, ServerPropertiesLoader serverPropertiesLoader, DataFixer dataFixer, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, UserCache userCache, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory, CallbackInfo ci) {
        Data.serverInstance = MinecraftDedicatedServer.class.cast(this);
        MixinLogger.logger.info("Fetched server instance");
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Inject(method = "setupServer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/MinecraftServer;loadWorld()V",
                    shift = At.Shift.AFTER
            )
    )
    public void onServerPostWorld(CallbackInfoReturnable<Boolean> cir) {
        MixinLogger.logger.info("Calling onServerPostWorld");
        FabricLoader.getInstance().onServerPostWorld();
    }

}

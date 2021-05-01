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

package red.mohist.sodionauth.forge;

import com.google.common.base.Preconditions;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SWindowItemsPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import red.mohist.sodionauth.core.SodionAuthCore;
import red.mohist.sodionauth.core.modules.*;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.Helper;
import red.mohist.sodionauth.forge.implementation.ForgePlayer;
import red.mohist.sodionauth.forge.interfaces.ForgeAPIListener;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

@Mod(ForgeLoader.MODID)
public class ForgeLoader implements PlatformAdapter {
    public static final String MODID = "sodionauth";
    public static final String NAME = "SodionAuth";

    public static final Logger logger = LogManager.getLogger(NAME);
    public static ForgeLoader instance;
    public SodionAuthCore sodionAuthCore;
    public MinecraftServer server;

    public ForgeLoader() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStart(FMLServerStartedEvent event) {
        try {
            File configDir = new File("SodionAuth");
            if (!configDir.exists()) configDir.mkdir();

            new Helper(configDir.toString(), new LogProvider() {
                @Override
                public void info(String info) {
                    logger.info(info);
                }

                @Override
                public void info(String info, Exception exception) {
                    logger.info(info, exception.toString());
                }

                @Override
                public void warn(String info) {
                    logger.warn(info);
                }

                @Override
                public void warn(String info, Exception exception) {
                    logger.warn(info, exception);
                }
            });

            instance = this;
            server = event.getServer();
            Helper.getLogger().info("Hello, SodionAuth!");

            sodionAuthCore = new SodionAuthCore(this);

            registerListeners();
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("SodionAuth load fail.");
            event.getServer().close();
        }
    }

    private void registerListeners() {
        int unavailableCount = 0;
        Set<Class<? extends ForgeAPIListener>> classes = new Reflections("red.mohist.sodionauth.forge.listeners")
                .getSubTypesOf(ForgeAPIListener.class);
        for (Class<? extends ForgeAPIListener> clazz : classes) {
            ForgeAPIListener listener;
            try {
                listener = clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                Helper.getLogger().warn(clazz.getName() + " is not available.");
                unavailableCount++;
                continue;
            }
            if (!listener.isAvailable()) {
                Helper.getLogger().warn(clazz.getName() + " is not available.");
                unavailableCount++;
                continue;
            }
            MinecraftForge.EVENT_BUS.register(this);
        }
        if (unavailableCount > 0) {
            Helper.getLogger().warn("Warning: Some features in this plugin is not available on this version of bukkit");
            Helper.getLogger().warn("If your encountered errors, do NOT report to SodionAuth.");
            Helper.getLogger().warn("Error count: " + unavailableCount);
        }
    }

    @Override
    public void shutdown() {
        if (server != null) {
            logger.warn("SodionAuth load fail.");
            server.close();
        } else {
            throw new RuntimeException("SodionAuth load fail.");
        }
    }

    @Override
    public void registerPluginMessageChannel(String channel) {
        // not need
    }

    @Override
    public LocationInfo getSpawn(String world) {
        Preconditions.checkNotNull(world);
        for (ServerWorld level : server.getAllLevels()) {
            if (level.getLevelData() instanceof IServerWorldInfo) {
                if (((IServerWorldInfo) level.getLevelData()).getLevelName().equals(world)) {
                    BlockPos spawnPos = level.getSharedSpawnPos();
                    return new LocationInfo(world, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 0, 0);
                }
            }
        }
        return null;
    }

    @Override
    public String getDefaultWorld() {
        return ((IServerWorldInfo) server.overworld().getLevel()).getLevelName();
    }

    @Override
    public void onLogin(AbstractPlayer player) {
        if (player instanceof ForgePlayer) {
            ServerPlayerEntity serverPlayerEntity = ((ForgePlayer) player).getHandle();
            serverPlayerEntity.refreshContainer(serverPlayerEntity.containerMenu);
        }
    }

    @Override
    public void sendBlankInventoryPacket(AbstractPlayer player) {
        if (player instanceof ForgePlayer) {
            ServerPlayerEntity serverPlayerEntity = ((ForgePlayer) player).getHandle();
            if (!serverPlayerEntity.isAlive() && serverPlayerEntity.getHealth() <= 0) {
                server.getPlayerList().respawn(serverPlayerEntity, false);
            }

            player.setEffects(new LinkedList<>());
            player.setHealth(20);
            player.setRemainingAir(0);
            player.setMaxHealth(20);
            player.setFood(new FoodInfo());
            if (Config.security.spectatorLogin) {
                player.setGameMode(3);
            } else {
                player.setGameMode(Config.security.defaultGamemode);
            }

            serverPlayerEntity.connection.send(new SWindowItemsPacket(-1, NonNullList.withSize(54, ItemStack.EMPTY)));
        }
    }

    @Override
    public Collection<AbstractPlayer> getAllPlayer() {
        return server.getPlayerList().getPlayers().stream().map(ForgePlayer::new).collect(Collectors.toList());
    }
}

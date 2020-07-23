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

package red.mohist.sodionauth.fabric;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.level.ServerWorldProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import red.mohist.sodionauth.core.SodionAuthCore;
import red.mohist.sodionauth.core.interfaces.LogProvider;
import red.mohist.sodionauth.core.interfaces.PlatformAdapter;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.modules.LocationInfo;
import red.mohist.sodionauth.core.utils.Helper;
import red.mohist.sodionauth.fabric.data.Data;
import red.mohist.sodionauth.fabric.implementation.FabricPlayer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class FabricLoader implements ModInitializer, PlatformAdapter {

    public static final Logger logger = LogManager.getLogger("SodionAuth|Main");
    private static FabricLoader instance = null;
    private final File configDir = new File("./SodionAuth/");
    private SodionAuthCore core = null;

    public FabricLoader() {
        synchronized (FabricLoader.class) {
            if (instance != null) throw new IllegalStateException("Cannot initialize twice");
            instance = this;
        }
    }

    public static FabricLoader getInstance() {
        return instance;
    }

    @Override
    public void onInitialize() {
        configDir.mkdirs();
        logger.info("SodionAuth has been successfully discovered");
    }

    public void onServerPostWorld() {
        logger.info("Hello, SodionAuth!");
        try {
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
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize helper", e);
        }
        core = new SodionAuthCore(this);
    }

    public void onServerPreShutdown() {
        core.onDisable();
    }

    @Override
    public LocationInfo getSpawn(String world) {
        Preconditions.checkNotNull(world);
        for (ServerWorld world1 : Data.serverInstance.getWorlds()) {
            if (world1 == null) continue;
            final String levelName = ((ServerWorldProperties) world1.getLevelProperties()).getLevelName();
            if (!levelName.equals(world)) continue;
            final BlockPos spawnPos = world1.getSpawnPos();
            return new LocationInfo(world, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 0, 0);
        }
        return null;
    }

    @Override
    public String getDefaultWorld() {
        return ((ServerWorldProperties) Iterables.get(Data.serverInstance.getWorlds(), 0).getLevelProperties())
                .getLevelName();
    }

    @Override
    public void onLogin(AbstractPlayer player) {
        ServerPlayerEntity fabricPlayer = Data.serverInstance.getPlayerManager().getPlayer(player.getName());
        assert fabricPlayer != null;
        fabricPlayer.inventory.updateItems();
    }

    @Override
    public void sendBlankInventoryPacket(AbstractPlayer player) {
        ServerPlayerEntity fabricPlayer = Data.serverInstance.getPlayerManager().getPlayer(player.getName());
        assert fabricPlayer != null;
        fabricPlayer.networkHandler.sendPacket(new InventoryS2CPacket(-1,
                DefaultedList.ofSize(54, ItemStack.EMPTY)));
    }

    @Override
    public Collection<AbstractPlayer> getAllPlayer() {
        List<FabricPlayer> players = new ArrayList<>(Data.serverInstance.getCurrentPlayerCount());
        for (ServerPlayerEntity player :
                Data.serverInstance.getPlayerManager().getPlayerList()) {
            players.add(new FabricPlayer(player));
        }
        return Collections.unmodifiableList(players);
    }
}

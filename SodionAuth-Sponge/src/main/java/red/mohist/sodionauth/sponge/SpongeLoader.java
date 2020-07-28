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

package red.mohist.sodionauth.sponge;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import red.mohist.sodionauth.core.SodionAuthCore;
import red.mohist.sodionauth.core.interfaces.LogProvider;
import red.mohist.sodionauth.core.interfaces.PlatformAdapter;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.modules.FoodInfo;
import red.mohist.sodionauth.core.modules.LocationInfo;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.Helper;
import red.mohist.sodionauth.libs.reflections.Reflections;
import red.mohist.sodionauth.sponge.implementation.SpongePlayer;
import red.mohist.sodionauth.sponge.interfaces.SpongeAPIListener;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

@Plugin(
        id = "sodionauth",
        name = "SodionAuth",
        description = "A new generation of authentication plugin for Minecraft",
        version = "2.0"
)
public class SpongeLoader implements PlatformAdapter {
    public static SpongeLoader instance;
    public SodionAuthCore sodionAuthCore;
    @Inject
    private Logger logger;
    @Inject
    @ConfigDir(sharedRoot = false)
    private Path privateConfigDir;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        try {
            new Helper(privateConfigDir.toString(), new LogProvider() {
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
            Helper.getLogger().info("Hello, SodionAuth!");

            sodionAuthCore = new SodionAuthCore(this);


            {
                int unavailableCount = 0;
                Set<Class<? extends SpongeAPIListener>> classes = new Reflections("red.mohist.sodionauth.sponge.listeners")
                        .getSubTypesOf(SpongeAPIListener.class);
                for (Class<? extends SpongeAPIListener> clazz : classes) {
                    SpongeAPIListener listener;
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
                    Sponge.getEventManager().registerListeners(this, listener);
                }
                if (unavailableCount > 0) {
                    Helper.getLogger().warn("Warning: Some features in this plugin is not available on this version of sponge");
                    Helper.getLogger().warn("If your encountered errors, do NOT report to SodionAuth.");
                    Helper.getLogger().warn("Error count: " + unavailableCount);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.warn("SodionAuth load fail.");
            Sponge.getServer().shutdown(Text.of("SodionAuth load fail."));
        }
    }

    @Listener
    public void onServerStop(GameStoppingServerEvent event) {
        SodionAuthCore.instance.onDisable();
    }

    @Override
    public void shutdown() {
        Sponge.getServer().shutdown(Text.of("SodionAuth load fail."));
    }


    @Override
    public Collection<AbstractPlayer> getAllPlayer() {
        Collection<AbstractPlayer> allPlayers = new Vector<>();
        for (Player onlinePlayer : Sponge.getServer().getOnlinePlayers()) {
            allPlayers.add(new SpongePlayer(onlinePlayer));
        }
        return allPlayers;
    }

    @Override
    public LocationInfo getSpawn(String worldName) {
        Optional<Location<World>> world = Sponge.getServer().getWorld(worldName).map(World::getSpawnLocation);
        return new LocationInfo(worldName, world.get().getX(), world.get().getY(), world.get().getZ(), 0, 0);
    }

    @Override
    public String getDefaultWorld() {
        return Sponge.getServer().getDefaultWorldName();
    }

    @Override
    public void onLogin(AbstractPlayer player) {

    }

    @Override
    public void sendBlankInventoryPacket(AbstractPlayer player) {
        if (player instanceof SpongePlayer) {
            Runnable run = () -> {
                Player handle = ((SpongePlayer) player).handle;
                handle.respawnPlayer();
                player.setEffects(new LinkedList<>());
                player.setHealth(20);
                player.setRemainingAir(0);
                player.setMaxHealth(20);
                player.setFood(new FoodInfo());
                if (Config.security.getSpectatorLogin()) {
                    player.setGameMode(3);
                } else {
                    player.setGameMode(Config.security.getDefaultGamemode());
                }
            };
            if (Sponge.getServer().isMainThread()) {
                run.run();
            } else {
                Sponge.getScheduler()
                        .createTaskBuilder()
                        .execute(run)
                        .submit(this);
            }
        }
    }
}

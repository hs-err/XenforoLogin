/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.sponge;

import com.google.inject.Inject;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import red.mohist.xenforologin.core.XenforoLoginCore;
import red.mohist.xenforologin.core.interfaces.LogProvider;
import red.mohist.xenforologin.core.interfaces.PlatformAdapter;
import red.mohist.xenforologin.core.modules.AbstractPlayer;
import red.mohist.xenforologin.core.modules.LocationInfo;
import red.mohist.xenforologin.core.utils.Helper;
import red.mohist.xenforologin.sponge.implementation.SpongePlayer;
import red.mohist.xenforologin.sponge.interfaces.SpongeAPIListener;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.Vector;

@Plugin(
        id = "xenforologin",
        name = "XenforoLogin",
        description = "XenforoLogin"
)
public class SpongeLoader implements PlatformAdapter {
    @Inject
    private Logger logger;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path privateConfigDir;

    public SpongeLoader instance;
    public XenforoLoginCore xenforoLoginCore;

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
                    logger.info(info,exception.toString());
                }

                @Override
                public void warn(String info) {
                    logger.warn(info);
                }

                @Override
                public void warn(String info, Exception exception) {
                    logger.warn(info,exception);
                }
            });

            instance = this;
            Helper.getLogger().info("Hello, XenforoLogin!");

            xenforoLoginCore = new XenforoLoginCore(this);


            {
                int unavailableCount = 0;
                Set<Class<? extends SpongeAPIListener>> classes = new Reflections("red.mohist.xenforologin.sponge.listeners")
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
                    Helper.getLogger().warn("If your encountered errors, do NOT report to XenforoLogin.");
                    Helper.getLogger().warn("Error count: " + unavailableCount);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.warn("XenforoLogin load fail.");
            Sponge.getServer().shutdown();
        }

    }

    @Listener
    public void onServerStop(GameStoppingServerEvent event) {
        XenforoLoginCore.instance.onDisable();
    }

    @Override
    public Collection<AbstractPlayer> getAllPlayer() {
        Collection<AbstractPlayer> allPlayers=new Vector<>();
        for (Player onlinePlayer : Sponge.getServer().getOnlinePlayers()) {
            allPlayers.add(new SpongePlayer(onlinePlayer));
        }
        return allPlayers;
    }

    @Override
    public LocationInfo getSpawn(String worldName) {
        Optional<Location<World>> world = Sponge.getServer().getWorld(worldName).map(World::getSpawnLocation);
        return new LocationInfo(worldName,world.get().getX(),world.get().getY(),world.get().getZ(),0,0);
    }

    @Override
    public void login(AbstractPlayer player) {

    }

    @Override
    public void sendBlankInventoryPacket(AbstractPlayer player) {

    }
}

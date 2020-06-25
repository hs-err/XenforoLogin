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
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import red.mohist.xenforologin.core.XenforoLoginCore;
import red.mohist.xenforologin.core.interfaces.LogProvider;
import red.mohist.xenforologin.core.interfaces.PlatformAdapter;
import red.mohist.xenforologin.core.modules.AbstractPlayer;
import red.mohist.xenforologin.core.modules.LocationInfo;
import red.mohist.xenforologin.core.utils.Helper;
import red.mohist.xenforologin.sponge.listeners.CommonListener;

import java.io.IOException;
import java.util.Optional;

@Plugin(id = "xenforologin", name = "XenforoLogin", version = "1.2.1", description = "XenforoLogin")
public class SpongeLoader implements PlatformAdapter {
    @Inject
    private Logger logger;
    public SpongeLoader instance;
    public XenforoLoginCore xenforoLoginCore;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        try {
            new Helper(".", new LogProvider() {
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

            Sponge.getEventManager().registerListeners(this, new CommonListener());
        } catch (IOException e) {
            e.printStackTrace();
            logger.warn("XenforoLogin load fail.");
            Sponge.getServer().shutdown();
        }

        instance = this;
        Helper.getLogger().info("Hello, XenforoLogin!");

        xenforoLoginCore = new XenforoLoginCore(this);
    }


    @Override
    public LocationInfo getSpawn(String worldName) {
        Optional<Location<World>> world = Sponge.getServer().getWorld(worldName).map(World::getSpawnLocation);
        return new LocationInfo(worldName,world.get().getX(),world.get().getY(),world.get().getZ(),0,0);
    }

    @Override
    public void login(AbstractPlayer player) {
        player.sendMessage("LLLLLLLL");
    }

    @Override
    public void sendBlankInventoryPacket(AbstractPlayer player) {
        player.sendMessage("sendBlankInventoryPacket???");
    }
}

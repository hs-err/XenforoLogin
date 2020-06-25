/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.sponge.listeners;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import red.mohist.xenforologin.core.XenforoLoginCore;
import red.mohist.xenforologin.core.modules.LocationInfo;
import red.mohist.xenforologin.core.utils.Config;
import red.mohist.xenforologin.sponge.implementation.SpongePlayer;
import red.mohist.xenforologin.sponge.interfaces.SpongeAPIListener;

public class MoveEntityListener implements SpongeAPIListener {

    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onMoveEntity(MoveEntityEvent event, @First Player player) {
        if (event instanceof MoveEntityEvent.Teleport
            || !XenforoLoginCore.instance.needCancelled(new SpongePlayer(player))) {
            return;
        }

        if (Config.getBoolean("teleport.tp_spawn_before_login", true)) {
            if (Config.getBoolean("teleport.tp_spawn_before_login", true)) {
                if (XenforoLoginCore.instance.default_location.x
                        != event.getToTransform().getPosition().getFloorX()
                        || XenforoLoginCore.instance.default_location.z
                        != event.getToTransform().getPosition().getFloorZ()) {
                    event.setCancelled(true);
                    new SpongePlayer(player).teleport(new LocationInfo(
                            XenforoLoginCore.instance.default_location.world,
                            XenforoLoginCore.instance.default_location.x,
                            XenforoLoginCore.instance.default_location.y,
                            XenforoLoginCore.instance.default_location.z,
                            XenforoLoginCore.instance.default_location.yaw,
                            XenforoLoginCore.instance.default_location.pitch
                    ));
                }
            }
        } else {
            if (event.getFromTransform().getPosition().getFloorX()
                    != event.getToTransform().getPosition().getFloorX()
                    || event.getFromTransform().getPosition().getFloorZ()
                    != event.getToTransform().getPosition().getFloorZ()) {
                event.setCancelled(true);
            }
        }
    }
    @Override
    public void eventClass() {
        MoveEntityEvent.class.getName();
    }
}

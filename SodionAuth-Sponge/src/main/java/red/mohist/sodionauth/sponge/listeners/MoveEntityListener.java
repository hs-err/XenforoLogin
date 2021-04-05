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

package red.mohist.sodionauth.sponge.listeners;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import red.mohist.sodionauth.core.modules.LocationInfo;
import red.mohist.sodionauth.core.services.Service;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.sponge.implementation.SpongePlayer;
import red.mohist.sodionauth.sponge.interfaces.SpongeAPIListener;

public class MoveEntityListener implements SpongeAPIListener {

    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onMoveEntity(MoveEntityEvent event, @First Player player) {
        if (event instanceof MoveEntityEvent.Teleport
                || !Service.auth.needCancelled(new SpongePlayer(player))) {
            return;
        }

        if (Config.teleport.getTpSpawnBeforeLogin()) {
            if (Config.security.getSpectatorLogin()) {
                event.setCancelled(true);
                new SpongePlayer(player).teleport(new LocationInfo(
                        Service.auth.default_location.world,
                        Service.auth.default_location.x,
                        Service.auth.default_location.y,
                        Service.auth.default_location.z,
                        Service.auth.default_location.yaw,
                        Service.auth.default_location.pitch
                ));
            } else {
                if (Service.auth.default_location.x
                        != event.getToTransform().getPosition().getFloorX()
                        || Service.auth.default_location.z
                        != event.getToTransform().getPosition().getFloorZ()) {
                    event.setCancelled(true);
                    new SpongePlayer(player).teleport(new LocationInfo(
                            Service.auth.default_location.world,
                            Service.auth.default_location.x,
                            event.getToTransform().getPosition().getFloorY(),
                            Service.auth.default_location.z,
                            Service.auth.default_location.yaw,
                            Service.auth.default_location.pitch
                    ));
                }
            }

        } else {
            if (event.getFromTransform().getPosition().getFloorX()
                    != event.getToTransform().getPosition().getFloorX()
                    || event.getFromTransform().getPosition().getFloorZ()
                    != event.getToTransform().getPosition().getFloorZ()) {
                event.setCancelled(true);
                new SpongePlayer(player).teleport(new LocationInfo(
                        event.getToTransform().getExtent().getName(),
                        event.getFromTransform().getPosition().getFloorX(),
                        event.getToTransform().getPosition().getFloorY(),
                        event.getFromTransform().getPosition().getFloorZ(),
                        (float)event.getFromTransform().getYaw(),
                        (float)event.getFromTransform().getPitch()
                ));
            }
        }
    }

    @Override
    public void eventClass() {
        MoveEntityEvent.class.getName();
    }
}

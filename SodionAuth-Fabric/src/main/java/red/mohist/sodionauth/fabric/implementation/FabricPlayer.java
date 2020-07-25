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

package red.mohist.sodionauth.fabric.implementation;

import com.google.common.base.Preconditions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.modules.LocationInfo;
import red.mohist.sodionauth.fabric.data.Data;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class FabricPlayer extends AbstractPlayer {

    private final ServerPlayerEntity handle;

    public FabricPlayer(ServerPlayerEntity handle) {
        super(handle.getName().getString(), handle.getUuid(),
                ((InetSocketAddress) handle.networkHandler.connection.getAddress()).getAddress());
        this.handle = handle;
    }

    @Override
    public void sendMessage(String message) {
        Preconditions.checkState(!handle.isDisconnected());
        handle.sendMessage(Text.method_30163(message), false);
    }

    @Override
    public CompletableFuture<Boolean> teleport(LocationInfo location) {
        Preconditions.checkState(!handle.isDisconnected());
        AtomicReference<ServerWorld> world = new AtomicReference<>();
        Data.serverInstance.getWorldRegistryKeys().forEach(worldRegistryKey -> {
            if (location.world.equals(worldRegistryKey.getValue().toString()))
                world.set(Data.serverInstance.getWorld(worldRegistryKey));
        });

        try {
            handle.teleport(world.get(), location.x, location.y, location.z, location.yaw, location.pitch);
        } catch (Throwable throwable) {
            return CompletableFuture.completedFuture(false);
        }
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public void kick(String message) {
        Preconditions.checkState(!handle.isDisconnected());
        handle.networkHandler.disconnect(Text.method_30163(message));
    }

    @Override
    public LocationInfo getLocation() {
        Preconditions.checkState(!handle.isDisconnected());
        return null;
    }

    @Override
    public int getGameMode() {
        Preconditions.checkState(!handle.isDisconnected());
        return handle.interactionManager.getGameMode().getId();
    }

    @Override
    public void setGameMode(int gameMode) {
        Preconditions.checkState(!handle.isDisconnected());
        handle.setGameMode(GameMode.byId(gameMode, GameMode.SURVIVAL));
    }

    @Override
    public boolean isOnline() {
        return !handle.isDisconnected();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FabricPlayer that = (FabricPlayer) o;
        return handle.equals(that.handle);
    }

    @Override
    public int hashCode() {
        return handle.getUuid().hashCode();
    }
}

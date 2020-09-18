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
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameMode;
import net.minecraft.world.level.ServerWorldProperties;
import red.mohist.sodionauth.core.modules.*;
import red.mohist.sodionauth.fabric.FabricLoader;
import red.mohist.sodionauth.fabric.data.Data;
import red.mohist.sodionauth.fabric.mixininterface.IHungerManager;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class FabricPlayer extends AbstractPlayer {

    private final ServerPlayerEntity handle;

    public FabricPlayer(ServerPlayerEntity handle) {
        super(handle.getName().getString(), handle.getUuid(),
                ((InetSocketAddress) handle.networkHandler.connection.getAddress()).getAddress());
        this.handle = handle;
    }

    public ServerPlayerEntity getHandle() {
        return handle;
    }

    @Override
    public void sendMessage(String message) {
        Preconditions.checkState(!handle.isDisconnected());
        handle.sendMessage(new LiteralText(message), false);
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
        handle.networkHandler.disconnect(new LiteralText(message));
    }

    @Override
    public LocationInfo getLocation() {
        Preconditions.checkState(!handle.isDisconnected());
        return new LocationInfo(
                ((ServerWorldProperties) handle.world.getLevelProperties())
                        .getLevelName(),
                handle.getX(),
                handle.getY(),
                handle.getZ(),
                handle.yaw,
                handle.pitch
        );
    }

    @Override
    public void setLocation(LocationInfo location) {
        Preconditions.checkState(!handle.isDisconnected());
        Preconditions.checkNotNull(location);
        ServerWorld world = null;
        for (ServerWorld current : Data.serverInstance.getWorlds()) {
            if (((ServerWorldProperties) current.getLevelProperties())
                    .getLevelName().equals(location.world)) {
                world = current;
                break;
            }
        }
        Preconditions.checkArgument(world != null, "Invaild world");
        handle.teleport(
                world,
                location.x,
                location.y,
                location.z,
                location.yaw,
                location.pitch
        );
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
    public double getHealth() {
        Preconditions.checkState(!handle.isDisconnected());
        return handle.getHealth();
    }

    @Override
    public void setHealth(double health) {
        Preconditions.checkState(!handle.isDisconnected());
        handle.setHealth((float) health);
    }

    @Override
    public double getMaxHealth() {
        Preconditions.checkState(!handle.isDisconnected());
        return handle.getMaxHealth();
    }

    @Override
    public void setMaxHealth(double maxHealth) {
        Preconditions.checkState(!handle.isDisconnected());
        throw new UnsupportedOperationException("Setting max health in fabric is not implemented yet");
    }

    @Override
    public float getFallDistance() {
        Preconditions.checkState(!handle.isDisconnected());
        return handle.fallDistance;
    }

    @Override
    public void setFallDistance(float fallDistance) {
        Preconditions.checkState(!handle.isDisconnected());
        handle.fallDistance = fallDistance;
    }

    @Override
    public VelocityInfo getVelocity() {
        Preconditions.checkState(!handle.isDisconnected());
        return VelocityInfo.create(
                handle.getVelocity().x,
                handle.getVelocity().y,
                handle.getVelocity().z
        );
    }

    @Override
    public void setVelocity(VelocityInfo velocity) {
        Preconditions.checkState(!handle.isDisconnected());
        handle.setVelocity(velocity.x, velocity.y, velocity.z);
    }

    @Override
    public FoodInfo getFood() {
        Preconditions.checkState(!handle.isDisconnected());
        return FoodInfo.create(
                handle.getHungerManager().getFoodLevel(),
                ((IHungerManager) handle.getHungerManager()).getExhaustion(),
                handle.getHungerManager().getSaturationLevel()
        );
    }

    @Override
    public void setFood(FoodInfo food) {
        Preconditions.checkState(!handle.isDisconnected());
        handle.getHungerManager().setFoodLevel(food.foodLevel);
        ((IHungerManager) handle.getHungerManager()).setExhaustion((float) food.exhaustion);
        ((IHungerManager) handle.getHungerManager()).setSaturation((float) food.saturation);
    }

    @Override
    public int getRemainingAir() {
        Preconditions.checkState(!handle.isDisconnected());
        return handle.getMaxAir() - handle.getAir();
    }

    @Override
    public void setRemainingAir(int remainingAir) {
        Preconditions.checkState(!handle.isDisconnected());
        handle.setAir(handle.getMaxAir() - remainingAir);
    }

    @Override
    public Collection<EffectInfo> getEffects() {
        Preconditions.checkState(!handle.isDisconnected());
        ArrayList<EffectInfo> effects = new ArrayList<>(handle.getStatusEffects().size());
        for (StatusEffectInstance statusEffect : handle.getStatusEffects()) {
            effects.add(EffectInfo.create(
                    Objects.requireNonNull(Registry.STATUS_EFFECT.getId(statusEffect.getEffectType())).toString(),
                    statusEffect.getAmplifier(),
                    statusEffect.getDuration()
            ));
        }
        return Collections.unmodifiableList(effects);
    }

    @Override
    public void setEffects(Collection<EffectInfo> effects) {
        Preconditions.checkState(!handle.isDisconnected());
        if (handle.clearStatusEffects())
            FabricLoader.logger.warn("Cannot clear " + handle.getName() + " status effects cleanly");
        for (EffectInfo effect : effects) {
            handle.addStatusEffect(new StatusEffectInstance(
                    Registry.STATUS_EFFECT.get(new Identifier(effect.type)),
                    effect.duration,
                    effect.amplifier
            ));
        }
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

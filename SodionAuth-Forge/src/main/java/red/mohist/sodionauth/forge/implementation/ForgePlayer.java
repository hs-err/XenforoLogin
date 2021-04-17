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

package red.mohist.sodionauth.forge.implementation;

import com.google.common.base.Preconditions;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.network.play.server.SCustomPayloadPlayPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.GameType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.IServerWorldInfo;
import red.mohist.sodionauth.core.modules.*;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ForgePlayer extends AbstractPlayer {
    private ServerPlayerEntity handle;

    public ForgePlayer(ServerPlayerEntity handle) {
        super(handle.getScoreboardName(), handle.getUUID(), ((InetSocketAddress)handle.connection.connection.getRemoteAddress()).getAddress());
        this.handle = handle;
    }

    public ServerPlayerEntity getHandle() {
        return this.handle;
    }

    @Override
    public void sendMessage(String message) {
        Preconditions.checkState(isOnline());
        getHandle().connection.send(new SChatPacket(new StringTextComponent(message), ChatType.CHAT, Util.NIL_UUID));
    }

    @Override
    public void sendServerData(String channel, byte[] data) {
        // empty
    }

    @Override
    public void sendClientData(String channel, byte[] data) {
        Preconditions.checkState(isOnline());
        SCustomPayloadPlayPacket packet = new SCustomPayloadPlayPacket(new ResourceLocation(channel), new PacketBuffer(Unpooled.wrappedBuffer(data)));
        getHandle().connection.send(packet);
    }

    @Override
    public CompletableFuture<Boolean> teleport(LocationInfo location) {
        Preconditions.checkState(isOnline());
        ServerWorld toWorld = null;
        for (ServerWorld level : getHandle().server.getAllLevels()) {
            if (level.getLevelData() instanceof IServerWorldInfo) {
                if (((IServerWorldInfo) level.getLevelData()).getLevelName().equals(location.world)) {
                    toWorld = level;
                }
            }
        }

        if (toWorld != null) {
            getHandle().teleportTo(toWorld, location.x, location.y, location.z, location.yaw, location.pitch);
        }

        return CompletableFuture.completedFuture(true);
    }

    @Override
    public void kick(String message) {
        Preconditions.checkState(isOnline());
        getHandle().connection.disconnect(new StringTextComponent(message == null ? "" : message));
    }

    @Override
    public LocationInfo getLocation() {
        Preconditions.checkState(isOnline());
        return new LocationInfo(
                ((IServerWorldInfo) getHandle().level.getLevelData()).getLevelName(),
                getHandle().getX(),
                getHandle().getY(),
                getHandle().getZ(),
                getHandle().yRot,
                getHandle().xRot
        );
    }

    @Override
    public void setLocation(LocationInfo location) {
        Preconditions.checkState(isOnline());
        teleport(location);
    }

    @Override
    public int getGameMode() {
        Preconditions.checkState(isOnline());
        return getHandle().gameMode.getGameModeForPlayer().getId();
    }

    @Override
    public void setGameMode(int gameMode) {
        Preconditions.checkState(isOnline());
        getHandle().setGameMode(GameType.byId(gameMode));
    }

    @Override
    public double getHealth() {
        Preconditions.checkState(isOnline());
        return getHandle().getHealth();
    }

    @Override
    public void setHealth(double health) {
        Preconditions.checkState(isOnline());
        getHandle().setHealth((float) health);
    }

    @Override
    public double getMaxHealth() {
        Preconditions.checkState(isOnline());
        return getHandle().getMaxHealth();
    }

    @Override
    public void setMaxHealth(double maxHealth) {
        Preconditions.checkState(isOnline());
        getHandle().getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHealth);
    }

    @Override
    public float getFallDistance() {
        Preconditions.checkState(isOnline());
        return getHandle().fallDistance;
    }

    @Override
    public void setFallDistance(float fallDistance) {
        Preconditions.checkState(isOnline());
        getHandle().fallDistance = fallDistance;
    }

    @Override
    public VelocityInfo getVelocity() {
        Preconditions.checkState(isOnline());
        Vector3d vector = getHandle().getDeltaMovement();
        return VelocityInfo.create(vector.x, vector.y, vector.z);
    }

    @Override
    public void setVelocity(VelocityInfo velocity) {
        Preconditions.checkState(isOnline());
        getHandle().setDeltaMovement(new Vector3d(velocity.x, velocity.y, velocity.z));
        getHandle().hurtMarked = true;
    }

    @Override
    public FoodInfo getFood() {
        Preconditions.checkState(isOnline());
        return FoodInfo.create(
                getHandle().getFoodData().getFoodLevel(),
                getHandle().getFoodData().exhaustionLevel,
                getHandle().getFoodData().getSaturationLevel()
        );
    }

    @Override
    public void setFood(FoodInfo food) {
        Preconditions.checkState(isOnline());
        getHandle().getFoodData().setFoodLevel(food.foodLevel);
        getHandle().getFoodData().exhaustionLevel = (float) food.exhaustion;
        getHandle().getFoodData().setSaturation((float) food.saturation);
    }

    @Override
    public int getRemainingAir() {
        Preconditions.checkState(isOnline());
        return getHandle().getAirSupply();
    }

    @Override
    public void setRemainingAir(int remainingAir) {
        Preconditions.checkState(isOnline());
        getHandle().setAirSupply(remainingAir);
    }

    @Override
    public Collection<EffectInfo> getEffects() {
        Preconditions.checkState(isOnline());
        ArrayList<EffectInfo> effects = new ArrayList<>();
        for (EffectInstance effectInstance : getHandle().getActiveEffects()) {
            effects.add(EffectInfo.create(
                    Registry.MOB_EFFECT.getKey(effectInstance.getEffect()).toString(),
                    effectInstance.getAmplifier(),
                    effectInstance.getDuration()
            ));
        }
        return effects;
    }

    @Override
    public void setEffects(Collection<EffectInfo> effects) {
        Preconditions.checkState(isOnline());
        for (EffectInstance effectInstance : new ArrayList<>(getHandle().getActiveEffects())) {
            getHandle().removeEffect(effectInstance.getEffect());
        }
        for (EffectInfo effect : effects) {
            handle.addEffect(new EffectInstance(
                    Registry.MOB_EFFECT.get(new ResourceLocation(effect.type)),
                    effect.duration,
                    effect.amplifier
            ));
        }
    }

    @Override
    public boolean isOnline() {
        return !getHandle().hasDisconnected();
    }
}

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

package red.mohist.sodionauth.sponge.implementation;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.FoodData;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.mutable.MutableBoundedValue;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import red.mohist.sodionauth.core.modules.*;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.Helper;
import red.mohist.sodionauth.sponge.SpongeLoader;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class SpongePlayer extends AbstractPlayer {

    public Player handle;

    public SpongePlayer(Player handle) {
        super(handle.getName(), handle.getUniqueId(), handle.getConnection().getAddress().getAddress());
        this.handle = handle;
    }

    public SpongePlayer(String name, UUID uuid, InetAddress address) {
        super(name, uuid, address);
    }

    public void checkHandle() {
        if (handle == null) {
            Optional<Player> optionalPlayer = Sponge.getServer().getPlayer(getUniqueId());
            if (!optionalPlayer.isPresent()) {
                Helper.getLogger().warn("No player can be call.");
            } else {
                handle = optionalPlayer.get();
            }
        }
    }

    @Override
    public void sendMessage(String message) {
        checkHandle();
        handle.sendMessage(Text.of(message));
    }

    @Override
    public CompletableFuture<Boolean> teleport(LocationInfo location) {
        checkHandle();
        CompletableFuture<Boolean> booleanCompletableFuture = new CompletableFuture<>();
        if (Sponge.getServer().isMainThread()) {
            booleanCompletableFuture.complete(handle.setLocation(Sponge.getServer().getWorld(location.world)
                    .get().getLocation(location.x, location.y, location.z)));
        } else {
            Sponge.getScheduler().createTaskBuilder().execute(() -> {
                try {
                    booleanCompletableFuture.complete(teleport(location).get());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }).submit(SpongeLoader.instance);
        }
        return booleanCompletableFuture;
    }

    @Override
    public void kick(String message) {
        checkHandle();
        if (!Sponge.getServer().isMainThread()) {
            Sponge.getScheduler()
                    .createTaskBuilder()
                    .execute(() -> {
                        kick(message);
                    })
                    .submit(SpongeLoader.instance);
            return;
        }
        handle.kick(Text.of(message));
    }

    @Override
    public LocationInfo getLocation() {
        checkHandle();
        Location<World> location = handle.getLocation();
        return new LocationInfo(
                location.getExtent().getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                0, 0
        );
    }

    @Override
    public void setLocation(LocationInfo location) {
        checkHandle();
        teleport(location);
    }

    public int getGameMode() {
        checkHandle();
        GameMode gameMode = handle.gameMode().get();
        if (GameModes.SURVIVAL.equals(gameMode)) {
            return 0;
        } else if (GameModes.CREATIVE.equals(gameMode)) {
            return 1;
        } else if (GameModes.ADVENTURE.equals(gameMode)) {
            return 2;
        } else if (GameModes.SPECTATOR.equals(gameMode)) {
            return 3;
        } else {
            return Config.security.getDefaultGamemode(0);
        }
    }

    @Override
    public void setGameMode(int gameMode) {
        checkHandle();
        if (!Sponge.getServer().isMainThread()) {
            Sponge.getScheduler()
                    .createTaskBuilder()
                    .execute(() -> {
                        setGameMode(gameMode);
                    })
                    .submit(SpongeLoader.instance);
            return;
        }
        switch (gameMode) {
            case 0:
                handle.offer(Keys.GAME_MODE, GameModes.SURVIVAL);
                return;
            case 1:
                handle.offer(Keys.GAME_MODE, GameModes.CREATIVE);
                return;
            case 2:
                handle.offer(Keys.GAME_MODE, GameModes.ADVENTURE);
                return;
            case 3:
                handle.offer(Keys.GAME_MODE, GameModes.SPECTATOR);
        }
    }

    @Override
    public double getHealth() {
        checkHandle();
        return handle.getHealthData().health().get();
    }

    @Override
    public void setHealth(double health) {
        checkHandle();
        if (!Sponge.getServer().isMainThread()) {
            Sponge.getScheduler()
                    .createTaskBuilder()
                    .execute(() -> {
                        setHealth(health);
                    })
                    .submit(SpongeLoader.instance);
            return;
        }
        handle.offer(Keys.HEALTH, health);
    }

    @Override
    public double getMaxHealth() {
        checkHandle();
        return handle.getHealthData().maxHealth().get();
    }

    @Override
    public void setMaxHealth(double maxHealth) {
        checkHandle();
        if (!Sponge.getServer().isMainThread()) {
            Sponge.getScheduler()
                    .createTaskBuilder()
                    .execute(() -> {
                        setMaxHealth(maxHealth);
                    })
                    .submit(SpongeLoader.instance);
            return;
        }
        handle.offer(Keys.MAX_HEALTH, maxHealth);
    }

    @Override
    public float getFallDistance() {
        checkHandle();
        return handle.get(Keys.FALL_DISTANCE).orElse((float) 0);
    }

    @Override
    public void setFallDistance(float fallDistance) {
        checkHandle();
        if (!Sponge.getServer().isMainThread()) {
            Sponge.getScheduler()
                    .createTaskBuilder()
                    .execute(() -> {
                        setFallDistance(fallDistance);
                    })
                    .submit(SpongeLoader.instance);
            return;
        }
        handle.offer(Keys.FALL_DISTANCE, fallDistance);
    }

    @Override
    public VelocityInfo getVelocity() {
        checkHandle();
        Vector3d v3d = handle.getVelocity();
        return VelocityInfo.create(v3d.getX(), v3d.getY(), v3d.getZ());
    }

    @Override
    public void setVelocity(VelocityInfo velocity) {
        checkHandle();
        if (!Sponge.getServer().isMainThread()) {
            Sponge.getScheduler()
                    .createTaskBuilder()
                    .execute(() -> {
                        setVelocity(velocity);
                    })
                    .submit(SpongeLoader.instance);
            return;
        }
        handle.setVelocity(new Vector3d(velocity.x, velocity.y, velocity.z));
    }

    @Override
    public FoodInfo getFood() {
        checkHandle();
        FoodData foodData = handle.getFoodData();
        return FoodInfo.create(
                foodData.foodLevel().get(),
                foodData.exhaustion().get(),
                foodData.saturation().get()
        );
    }

    @Override
    public void setFood(FoodInfo food) {
        checkHandle();
        if (!Sponge.getServer().isMainThread()) {
            Sponge.getScheduler()
                    .createTaskBuilder()
                    .execute(() -> {
                        setFood(food);
                    })
                    .submit(SpongeLoader.instance);
            return;
        }
        handle.offer(Keys.FOOD_LEVEL, food.foodLevel);
        handle.offer(Keys.EXHAUSTION, food.exhaustion);
        handle.offer(Keys.SATURATION, food.saturation);
    }

    @Override
    public int getRemainingAir() {
        checkHandle();
        Optional<MutableBoundedValue<Integer>> remainingAir = handle.getValue(Keys.REMAINING_AIR);
        return remainingAir.map(BaseValue::get).orElse(0);
    }

    @Override
    public void setRemainingAir(int remainingAir) {
        checkHandle();
        if (!Sponge.getServer().isMainThread()) {
            Sponge.getScheduler()
                    .createTaskBuilder()
                    .execute(() -> {
                        setRemainingAir(remainingAir);
                    })
                    .submit(SpongeLoader.instance);
            return;
        }
        handle.offer(Keys.REMAINING_AIR, remainingAir);
    }

    @Override
    public Collection<EffectInfo> getEffects() {
        checkHandle();
        Collection<EffectInfo> effectInfoCollection = new LinkedList<>();
        handle.get(Keys.POTION_EFFECTS).ifPresent(value -> {
            for (PotionEffect effect : value) {
                effectInfoCollection.add(EffectInfo.create(effect.getType().getId(), effect.getAmplifier(), effect.getDuration()));
            }
        });
        return effectInfoCollection;
    }

    @Override
    public void setEffects(Collection<EffectInfo> effects) {
        checkHandle();
        if (!Sponge.getServer().isMainThread()) {
            Sponge.getScheduler()
                    .createTaskBuilder()
                    .execute(() -> {
                        setEffects(effects);
                    })
                    .submit(SpongeLoader.instance);
            return;
        }
        List<PotionEffect> potionEffects = new LinkedList<>();
        for (EffectInfo effect : effects) {
            for (Field field : PotionEffectTypes.class.getFields()) {
                if (field.getType() == PotionEffectType.class
                        && Modifier.isStatic(field.getModifiers())) {
                    try {
                        if (((PotionEffectType) field.get(PotionEffect.class)).getId().equals(effect.type)) {
                            potionEffects.add(
                                    PotionEffect.builder()
                                            .potionType(((PotionEffectType) field.get(PotionEffect.class)))
                                            .amplifier(effect.amplifier)
                                            .duration(effect.duration)
                                            .particles(true)
                                            .ambience(false)
                                            .build());
                            break;
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        handle.offer(Keys.POTION_EFFECTS, potionEffects);
    }

    public boolean isOnline() {
        if (handle == null) {
            Optional<Player> optionalPlayer = Sponge.getServer().getPlayer(getUniqueId());
            return optionalPlayer.map(User::isOnline).orElse(false);
        } else {
            return handle.isOnline();
        }
    }

    @Override
    public void setPlayerInfo(PlayerInfo playerInfo) {
        if (!Sponge.getServer().isMainThread()) {
            Sponge.getScheduler()
                    .createTaskBuilder()
                    .execute(() -> {
                        setPlayerInfo(playerInfo);
                    })
                    .submit(SpongeLoader.instance);
            return;
        }
        super.setPlayerInfo(playerInfo);
    }
}

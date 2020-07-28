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

package red.mohist.sodionauth.bukkit.implementation;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import red.mohist.sodionauth.bukkit.BukkitLoader;
import red.mohist.sodionauth.core.modules.*;
import red.mohist.sodionauth.core.utils.Helper;

import java.net.InetAddress;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BukkitPlayer extends AbstractPlayer {
    private Player handle;

    public BukkitPlayer(Player handle) {
        super(handle.getName(), handle.getUniqueId(), Objects.requireNonNull(handle.getAddress()).getAddress());
        this.handle = handle;
    }

    public BukkitPlayer(String name, UUID uuid, InetAddress address) {
        super(name, uuid, address);
    }

    public void checkHandle() {
        if (handle == null) {
            handle = Bukkit.getPlayer(getUniqueId());
            if (handle == null) {
                Helper.getLogger().warn("No player can be call.");
            }
        }
    }

    @Override
    public void sendMessage(String message) {
        handle.sendMessage(message);
    }

    @Override
    public CompletableFuture<Boolean> teleport(LocationInfo location) {
        try {
            try {
                return handle.teleportAsync(new Location(Bukkit.getWorld(location.world),
                        location.x, location.y, location.z, location.yaw, location.pitch));
            } catch (NoSuchMethodError e) {
                BukkitLoader.instance.getLogger()
                        .warning("You are not running Paper? Using synchronized teleport.");
                CompletableFuture<Boolean> booleanCompletableFuture = new CompletableFuture<>();
                Bukkit.getScheduler().runTask(BukkitLoader.instance, () ->
                        booleanCompletableFuture.complete(handle.teleport(new Location(Bukkit.getWorld(location.world),
                                location.x, location.y, location.z, location.yaw, location.pitch))));
                return booleanCompletableFuture;
            }
        } catch (IllegalPluginAccessException e) {
            CompletableFuture<Boolean> r = new CompletableFuture<>();
            r.complete(false);
            return r;
        }
    }

    @Override
    public void kick(String message) {
        Bukkit.getScheduler().runTask(BukkitLoader.instance, () -> handle.kickPlayer(message));
    }

    @Override
    public LocationInfo getLocation() {
        checkHandle();
        final Location holderLocation = handle.getLocation();
        return new LocationInfo(
                holderLocation.getWorld().getName(),
                holderLocation.getX(),
                holderLocation.getY(),
                holderLocation.getZ(),
                holderLocation.getYaw(),
                holderLocation.getPitch()
        );
    }

    @Override
    public void setLocation(LocationInfo location) {
        teleport(location);
    }

    @Override
    public int getGameMode() {
        return handle.getGameMode().getValue();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setGameMode(int gameMode) {
        GameMode gm = GameMode.getByValue(gameMode);
        if (gm != null) {
            Bukkit.getScheduler().runTask(BukkitLoader.instance, () ->
                    handle.setGameMode(gm));
        } else {
            BukkitLoader.instance.getLogger().warning("fail to set gamemode" + gameMode);
        }
    }

    @Override
    public double getHealth() {
        checkHandle();
        return handle.getHealth();
    }

    @Override
    public void setHealth(double health) {
        checkHandle();
        handle.setHealth(health);
    }

    @Override
    public double getMaxHealth() {
        checkHandle();
        return handle.getMaxHealth();
    }

    @Override
    public void setMaxHealth(double maxHealth) {
        checkHandle();
        handle.setMaxHealth(maxHealth);
    }

    @Override
    public float getFallDistance() {
        checkHandle();
        return handle.getFallDistance();
    }

    @Override
    public void setFallDistance(float fallDistance) {
        checkHandle();
        handle.setFallDistance(fallDistance);
    }

    @Override
    public VelocityInfo getVelocity() {
        checkHandle();
        Vector v3d = handle.getVelocity();
        return VelocityInfo.create(v3d.getX(), v3d.getY(), v3d.getZ());
    }

    @Override
    public void setVelocity(VelocityInfo velocity) {
        checkHandle();
        handle.setVelocity(new Vector(velocity.x, velocity.y, velocity.z));
    }

    @Override
    public FoodInfo getFood() {
        checkHandle();
        return FoodInfo.create(
                handle.getFoodLevel(),
                handle.getExhaustion(),
                handle.getSaturation()
        );
    }

    @Override
    public void setFood(FoodInfo food) {
        checkHandle();
        handle.setFoodLevel(food.foodLevel);
        handle.setExhaustion((float) food.exhaustion);
        handle.setSaturation((float) food.saturation);
    }

    @Override
    public int getRemainingAir() {
        checkHandle();
        return handle.getRemainingAir();
    }

    @Override
    public void setRemainingAir(int remainingAir) {
        checkHandle();
        handle.setRemainingAir(remainingAir);
    }

    @Override
    public Collection<EffectInfo> getEffects() {
        checkHandle();
        Collection<EffectInfo> effects = new LinkedList<>();
        for (PotionEffect effect : handle.getActivePotionEffects()) {
            effects.add(EffectInfo.create(effect.getType().getName(), effect.getAmplifier(), effect.getDuration()));
        }
        return effects;
    }

    @Override
    public void setEffects(Collection<EffectInfo> effects) {
        checkHandle();
        for (PotionEffect effect : handle.getActivePotionEffects()) {
            handle.removePotionEffect(effect.getType());
        }
        for (EffectInfo effect : effects) {
            PotionEffectType potionEffectType = PotionEffectType.getByName(effect.type);
            if (potionEffectType != null) {
                handle.addPotionEffect(new PotionEffect(
                        potionEffectType,
                        effect.duration,
                        effect.amplifier
                ));
            }
        }
    }

    @Override
    public boolean isOnline() {
        if (handle == null) {
            handle = Bukkit.getPlayer(getUniqueId());
        }
        if (handle == null) {
            return false;
        }
        return handle.isOnline();
    }

    @Override
    public void setPlayerInfo(PlayerInfo playerInfo) {
        if (!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(BukkitLoader.instance, () -> {
                setPlayerInfo(playerInfo);
            });
            return;
        }
        super.setPlayerInfo(playerInfo);
    }
}

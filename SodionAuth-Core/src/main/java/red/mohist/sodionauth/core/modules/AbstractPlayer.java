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

package red.mohist.sodionauth.core.modules;


import red.mohist.sodionauth.core.config.LangConfiguration;
import red.mohist.sodionauth.core.services.Service;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.Lang;

import java.net.InetAddress;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractPlayer {
    protected final String name;
    protected final UUID uuid;
    protected final InetAddress address;

    public AbstractPlayer(String name, UUID uuid, InetAddress address) {
        this.name = name;
        this.uuid = uuid;
        this.address = address;
    }

    public PlayerStatus getStatus() {
        return Service.auth.logged_in.get(getUniqueId());
    }

    public LangConfiguration getLang() {
        return Lang.get(Config.defaultLang);
    }

    public String getName() {
        return name;
    }

    public InetAddress getAddress() {
        return address;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public abstract void sendMessage(String message);

    // as Bungee
    public abstract void sendServerData(String channel, byte[] data);

    // as Bukkit
    public abstract void sendClientData(String channel, byte[] data);

    @SuppressWarnings("UnusedReturnValue")
    public abstract CompletableFuture<Boolean> teleport(LocationInfo location);

    public abstract void kick(String message);

    public abstract LocationInfo getLocation();

    public abstract void setLocation(LocationInfo location);

    public abstract int getGameMode();

    public abstract void setGameMode(int gameMode);

    public abstract double getHealth();

    public abstract void setHealth(double health);

    public abstract double getMaxHealth();

    public abstract void setMaxHealth(double maxHealth);

    public abstract float getFallDistance();

    public abstract void setFallDistance(float fallDistance);

    public abstract VelocityInfo getVelocity();

    public abstract void setVelocity(VelocityInfo velocity);

    public abstract FoodInfo getFood();

    public abstract void setFood(FoodInfo food);

    public abstract int getRemainingAir();

    public abstract void setRemainingAir(int remainingAir);

    public abstract Collection<EffectInfo> getEffects();

    public abstract void setEffects(Collection<EffectInfo> effects);

    public PlayerInfo getPlayerInfo() {
        PlayerInfo playerInfo = new PlayerInfo();
        playerInfo.location = getLocation();
        playerInfo.gameMode = getGameMode();
        playerInfo.health = getHealth();
        playerInfo.maxHealth = getMaxHealth();
        playerInfo.fallDistance = getFallDistance();
        playerInfo.velocity = getVelocity();
        playerInfo.food = getFood();
        playerInfo.remainingAir = getRemainingAir();
        playerInfo.effects = getEffects();
        return playerInfo;
    }

    public void setPlayerInfo(PlayerInfo playerInfo) {
        if (Config.teleport.tpBackAfterLogin) {
            setLocation(playerInfo.location);
        }
        setGameMode(playerInfo.gameMode);
        setHealth(playerInfo.health);
        setMaxHealth(playerInfo.maxHealth);
        setFallDistance(playerInfo.fallDistance);
        setVelocity(playerInfo.velocity);
        setFood(playerInfo.food);
        setRemainingAir(playerInfo.remainingAir);
        setEffects(playerInfo.effects);
    }

    public abstract boolean isOnline();
}

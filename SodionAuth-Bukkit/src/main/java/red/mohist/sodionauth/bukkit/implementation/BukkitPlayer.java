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
import org.bukkit.util.Vector;
import red.mohist.sodionauth.bukkit.BukkitLoader;
import red.mohist.sodionauth.core.modules.*;
import red.mohist.sodionauth.core.utils.Helper;

import java.net.InetAddress;
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
    public void checkHandle(){
        if(handle==null){
            handle=Bukkit.getPlayer(getUniqueId());
            if(handle==null){
                Helper.getLogger().warn("No player can be call.");
            }
        }
    }
    @Override
    public void sendMessage(String message) {
        checkHandle();
        handle.sendMessage(message);
    }

    @Override
    public CompletableFuture<Boolean> teleport(LocationInfo location) {
        checkHandle();
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
        checkHandle();
        if(Bukkit.isPrimaryThread()){
            handle.kickPlayer(message);
        }else {
            Bukkit.getScheduler().runTask(BukkitLoader.instance, () -> handle.kickPlayer(message));
        }
    }

    private LocationInfo getLocation() {
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

    private void setLocation(LocationInfo location) {
        teleport(location);
    }

    public int getGameMode() {
        checkHandle();
        return handle.getGameMode().getValue();
    }

    public void setGameMode(int gameMode) {
        checkHandle();
        GameMode gm = GameMode.getByValue(gameMode);
        if (gm != null) {
            Bukkit.getScheduler().runTask(BukkitLoader.instance, () ->
                    handle.setGameMode(gm));
        } else {
            BukkitLoader.instance.getLogger().warning("fail to set gamemode" + gameMode);
        }
    }

    @Override
    public PlayerInfo getPlayerInfo() {
        checkHandle();
        PlayerInfo playerInfo = new PlayerInfo();
        playerInfo.locationInfo=getLocation();
        playerInfo.gameMode=getGameMode();
        playerInfo.health=handle.getHealth();
        playerInfo.maxHealth=handle.getMaxHealth();
        playerInfo.fallDistance=handle.getFallDistance();
        Vector v3d = handle.getVelocity();
        playerInfo.velocityInfo= VelocityInfo.create(v3d.getX(),v3d.getY(),v3d.getZ());
        playerInfo.foodInfo=FoodInfo.create(
                handle.getFoodLevel(),
                handle.getExhaustion(),
                handle.getSaturation()
        );
        playerInfo.remainingAir=handle.getRemainingAir();
        return playerInfo;
    }

    @Override
    public void setPlayerInfo(PlayerInfo playerInfo) {
        checkHandle();
        if(playerInfo.locationInfo != null) {
            setLocation(playerInfo.locationInfo);
        }
        if(playerInfo.gameMode != null) {
            setGameMode(playerInfo.gameMode);
        }
    }

    @Override
    public boolean isOnline() {
        if(handle==null){
            handle=Bukkit.getPlayer(getUniqueId());
        }
        if(handle==null){
            return false;
        }
        return handle.isOnline();
    }
}

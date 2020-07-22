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
import red.mohist.sodionauth.bukkit.BukkitLoader;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.modules.LocationInfo;

import java.net.InetAddress;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BukkitPlainPlayer extends AbstractPlayer {

    public BukkitPlainPlayer(String name, UUID uuid, InetAddress address) {
        super(name, uuid, address);
    }

    @Override
    public void sendMessage(String message) {
        Objects.requireNonNull(Bukkit.getPlayer(getUniqueId())).sendMessage(message);
    }

    @Override
    public CompletableFuture<Boolean> teleport(LocationInfo location) {
        try {
            try {
                return Objects.requireNonNull(Bukkit.getPlayer(getUniqueId())).teleportAsync(new Location(Bukkit.getWorld(location.world),
                        location.x, location.y, location.z, location.yaw, location.pitch));
            } catch (NoSuchMethodError e) {
                BukkitLoader.instance.getLogger()
                        .warning("You are not running Paper? Using synchronized teleport.");
                CompletableFuture<Boolean> booleanCompletableFuture = new CompletableFuture<>();
                Bukkit.getScheduler().runTask(BukkitLoader.instance, () ->
                        booleanCompletableFuture.complete(Objects.requireNonNull(Bukkit.getPlayer(getUniqueId())).teleport(new Location(Bukkit.getWorld(location.world),
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
        Objects.requireNonNull(Bukkit.getPlayer(getUniqueId())).kickPlayer(message);
    }

    @Override
    public LocationInfo getLocation() {
        final Location location = Objects.requireNonNull(Bukkit.getPlayer(getUniqueId())).getLocation();
        return new LocationInfo(
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        );
    }

    @Override
    public int getGameMode() {
        Player player = Bukkit.getPlayer(getUniqueId());
        if (player != null) {
            return player.getGameMode().getValue();
        } else {
            BukkitLoader.instance.getLogger().warning("fail to get gamemode");
            return 0;
        }
    }

    @Override
    public void setGameMode(int gameMode) {
        Player player = Bukkit.getPlayer(getUniqueId());
        if (player != null) {
            GameMode gm = GameMode.getByValue(gameMode);
            if (gm != null) {
                player.setGameMode(gm);
            } else {
                BukkitLoader.instance.getLogger().warning("fail to set gamemode" + gameMode);
            }
        }
    }

    @Override
    public boolean isOnline() {
        return Objects.requireNonNull(Bukkit.getPlayer(getUniqueId())).isOnline();
    }
}

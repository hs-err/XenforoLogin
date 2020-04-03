/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.bukkit.implementation;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import red.mohist.xenforologin.bukkit.BukkitLoader;
import red.mohist.xenforologin.core.modules.AbstractPlayer;
import red.mohist.xenforologin.core.modules.LocationInfo;

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
            return Objects.requireNonNull(Bukkit.getPlayer(getUniqueId()))
                    .teleportAsync(new Location(Bukkit.getWorld(location.world),
                            location.x, location.y, location.z, location.yaw, location.pitch));
        } catch (NoSuchMethodError error) {
            BukkitLoader.instance.getSLF4JLogger()
                    .debug("You are not running Paper? Using synchronized teleport.", error);
            CompletableFuture<Boolean> booleanCompletableFuture = new CompletableFuture<>();
            Bukkit.getScheduler().runTask(BukkitLoader.instance, () ->
                    booleanCompletableFuture.complete(Objects.requireNonNull(Bukkit.getPlayer(getUniqueId()))
                            .teleport(new Location(Bukkit.getWorld(location.world),
                                    location.x, location.y, location.z, location.yaw, location.pitch))));
            return booleanCompletableFuture;
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
    public int getGamemode() {
        Player player = Bukkit.getPlayer(getUniqueId());
        if (player != null) {
            return player.getGameMode().getValue();
        } else {
            BukkitLoader.instance.getLogger().warning("fail to get gamemode");
            return 0;
        }
    }

    @Override
    public void setGamemode(int gamemode) {
        Player player = Bukkit.getPlayer(getUniqueId());
        if (player != null) {
            GameMode gm = GameMode.getByValue(gamemode);
            if (gm != null) {
                player.setGameMode(gm);
            } else {
                BukkitLoader.instance.getLogger().warning("fail to set gamemode" + gamemode);
            }
        }
    }

    @Override
    public boolean isOnline() {
        return Objects.requireNonNull(Bukkit.getPlayer(getUniqueId())).isOnline();
    }
}

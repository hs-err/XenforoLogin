/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.bukkit.implementation;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import red.mohist.xenforologin.bukkit.BukkitLoader;
import red.mohist.xenforologin.core.modules.AbstractPlayer;
import red.mohist.xenforologin.core.modules.LocationInfo;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class BukkitPlayer extends AbstractPlayer {
    private final Player handle;

    public BukkitPlayer(Player handle) {
        super(handle.getName(), handle.getUniqueId(), Objects.requireNonNull(handle.getAddress()).getAddress());
        this.handle = handle;
    }

    public Player getHandle() {
        return handle;
    }

    @Override
    public void sendMessage(String message) {
        handle.sendMessage(message);
    }

    @Override
    public CompletableFuture<Boolean> teleport(LocationInfo location) {
        try {
            return handle.teleportAsync(new Location(Bukkit.getWorld(location.world),
                    location.x, location.y, location.z, location.yaw, location.pitch));
        } catch (NoSuchMethodError error) {
            BukkitLoader.instance.getSLF4JLogger()
                    .debug("You are not running Paper? Using synchronized teleport.", error);
            CompletableFuture<Boolean> booleanCompletableFuture = new CompletableFuture<>();
            Bukkit.getScheduler().runTask(BukkitLoader.instance, () ->
                    booleanCompletableFuture.complete(handle.teleport(new Location(Bukkit.getWorld(location.world),
                            location.x, location.y, location.z, location.yaw, location.pitch))));
            return booleanCompletableFuture;
        }
    }

    @Override
    public void kick(String message) {
        Bukkit.getScheduler().runTask(BukkitLoader.instance, () -> handle.kickPlayer(message));
    }

    @Override
    public LocationInfo getLocation() {
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
    public boolean isOnline() {
        return handle.isOnline();
    }
}

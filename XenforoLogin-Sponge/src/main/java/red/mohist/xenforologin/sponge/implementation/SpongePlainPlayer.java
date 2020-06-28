/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.sponge.implementation;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import red.mohist.xenforologin.core.modules.AbstractPlayer;
import red.mohist.xenforologin.core.modules.LocationInfo;

import java.net.InetAddress;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SpongePlainPlayer extends AbstractPlayer {

    public SpongePlainPlayer(String name, UUID uuid, InetAddress address) {
        super(name, uuid, address);
    }

    @Override
    public void sendMessage(String message) {
        Objects.requireNonNull(Sponge.getServer().getPlayer(getUniqueId())).get().sendMessage(Text.of(message));
    }

    @Override
    public CompletableFuture<Boolean> teleport(LocationInfo location) {
        CompletableFuture<Boolean> booleanCompletableFuture = new CompletableFuture<>();
        booleanCompletableFuture.complete(Sponge.getServer().getPlayer(getUniqueId()).get().setLocationSafely(
                Sponge.getServer().getWorld(location.world).get().getLocation(
                        location.x,
                        location.y,
                        location.z
                )
        ));
        return booleanCompletableFuture;
    }

    @Override
    public void kick(String message) {
        Sponge.getServer().getPlayer(getUniqueId()).get().kick(Text.of(message));
    }

    @Override
    public LocationInfo getLocation() {
        Location<World> location = Sponge.getServer().getPlayer(getUniqueId()).get().getLocation();
        return new LocationInfo(
                location.getExtent().getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                0,0
        );
    }

    @Override
    public int getGamemode() {
        Player handle = Sponge.getServer().getPlayer(getUniqueId()).get();
        GameMode gameMode = handle.gameMode().get();
        if (GameModes.SURVIVAL.equals(gameMode)) {
            return 0;
        } else if (GameModes.CREATIVE.equals(gameMode)) {
            return 1;
        } else if (GameModes.ADVENTURE.equals(gameMode)) {
            return 2;
        } else if (GameModes.SPECTATOR.equals(gameMode)) {
            return 3;
        }else {
            return 0;
        }
    }

    @Override
    public void setGamemode(int gamemode) {
        Player handle = Sponge.getServer().getPlayer(getUniqueId()).get();
        switch (gamemode){
            case 0:
                handle.gameMode().set(GameModes.SURVIVAL);
                return;
            case 1:
                handle.gameMode().set(GameModes.CREATIVE);
                return;
            case 2:
                handle.gameMode().set(GameModes.ADVENTURE);
                return;
            case 3:
                handle.gameMode().set(GameModes.SPECTATOR);
        }
    }

    @Override
    public boolean isOnline() {
        return Sponge.getServer().getPlayer(getUniqueId()).get().isOnline();
    }
}


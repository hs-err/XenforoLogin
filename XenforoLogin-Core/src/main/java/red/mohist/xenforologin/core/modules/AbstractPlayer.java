/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.core.modules;


import java.net.InetAddress;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractPlayer {
    private String name;
    private UUID uuid;
    private InetAddress address;

    public AbstractPlayer(String name, UUID uuid, InetAddress address) {
        this.name = name;
        this.uuid = uuid;
        this.address = address;
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

    public abstract CompletableFuture<Boolean> teleport(LocationInfo location);

    public abstract void kick(String message);

    public abstract LocationInfo getLocation();

    public abstract int getGamemode();

    public abstract void setGamemode(int gamemode);

    public abstract boolean isOnline();
}

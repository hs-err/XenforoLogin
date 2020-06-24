/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.yggdrasilserver.implementation;

import red.mohist.xenforologin.core.modules.AbstractPlayer;
import red.mohist.xenforologin.core.modules.LocationInfo;

import java.net.InetAddress;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlainPlayer extends AbstractPlayer {
    public PlainPlayer(String name, UUID uuid, InetAddress address) {
        super(name, uuid, address);
    }

    @Override
    public void sendMessage(String message) {

    }

    @Override
    public CompletableFuture<Boolean> teleport(LocationInfo location) {
        return null;
    }

    @Override
    public void kick(String message) {

    }

    @Override
    public LocationInfo getLocation() {
        return null;
    }

    @Override
    public int getGamemode() {
        return 0;
    }

    @Override
    public void setGamemode(int gamemode) {

    }

    @Override
    public boolean isOnline() {
        return false;
    }
}

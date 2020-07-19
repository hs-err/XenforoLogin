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

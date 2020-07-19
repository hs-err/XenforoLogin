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

package red.mohist.xenforologin.yggdrasilserver.implementation;

import red.mohist.xenforologin.core.modules.AbstractPlayer;
import red.mohist.xenforologin.core.modules.LocationInfo;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlainPlayer extends AbstractPlayer {
    public PlainPlayer(String name) {
        super(name, UUID.randomUUID(), null);
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

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

package red.mohist.sodionauth.yggdrasilserver;

import red.mohist.sodionauth.core.interfaces.PlatformAdapter;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.modules.LocationInfo;

import java.util.Collection;

public class YggdrasilServerLoader implements PlatformAdapter {

    @Override
    public LocationInfo getSpawn(String world) {
        return null;
    }

    // TODO
    @Override
    public String getDefaultWorld() {
        return null;
    }

    @Override
    public void onLogin(AbstractPlayer player) {

    }

    @Override
    public void sendBlankInventoryPacket(AbstractPlayer player) {

    }

    @Override
    public Collection<AbstractPlayer> getAllPlayer() {
        return null;
    }
}

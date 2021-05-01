/*
 * Copyright 2021 Mohist-Community
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

package red.mohist.sodionauth.core.services;

import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;
import red.mohist.sodionauth.core.SodionAuthCore;
import red.mohist.sodionauth.core.database.entities.LastInfo;
import red.mohist.sodionauth.core.events.player.LoginEvent;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.modules.LocationInfo;
import red.mohist.sodionauth.core.modules.PlayerInfo;
import red.mohist.sodionauth.core.utils.Helper;

public class LoginService {

    public LocationInfo spawn_location;

    public LoginService() {
        spawn_location = SodionAuthCore.instance.api.getSpawn(SodionAuthCore.instance.api.getDefaultWorld());
    }

    @Subscribe
    public void onLoginIn(LoginEvent event) {
        AbstractPlayer player = event.getPlayer();
        // restore playerInfo
        LastInfo lastinfo = LastInfo.getByUuid(player.getUniqueId());
        if(lastinfo == null){
            player.setPlayerInfo(new PlayerInfo());
        }else{
            player.setPlayerInfo(new Gson().fromJson(lastinfo.getInfo(), PlayerInfo.class));
            lastinfo.delete();
        }
        SodionAuthCore.instance.api.onLogin(player);
        Helper.getLogger().info("Logging in " + player.getUniqueId());
        player.sendMessage(player.getLang().loginSuccess);
    }
}

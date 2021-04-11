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

import red.mohist.sodionauth.core.events.player.PlayerChatEvent;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.Helper;

public class UnRegisterService {
    public boolean enable;
    public UnRegisterService(){
        Helper.getLogger().info("Initializing unRegister service...");

        if(Config.api.getSystem().equals("sqlite")
                || Config.api.getSystem().equals("mysql")){
            enable=true;
        }else{
            enable=false;
        }
    }
    public void onChat(PlayerChatEvent event){
        if(event.getMessage().equals(".unregister")){
           if(!Service.auth.needCancelled(event.getPlayer())){
               event.setCancelled(true);
           }
        }
    }
}

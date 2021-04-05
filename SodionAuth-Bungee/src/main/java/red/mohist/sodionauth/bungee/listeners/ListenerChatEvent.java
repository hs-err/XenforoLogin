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

package red.mohist.sodionauth.bungee.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import red.mohist.sodionauth.bungee.implementation.BungeePlayer;
import red.mohist.sodionauth.bungee.interfaces.BungeeAPIListener;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.services.Service;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.Helper;

public class ListenerChatEvent implements BungeeAPIListener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onChatEvent(ChatEvent event){
        if(event.isCancelled()){
            return;
        }
        if(event.getSender() instanceof ProxiedPlayer){
            ProxiedPlayer bungeePlayer = (ProxiedPlayer) event.getSender();
            AbstractPlayer player=new BungeePlayer(bungeePlayer);
            if (!Service.auth.needCancelled(player)) {
                if (Config.security.getCancelChatAfterLogin(false)) {
                    Helper.getLogger().info("Why cancel chat after login in bungee?");
                    event.setCancelled(true);
                }
            }else{
                event.setCancelled(true);
                new red.mohist.sodionauth.core.events.player.ChatEvent(player,event.getMessage()).post();
            }
        }

    }
    @Override
    public void eventClass() {
        PostLoginEvent.class.getName();
    }
}
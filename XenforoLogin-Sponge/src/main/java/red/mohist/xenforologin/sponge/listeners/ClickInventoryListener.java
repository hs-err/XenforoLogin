/*
    * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
    *
    * You are not permitted to interfere any protection that prevents loading in CatServer
    *
    * Copyright (c) 2020 Mohist-Community.
    *
    */
    
    package red.mohist.xenforologin.sponge.listeners;


import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import red.mohist.xenforologin.core.XenforoLoginCore;
import red.mohist.xenforologin.sponge.implementation.SpongePlayer;
import red.mohist.xenforologin.sponge.interfaces.SpongeAPIListener;
    
    public class ClickInventoryListener implements SpongeAPIListener {
       @Listener(order = Order.FIRST, beforeModifications = true)
       public void onClickInventoryEvent(ClickInventoryEvent event, @First Player player){
           if(XenforoLoginCore.instance.needCancelled(new SpongePlayer(player))){
               event.setCancelled(true);
           }
       }
       @Override
       public void eventClass() {
        ClickInventoryEvent.class.getName();
       }
    }
    
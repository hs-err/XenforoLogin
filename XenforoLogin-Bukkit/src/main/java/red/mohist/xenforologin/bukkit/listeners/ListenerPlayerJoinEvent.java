/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.bukkit.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import red.mohist.xenforologin.bukkit.BukkitLoader;
import red.mohist.xenforologin.bukkit.implementation.BukkitPlayer;
import red.mohist.xenforologin.bukkit.interfaces.BukkitAPIListener;
import red.mohist.xenforologin.core.XenforoLoginCore;
import red.mohist.xenforologin.core.modules.AbstractPlayer;
import red.mohist.xenforologin.core.utils.LoginTicker;

public class ListenerPlayerJoinEvent implements BukkitAPIListener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void OnPlayerJoinEvent(PlayerJoinEvent event) {
        BukkitLoader.instance.sendBlankInventoryPacket(event.getPlayer());
        if (!XenforoLoginCore.instance.logged_in.containsKey(event.getPlayer().getUniqueId())) {
            BukkitLoader.instance.getLogger().warning("AsyncPlayerPreLoginEvent isn't active. It may cause some security problems.");
            BukkitLoader.instance.getLogger().warning("It's not a bug. Do NOT report this.");
            new Thread(() -> {
                AbstractPlayer player = BukkitLoader.instance.player2info(event.getPlayer());
                String canjoin = XenforoLoginCore.instance.canJoin(player);
                if (canjoin != null) {
                    player.kick(canjoin);
                }
            }).start();
        }
        if ((boolean) BukkitLoader.instance.getConfigValue("teleport.tp_spawn_before_login", true)) {
            event.getPlayer().teleport(new Location(
                    Bukkit.getWorld(XenforoLoginCore.instance.default_location.world),
                    XenforoLoginCore.instance.default_location.x,
                    XenforoLoginCore.instance.default_location.y,
                    XenforoLoginCore.instance.default_location.z,
                    XenforoLoginCore.instance.default_location.yaw,
                    XenforoLoginCore.instance.default_location.pitch
            ));
        }
        if ((boolean) BukkitLoader.instance.getConfigValue("secure.spectator_login", true)) {
            event.getPlayer().setGameMode(GameMode.SPECTATOR);
        }
        LoginTicker.add(new BukkitPlayer(event.getPlayer()));
    }

    @Override
    public void eventClass() {
        PlayerJoinEvent.class.getName();
    }
}

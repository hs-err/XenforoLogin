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
import org.spongepowered.api.event.message.MessageChannelEvent.Chat;
import org.spongepowered.api.event.network.ClientConnectionEvent.Auth;
import org.spongepowered.api.event.network.ClientConnectionEvent.Join;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;
import red.mohist.xenforologin.core.XenforoLoginCore;
import red.mohist.xenforologin.core.modules.LocationInfo;
import red.mohist.xenforologin.core.utils.Config;
import red.mohist.xenforologin.core.utils.Helper;
import red.mohist.xenforologin.core.utils.LoginTicker;
import red.mohist.xenforologin.sponge.implementation.SpongePlainPlayer;
import red.mohist.xenforologin.sponge.implementation.SpongePlayer;

public class CommonListener {
    @Listener
    public void onAsyncPlayerPreLoginEvent(Auth event, GameProfile profile) {
        String canjoin = XenforoLoginCore.instance.canJoin(new SpongePlainPlayer(
                event.getProfile().getName().get(), event.getProfile().getUniqueId(), event.getConnection().getAddress().getAddress()));
        if (canjoin != null) {
            event.setMessage(Text.of(canjoin));
            event.setCancelled(true);
        }
    }
    @Listener
    public void onPlayerJoinEvent(Join playerJoinEvent, Player spongePlayer) {
        SpongePlayer player=new SpongePlayer(spongePlayer);
        XenforoLoginCore.instance.api.sendBlankInventoryPacket(player);
        if (!XenforoLoginCore.instance.logged_in.containsKey(spongePlayer.getUniqueId())) {
            Helper.getLogger().warn("AsyncPlayerPreLoginEvent isn't active. It may cause some security problems.");
            Helper.getLogger().warn("It's not a bug. Do NOT report this.");
            new Thread(() -> {
                String canjoin = XenforoLoginCore.instance.canJoin(player);
                if (canjoin != null) {
                    player.kick(canjoin);
                }
            }).start();
        }
        if (Config.getBoolean("teleport.tp_spawn_before_login", true)) {
            player.teleport(new LocationInfo(
                    XenforoLoginCore.instance.default_location.world,
                    XenforoLoginCore.instance.default_location.x,
                    XenforoLoginCore.instance.default_location.y,
                    XenforoLoginCore.instance.default_location.z,
                    XenforoLoginCore.instance.default_location.yaw,
                    XenforoLoginCore.instance.default_location.pitch
            ));
        }
        if (Config.getBoolean("secure.spectator_login", true)) {
            //event.getPlayer().setGameMode(GameMode.SPECTATOR);
        }
        LoginTicker.add(player);
    }

    @Listener
    public void onChatEvent(Chat event, Player spongePlayer) {
        SpongePlayer player=new SpongePlayer(spongePlayer);
        if (!XenforoLoginCore.instance.needCancelled(player)) {
            if (Config.getBoolean("secure.cancel_chat_after_login", false)) {
                player.sendMessage(Helper.langFile("logged_in"));
                event.setCancelled(true);
            }
            return;
        }
        event.setCancelled(true);
        XenforoLoginCore.instance.onChat(player, event.getMessage().toPlain());
    }
}

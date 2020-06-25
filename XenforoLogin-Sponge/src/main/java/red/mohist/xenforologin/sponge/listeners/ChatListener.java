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
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.message.MessageChannelEvent.Chat;
import red.mohist.xenforologin.core.XenforoLoginCore;
import red.mohist.xenforologin.core.utils.Config;
import red.mohist.xenforologin.core.utils.Helper;
import red.mohist.xenforologin.sponge.implementation.SpongePlayer;
import red.mohist.xenforologin.sponge.interfaces.SpongeAPIListener;

public class ChatListener implements SpongeAPIListener {

    @Listener
    public void onChatEvent(Chat event, @First Player spongePlayer) {
        SpongePlayer player=new SpongePlayer(spongePlayer);
        if (!XenforoLoginCore.instance.needCancelled(player)) {
            if (Config.getBoolean("secure.cancel_chat_after_login", false)) {
                player.sendMessage(Helper.langFile("logged_in"));
                event.setCancelled(true);
            }
            return;
        }
        event.setCancelled(true);
        XenforoLoginCore.instance.onChat(player, event.getRawMessage().toPlainSingle());
    }

    @Override
    public void eventClass() {
        Chat.class.getName();
    }
}

package red.mohist.xenforologin.bukkit.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import red.mohist.xenforologin.bukkit.implementation.BukkitPlainPlayer;
import red.mohist.xenforologin.core.XenforoLoginCore;

public class ListenerAsyncPlayerPreLoginEvent implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {
        String canjoin = XenforoLoginCore.instance.canJoin(new BukkitPlainPlayer(
                event.getName(), event.getUniqueId(), event.getAddress()));
        if (canjoin != null) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, canjoin);
        }
    }
}

package red.mohist.xenforologin.bukkit.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import red.mohist.xenforologin.bukkit.BukkitLoader;
import red.mohist.xenforologin.core.XenforoLoginCore;

public class ListenerPlayerQuitEvent implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void OnQuit(PlayerQuitEvent event) {
        XenforoLoginCore.instance.onQuit(BukkitLoader.instance.player2info(event.getPlayer()));
    }
}

package red.mohist.xenforologin.listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import red.mohist.xenforologin.BukkitLoader;
import red.mohist.xenforologin.XenforoLogin;
import red.mohist.xenforologin.modules.PlayerInfo;

import java.io.IOException;

public class ListenerPlayerQuitEvent implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void OnQuit(PlayerQuitEvent event) {
        XenforoLogin.instance.onQuit(BukkitLoader.instance.player2info(event.getPlayer()));
    }
}

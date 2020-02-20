package red.mohist.xenforologin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import red.mohist.xenforologin.Main;
import red.mohist.xenforologin.interfaces.BukkitAPIListener;

public class ListenerPlayerCommandPreProcessEvent implements BukkitAPIListener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommandPreProcess(PlayerCommandPreprocessEvent event) {
        if (Main.instance.needCancelled(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @Override
    public void eventClass() {
        PlayerCommandPreprocessEvent.class.getName();
    }
}

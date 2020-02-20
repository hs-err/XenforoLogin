package red.mohist.xenforologin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerShearEntityEvent;
import red.mohist.xenforologin.Main;
import red.mohist.xenforologin.interfaces.BukkitAPIListener;

public class ListenerPlayerShearEntityEvent implements BukkitAPIListener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnPlayerShearEntityEvent(PlayerShearEntityEvent event) {
        if (Main.instance.needCancelled(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @Override
    public void eventClass() {
        PlayerShearEntityEvent.class.getName();
    }
}

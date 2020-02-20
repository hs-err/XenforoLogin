package red.mohist.xenforologin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerPickupItemEvent;
import red.mohist.xenforologin.Main;
import red.mohist.xenforologin.interfaces.BukkitAPIListener;

@SuppressWarnings("deprecation")
public class ListenerPlayerPickupItemEvent implements BukkitAPIListener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnPlayerPickupItemEvent(PlayerPickupItemEvent event) {
        if (Main.instance.needCancelled(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @Override
    public void eventClass() {
        PlayerPickupItemEvent.class.getName();
    }
}

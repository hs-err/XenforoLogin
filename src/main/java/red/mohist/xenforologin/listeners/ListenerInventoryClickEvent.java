package red.mohist.xenforologin.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import red.mohist.xenforologin.Main;
import red.mohist.xenforologin.interfaces.BukkitAPIListener;

public class ListenerInventoryClickEvent implements BukkitAPIListener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnInventoryClickEvent(InventoryClickEvent event) {
        if (Main.instance.needCancelled((Player) event.getWhoClicked())) {
            event.setCancelled(true);
        }
    }

    @Override
    public void eventClass() {
        InventoryClickEvent.class.getName();
    }
}

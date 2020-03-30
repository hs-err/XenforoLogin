package red.mohist.xenforologin.bukkit.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryDragEvent;
import red.mohist.xenforologin.XenforoLogin;
import red.mohist.xenforologin.bukkit.BukkitLoader;
import red.mohist.xenforologin.core.interfaces.BukkitAPIListener;

public class ListenerInventoryDragEvent implements BukkitAPIListener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnInventoryDragEvent(InventoryDragEvent event) {
        if (XenforoLogin.instance.needCancelled(BukkitLoader.instance.player2info((Player) event.getWhoClicked()))) {
            event.setCancelled(true);
        }
    }

    @Override
    public void eventClass() {
        InventoryDragEvent.class.getName();
    }
}

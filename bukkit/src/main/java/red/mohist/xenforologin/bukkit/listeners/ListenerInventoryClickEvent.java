package red.mohist.xenforologin.bukkit.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import red.mohist.xenforologin.XenforoLogin;
import red.mohist.xenforologin.bukkit.BukkitLoader;
import red.mohist.xenforologin.core.interfaces.BukkitAPIListener;

public class ListenerInventoryClickEvent implements BukkitAPIListener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnInventoryClickEvent(InventoryClickEvent event) {
        if (XenforoLogin.instance.needCancelled(BukkitLoader.instance.player2info((Player) event.getWhoClicked()))) {
            event.setCancelled(true);
        }
    }

    @Override
    public void eventClass() {
        InventoryClickEvent.class.getName();
    }
}

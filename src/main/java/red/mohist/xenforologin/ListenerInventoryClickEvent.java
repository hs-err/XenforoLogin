package red.mohist.xenforologin;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ListenerInventoryClickEvent implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnInventoryClickEvent(InventoryClickEvent event){
        if(Main.instance.needcancelled((Player)event.getWhoClicked())){
            event.setCancelled(true);
        }
    }
}

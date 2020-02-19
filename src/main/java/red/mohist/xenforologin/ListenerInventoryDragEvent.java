package red.mohist.xenforologin;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;

public class ListenerInventoryDragEvent implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnInventoryDragEvent(InventoryDragEvent event){
        if(Main.instance.needcancelled((Player)event.getWhoClicked())){
            event.setCancelled(true);
        }
    }
}

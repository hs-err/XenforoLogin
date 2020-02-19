package red.mohist.xenforologin;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class ListenerInventoryOpenEvent implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnInventoryOpenEvent(InventoryOpenEvent event){
        if(Main.instance.needcancelled((Player) event.getPlayer())){
            event.setCancelled(true);
        }
    }
}

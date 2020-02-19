package red.mohist.xenforologin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;

public class ListenerBlockDropItemEvent implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnBlockDropItemEvent(BlockDropItemEvent event){
        if(Main.instance.needcancelled(event.getPlayer())){
            event.setCancelled(true);
        }
    }
}

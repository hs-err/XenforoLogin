package red.mohist.xenforologin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class ListenerBlockPlaceEvent implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnBlockPlaceEvent(BlockPlaceEvent event){
        if(Main.instance.needcancelled(event.getPlayer())){
            event.setCancelled(true);
        }
    }
}

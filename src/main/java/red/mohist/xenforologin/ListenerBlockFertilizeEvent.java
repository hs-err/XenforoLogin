package red.mohist.xenforologin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFertilizeEvent;

public class ListenerBlockFertilizeEvent implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnBlockFertilizeEvent(BlockFertilizeEvent event){
        if(Main.instance.needcancelled(event.getPlayer())){
            event.setCancelled(true);
        }
    }
}

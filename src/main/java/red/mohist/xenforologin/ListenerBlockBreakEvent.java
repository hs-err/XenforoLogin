package red.mohist.xenforologin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class ListenerBlockBreakEvent implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnBlockBreakEvent(BlockBreakEvent event){
        if(Main.instance.needcancelled(event.getPlayer())){
            event.setCancelled(true);
        }
    }
}

package red.mohist.xenforologin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupArrowEvent;

public class ListenerPlayerPickupArrowEvent implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnPlayerPickupArrowEvent(PlayerPickupArrowEvent event){
        if(Main.instance.needcancelled(event.getPlayer())){
            event.setCancelled(true);
        }
    }
}

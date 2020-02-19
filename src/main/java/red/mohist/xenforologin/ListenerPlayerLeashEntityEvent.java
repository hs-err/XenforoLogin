package red.mohist.xenforologin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerLeashEntityEvent;

public class ListenerPlayerLeashEntityEvent implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnPlayerLeashEntityEvent(PlayerLeashEntityEvent event){
        if(Main.instance.needcancelled(event.getPlayer())){
            event.setCancelled(true);
        }
    }
}

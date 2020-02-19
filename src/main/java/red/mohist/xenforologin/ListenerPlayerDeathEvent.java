package red.mohist.xenforologin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class ListenerPlayerDeathEvent implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnPlayerDeathEvent(PlayerDeathEvent event){
        if(Main.instance.needcancelled(event.getEntity())){
            event.setCancelled(true);
        }
    }
}

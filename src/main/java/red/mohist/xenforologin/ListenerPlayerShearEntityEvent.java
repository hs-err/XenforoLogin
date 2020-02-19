package red.mohist.xenforologin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerShearEntityEvent;

public class ListenerPlayerShearEntityEvent implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnPlayerShearEntityEvent(PlayerShearEntityEvent event){
        if(Main.instance.needcancelled(event.getPlayer())){
            event.setCancelled(true);
        }
    }
}

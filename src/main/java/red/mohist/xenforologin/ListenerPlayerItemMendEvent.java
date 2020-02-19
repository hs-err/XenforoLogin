package red.mohist.xenforologin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemMendEvent;

public class ListenerPlayerItemMendEvent implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnPlayerItemMendEvent(PlayerItemMendEvent event){
        if(Main.instance.needcancelled(event.getPlayer())){
            event.setCancelled(true);
        }
    }
}

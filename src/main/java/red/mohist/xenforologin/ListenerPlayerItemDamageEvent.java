package red.mohist.xenforologin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;

public class ListenerPlayerItemDamageEvent implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnPlayerItemDamageEvent(PlayerItemDamageEvent event){
        if(Main.instance.needcancelled(event.getPlayer())){
            event.setCancelled(true);
        }
    }
}

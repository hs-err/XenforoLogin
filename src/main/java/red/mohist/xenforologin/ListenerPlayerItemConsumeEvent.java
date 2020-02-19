package red.mohist.xenforologin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class ListenerPlayerItemConsumeEvent implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnPlayerItemConsumeEvent(PlayerItemConsumeEvent event){
        if(Main.instance.needcancelled(event.getPlayer())){
            event.setCancelled(true);
        }
    }
}

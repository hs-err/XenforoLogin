package red.mohist.xenforologin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class ListenerPlayerToggleSneakEvent implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnPlayerToggleSneakEvent(PlayerToggleSneakEvent event){
        if(Main.instance.needcancelled(event.getPlayer())){
            event.setCancelled(true);
        }
    }
}

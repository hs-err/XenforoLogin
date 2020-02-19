package red.mohist.xenforologin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;

public class ListenerPlayerTakeLecternBookEvent implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnPlayerTakeLecternBookEvent(PlayerTakeLecternBookEvent event){
        if(Main.instance.needcancelled(event.getPlayer())){
            event.setCancelled(true);
        }
    }
}

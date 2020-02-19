package red.mohist.xenforologin;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;


public class ListenerPlayerMoveEvent implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnMove(PlayerMoveEvent event){
        if(Main.instance.needcancelled(event.getPlayer())){
            Location location=event.getTo();
            location.setX(Main.instance.default_location.getX());
            location.setZ(Main.instance.default_location.getZ());
            event.setTo(location);
        }
    }
}

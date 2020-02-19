package red.mohist.xenforologin;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ListenerEntityDamageByEntityEvent implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnEntityDamageByEntityEvent(EntityDamageByEntityEvent event){
        if(event.getEntityType()== EntityType.PLAYER){
            if(Main.instance.needcancelled((Player)event.getEntity())){
                event.setCancelled(true);
            }
        }
        if(event.getDamager().getType()== EntityType.PLAYER){
            if(Main.instance.needcancelled((Player)event.getDamager())){
                event.setCancelled(true);
            }
        }
    }
}

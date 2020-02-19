package red.mohist.xenforologin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.raid.RaidTriggerEvent;

public class ListenerRaidTriggerEvent implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnRaidTriggerEvent(RaidTriggerEvent event){
        if(Main.instance.needcancelled(event.getPlayer())){
            event.setCancelled(true);
        }
    }
}

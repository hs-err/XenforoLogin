package red.mohist.xenforologin.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import red.mohist.xenforologin.XenforoLogin;
import red.mohist.xenforologin.interfaces.BukkitAPIListener;

public class ListenerEntityDamageByEntityEvent implements BukkitAPIListener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            if (XenforoLogin.instance.needCancelled((Player) event.getEntity())) {
                event.setCancelled(true);
            }
        }
        if (event.getDamager().getType() == EntityType.PLAYER) {
            if (XenforoLogin.instance.needCancelled((Player) event.getDamager())) {
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void eventClass() {
        EntityDamageByEntityEvent.class.getName();
    }
}

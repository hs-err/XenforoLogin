package red.mohist.xenforologin.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import red.mohist.xenforologin.BukkitLoader;
import red.mohist.xenforologin.XenforoLogin;
import red.mohist.xenforologin.interfaces.BukkitAPIListener;

public class ListenerEntityDamageEvent implements BukkitAPIListener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnEntityDamageEvent(EntityDamageEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            if (XenforoLogin.instance.needCancelled(BukkitLoader.instance.player2info((Player)event.getEntity()))) {
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void eventClass() {
        EntityDamageEvent.class.getName();
    }
}

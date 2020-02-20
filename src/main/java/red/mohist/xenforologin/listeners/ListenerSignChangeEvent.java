package red.mohist.xenforologin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.SignChangeEvent;
import red.mohist.xenforologin.Main;
import red.mohist.xenforologin.interfaces.BukkitAPIListener;

public class ListenerSignChangeEvent implements BukkitAPIListener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnSignChangeEvent(SignChangeEvent event) {
        if (Main.instance.needCancelled(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @Override
    public void eventClass() {
        SignChangeEvent.class.getName();
    }
}

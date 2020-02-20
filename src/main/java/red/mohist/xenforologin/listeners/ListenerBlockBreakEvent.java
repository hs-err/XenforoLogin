package red.mohist.xenforologin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import red.mohist.xenforologin.Main;
import red.mohist.xenforologin.interfaces.BukkitAPIListener;

public class ListenerBlockBreakEvent implements BukkitAPIListener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnBlockBreakEvent(BlockBreakEvent event) {
        if (Main.instance.needCancelled(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @Override
    public void eventClass() {
        BlockBreakEvent.class.getName();
    }
}

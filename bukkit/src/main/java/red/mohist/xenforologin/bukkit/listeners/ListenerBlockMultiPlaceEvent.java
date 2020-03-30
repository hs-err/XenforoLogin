package red.mohist.xenforologin.bukkit.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import red.mohist.xenforologin.XenforoLogin;
import red.mohist.xenforologin.bukkit.BukkitLoader;
import red.mohist.xenforologin.core.interfaces.BukkitAPIListener;

public class ListenerBlockMultiPlaceEvent implements BukkitAPIListener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnBlockMultiPlaceEvent(BlockMultiPlaceEvent event) {
        if (XenforoLogin.instance.needCancelled(BukkitLoader.instance.player2info(event.getPlayer()))) {
            event.setCancelled(true);
        }
    }

    @Override
    public void eventClass() {
        BlockMultiPlaceEvent.class.getName();
    }
}

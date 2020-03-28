package red.mohist.xenforologin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockFertilizeEvent;
import red.mohist.xenforologin.BukkitLoader;
import red.mohist.xenforologin.XenforoLogin;
import red.mohist.xenforologin.interfaces.BukkitAPIListener;

public class ListenerBlockFertilizeEvent implements BukkitAPIListener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnBlockFertilizeEvent(BlockFertilizeEvent event) {
        if (event.getPlayer() == null) return;
        if (XenforoLogin.instance.needCancelled(BukkitLoader.instance.player2info(event.getPlayer()))) {
            event.setCancelled(true);
        }
    }

    @Override
    public void eventClass() {
        BlockFertilizeEvent.class.getName();
    }
}

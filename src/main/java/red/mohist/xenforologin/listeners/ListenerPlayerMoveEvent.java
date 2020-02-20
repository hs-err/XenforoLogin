package red.mohist.xenforologin.listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import red.mohist.xenforologin.Main;
import red.mohist.xenforologin.interfaces.BukkitAPIListener;


public class ListenerPlayerMoveEvent implements BukkitAPIListener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnMove(PlayerMoveEvent event) {
        if (Main.instance.needcancelled(event.getPlayer())) {
            Location location = event.getTo();
            location.setX(Main.instance.default_location.getX());
            location.setZ(Main.instance.default_location.getZ());
            event.setTo(location);
        }
    }

    @Override
    public void eventClass() {
        PlayerMoveEvent.class.getName();
    }
}

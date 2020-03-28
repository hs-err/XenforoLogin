package red.mohist.xenforologin.listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import red.mohist.xenforologin.BukkitLoader;
import red.mohist.xenforologin.XenforoLogin;
import red.mohist.xenforologin.interfaces.BukkitAPIListener;


public class ListenerPlayerMoveEvent implements BukkitAPIListener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnMove(PlayerMoveEvent event) {
        if (XenforoLogin.instance.needCancelled(BukkitLoader.instance.player2info(event.getPlayer()))) {
            Location location = event.getTo();
            location.setX(XenforoLogin.instance.default_location.x);
            location.setZ(XenforoLogin.instance.default_location.z);
            event.setTo(location);
        }
    }

    @Override
    public void eventClass() {
        PlayerMoveEvent.class.getName();
    }
}

package red.mohist.xenforologin.listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import red.mohist.xenforologin.XenforoLogin;

import java.io.IOException;

public class ListenerPlayerQuitEvent implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void OnQuit(PlayerQuitEvent event) throws IOException {
        Location leave_location = event.getPlayer().getLocation();
        if (!XenforoLogin.instance.needCancelled(event.getPlayer())) {
            XenforoLogin.instance.location_data.set(event.getPlayer().getUniqueId().toString() + ".world", leave_location.getWorld().getUID().toString());
            XenforoLogin.instance.location_data.set(event.getPlayer().getUniqueId().toString() + ".x", leave_location.getX());
            XenforoLogin.instance.location_data.set(event.getPlayer().getUniqueId().toString() + ".y", leave_location.getY());
            XenforoLogin.instance.location_data.set(event.getPlayer().getUniqueId().toString() + ".z", leave_location.getZ());
            XenforoLogin.instance.location_data.set(event.getPlayer().getUniqueId().toString() + ".yaw", leave_location.getYaw());
            XenforoLogin.instance.location_data.set(event.getPlayer().getUniqueId().toString() + ".pitch", leave_location.getPitch());
            XenforoLogin.instance.location_data.save(XenforoLogin.instance.location_file);
        }
        XenforoLogin.instance.logged_in.remove(event.getPlayer().getUniqueId());
    }
}

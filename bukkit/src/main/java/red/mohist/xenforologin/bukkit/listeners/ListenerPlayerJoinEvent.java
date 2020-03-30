package red.mohist.xenforologin.bukkit.listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import red.mohist.xenforologin.XenforoLogin;
import red.mohist.xenforologin.bukkit.BukkitLoader;
import red.mohist.xenforologin.core.utils.LoginTicker;

public class ListenerPlayerJoinEvent implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void OnPlayerJoinEvent(PlayerJoinEvent event) {
        BukkitLoader.instance.sendBlankInventoryPacket(event.getPlayer());
        if (!XenforoLogin.instance.logged_in.containsKey(event.getPlayer().getUniqueId())) {
            BukkitLoader.instance.warn("AsyncPlayerPreLoginEvent isn't active. It may cause some security problems.");
            BukkitLoader.instance.warn("It's not a bug. Do NOT report this.");
        }
        if (BukkitLoader.instance.getConfigValue("tp.tp_spawn_before_login", "true") == "true") {
            event.getPlayer().teleport(new Location(
                    getWorld(XenforoLogin.instance.default_location.world),
                    XenforoLogin.instance.default_location.x,
                    XenforoLogin.instance.default_location.y,
                    XenforoLogin.instance.default_location.z,
                    XenforoLogin.instance.default_location.yaw,
                    XenforoLogin.instance.default_location.pitch
            ));
        }
        LoginTicker.add(event.getPlayer());
    }

}

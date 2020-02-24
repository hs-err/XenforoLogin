package red.mohist.xenforologin.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import red.mohist.xenforologin.XenforoLogin;
import red.mohist.xenforologin.utils.LoginTicker;

public class ListenerPlayerJoinEvent implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void OnPlayerJoinEvent(PlayerJoinEvent event) {
        XenforoLogin.instance.sendBlankInventoryPacket(event.getPlayer());
        if (!XenforoLogin.instance.logged_in.containsKey(event.getPlayer().getUniqueId())) {
            XenforoLogin.instance.getLogger().warning("AsyncPlayerPreLoginEvent isn't active. It may cause some security problems.");
            XenforoLogin.instance.getLogger().warning("It's not a bug. Do NOT report this.");
        }
        if (XenforoLogin.instance.config.getBoolean("tp.tp_spawn_before_login", true)) {
            try {
                event.getPlayer().teleportAsync(XenforoLogin.instance.default_location);
            } catch (NoSuchMethodError e) {
                XenforoLogin.instance.getLogger().warning("Cannot find method " + e.getMessage());
                XenforoLogin.instance.getLogger().warning("Using synchronized teleport");
                XenforoLogin.instance.getLogger().warning("It's not a bug. Do NOT report this.");
                Bukkit.getScheduler().runTask(XenforoLogin.instance, () ->
                        event.getPlayer().teleport(XenforoLogin.instance.default_location));
            }
        }
        LoginTicker.add(event.getPlayer());
    }

}

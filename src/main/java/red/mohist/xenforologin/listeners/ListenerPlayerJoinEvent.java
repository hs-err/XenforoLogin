package red.mohist.xenforologin.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import red.mohist.xenforologin.XenforoLogin;
import red.mohist.xenforologin.enums.StatusType;
import red.mohist.xenforologin.forums.ForumSystems;
import red.mohist.xenforologin.utils.ResultTypeUtils;

public class ListenerPlayerJoinEvent implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void OnJoin(PlayerJoinEvent event) {
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
        new Thread(() -> {
            if (!XenforoLogin.instance.logged_in.containsKey(event.getPlayer().getUniqueId())) {
                boolean result = ResultTypeUtils.handle(event.getPlayer(),
                        ForumSystems.getCurrentSystem()
                                .join(event.getPlayer())
                                .shouldLogin(false));
                if (!result) {
                    XenforoLogin.instance.getLogger().warning(
                            event.getPlayer().getName() + " didn't pass AccountExists test");
                    return;
                }
                XenforoLogin.instance.message(event.getPlayer());
            }
            XenforoLogin.instance.sendBlankInventoryPacket(event.getPlayer());
            int f = 0;
            int s = XenforoLogin.instance.config.getInt("secure.show_tips_time", 5);
            int t = XenforoLogin.instance.config.getInt("secure.max_login_time", 30);
            while (true) {
                XenforoLogin.instance.sendBlankInventoryPacket(event.getPlayer());
                switch (XenforoLogin.instance.logged_in.get(event.getPlayer().getUniqueId())) {
                    case NEED_LOGIN:
                        event.getPlayer().sendMessage(XenforoLogin.instance.langFile("need_login"));
                        break;
                    case NEED_REGISTER_EMAIL:
                        event.getPlayer().sendMessage(XenforoLogin.instance.langFile("register_email"));
                        break;
                    case NEED_REGISTER_PASSWORD:
                        event.getPlayer().sendMessage(XenforoLogin.instance.langFile("register_password"));
                        break;
                    case NEED_REGISTER_CONFIRM:
                        event.getPlayer().sendMessage(XenforoLogin.instance.langFile("register_password_confirm"));
                        break;
                }
                try {
                    Thread.sleep(s * 1000);
                    f += s;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (f > t && XenforoLogin.instance.logged_in.get(event.getPlayer().getUniqueId()) == StatusType.NEED_LOGIN) {
                    break;
                }
                if (!event.getPlayer().isOnline() || !XenforoLogin.instance.needCancelled(event.getPlayer())) {
                    return;
                }
            }
            Bukkit.getScheduler().runTask(XenforoLogin.instance, () -> event.getPlayer()
                    .kickPlayer(XenforoLogin.instance.langFile("errors.time_out")));

        }).start();
    }

}

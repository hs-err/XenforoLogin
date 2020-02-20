package red.mohist.xenforologin.listeners;

import com.google.common.collect.ImmutableMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import red.mohist.xenforologin.XenforoLogin;
import red.mohist.xenforologin.enums.ResultType;
import red.mohist.xenforologin.forums.ForumSystems;
import red.mohist.xenforologin.interfaces.BukkitAPIListener;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.bukkit.Bukkit.getWorld;

public class ListenerChatEvent implements BukkitAPIListener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) throws IOException, InvalidConfigurationException {
        if (!XenforoLogin.instance.needCancelled(event.getPlayer())) {
            if (XenforoLogin.instance.config.getBoolean("secure.cancel_chat_after_login", false)) {
                event.getPlayer().sendMessage(XenforoLogin.instance.langFile("logged_in"));
                event.setCancelled(true);
            }
            return;
        }
        event.setCancelled(true);
        ForumSystems
                .getCurrentSystem()
                .login(event.getPlayer(), event.getMessage())
                .setLogin(true)
                .handle(event.getPlayer());
    }

    @Override
    public void eventClass() {

    }
}

package red.mohist.xenforologin.listeners;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import red.mohist.xenforologin.XenforoLogin;
import red.mohist.xenforologin.interfaces.BukkitAPIListener;

import java.io.IOException;

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


    }

    @Override
    public void eventClass() {

    }
}

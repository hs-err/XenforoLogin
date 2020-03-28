package red.mohist.xenforologin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import red.mohist.xenforologin.BukkitLoader;
import red.mohist.xenforologin.XenforoLogin;
import red.mohist.xenforologin.enums.StatusType;
import red.mohist.xenforologin.forums.ForumSystems;
import red.mohist.xenforologin.interfaces.BukkitAPIListener;
import red.mohist.xenforologin.utils.ResultTypeUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListenerChatEvent implements BukkitAPIListener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        if (!XenforoLogin.instance.needCancelled(BukkitLoader.instance.player2info(event.getPlayer()))) {
            if (BukkitLoader.instance.getConfigValue("secure.cancel_chat_after_login", "false").equals("true")) {
                event.getPlayer().sendMessage(XenforoLogin.instance.langFile("logged_in"));
                event.setCancelled(true);
            }
            return;
        }
        event.setCancelled(true);
        XenforoLogin.instance.onChat(BukkitLoader.instance.player2info(event.getPlayer()),event.getMessage());
    }

    @Override
    public void eventClass() {
        AsyncPlayerChatEvent.class.getName();
    }
}

package red.mohist.xenforologin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import red.mohist.xenforologin.BukkitLoader;
import red.mohist.xenforologin.XenforoLogin;
import red.mohist.xenforologin.enums.ResultType;
import red.mohist.xenforologin.enums.StatusType;
import red.mohist.xenforologin.forums.ForumSystems;
import red.mohist.xenforologin.modules.PlayerInfo;

public class ListenerAsyncPlayerPreLoginEvent implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {
        String canjoin=XenforoLogin.instance.canJoin(new PlayerInfo(event.getName(),event.getUniqueId(),event.getAddress().getHostName()));
        if(canjoin!=null){
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,canjoin);
        }
    }
}

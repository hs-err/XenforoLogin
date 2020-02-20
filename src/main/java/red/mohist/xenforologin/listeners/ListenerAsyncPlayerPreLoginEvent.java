package red.mohist.xenforologin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import red.mohist.xenforologin.XenforoLogin;
import red.mohist.xenforologin.enums.ResultType;
import red.mohist.xenforologin.forums.ForumSystems;
import red.mohist.xenforologin.interfaces.BukkitAPIListener;

public class ListenerAsyncPlayerPreLoginEvent implements BukkitAPIListener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        ResultType resultType = ForumSystems.getCurrentSystem()
                .join(event.getName())
                .shouldLogin(false);
        if (resultType == ResultType.OK)
            event.allow();
        else if (resultType == ResultType.ERROR_NAME)
            event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                    XenforoLogin.instance.langFile("errors.name_incorrect", resultType.getInheritedObject()));
        else if (resultType == ResultType.NO_USER)
            event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST,
                    XenforoLogin.instance.langFile("errors.no_user"));
        else if (resultType == ResultType.SERVER_ERROR)
            event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_FULL,
                    XenforoLogin.instance.langFile("errors.server"));
    }

    @Override
    public void eventClass() {
        AsyncPlayerPreLoginEvent.class.getName();
    }
}

package red.mohist.xenforologin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import red.mohist.xenforologin.XenforoLogin;
import red.mohist.xenforologin.enums.ResultType;
import red.mohist.xenforologin.enums.StatusType;
import red.mohist.xenforologin.forums.ForumSystems;

public class ListenerAsyncPlayerPreLoginEvent implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {
        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, XenforoLogin.instance.langFile("errors.server"));
        if (XenforoLogin.instance.logged_in.containsKey(event.getUniqueId())) {
            return;
        }
        ResultType resultType = ForumSystems.getCurrentSystem()
                .join(event.getName())
                .shouldLogin(false);
        switch (resultType) {
            case OK:
                XenforoLogin.instance.logged_in.put(event.getUniqueId(), StatusType.NEED_LOGIN);
                event.allow();
                break;
            case ERROR_NAME:
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                        XenforoLogin.instance.langFile("errors.name_incorrect",
                                resultType.getInheritedObject()));
                break;
            case NO_USER:
                if (XenforoLogin.instance.config.getBoolean("api.register", false)) {
                    event.allow();
                    XenforoLogin.instance.logged_in.put(event.getUniqueId(), StatusType.NEED_REGISTER_EMAIL);
                } else {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                            XenforoLogin.instance.langFile("errors.no_user"));
                }
                break;
            case UNKNOWN:
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                        XenforoLogin.instance.langFile("errors.unknown", resultType.getInheritedObject()));
                break;
            case SERVER_ERROR:
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                        XenforoLogin.instance.langFile("errors.server"));
                break;
        }
    }
}

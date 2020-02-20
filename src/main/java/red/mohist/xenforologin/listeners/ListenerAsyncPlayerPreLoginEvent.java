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
        XenforoLogin.instance.asyncPlayerPreLoginActive();
        ResultType resultType = ForumSystems.getCurrentSystem()
                .join(event.getName())
                .shouldLogin(false);
        if (resultType == ResultType.OK) {
            XenforoLogin.instance.getLogger().info(
                    event.getAddress() + " " +
                            event.getName() + " passed AccountExists test");
            event.allow();
        } else if (resultType == ResultType.ERROR_NAME) {
            XenforoLogin.instance.getLogger().warning(
                    event.getAddress() + " " +
                            resultType.getInheritedObject() + " tried to use " +
                            event.getName() + " to join the server.");
            event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                    XenforoLogin.instance.langFile("errors.name_incorrect", resultType.getInheritedObject()));
        } else if (resultType == ResultType.NO_USER) {
            XenforoLogin.instance.getLogger().warning(
                    event.getAddress() + " " +
                            event.getName() + " is not registered at the forum.");
            event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST,
                    XenforoLogin.instance.langFile("errors.no_user"));
        } else if (resultType == ResultType.SERVER_ERROR) {
            XenforoLogin.instance.getLogger().warning(
                    event.getAddress() + " " +
                            event.getName() + " tried to join but an internal error occurred.");
            event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_FULL,
                    XenforoLogin.instance.langFile("errors.server"));
        }
    }

    @Override
    public void eventClass() {
        AsyncPlayerPreLoginEvent.class.getName();
    }
}

package red.mohist.xenforologin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
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
        if (!XenforoLogin.instance.needCancelled(event.getPlayer())) {
            if (XenforoLogin.instance.config.getBoolean("secure.cancel_chat_after_login", false)) {
                event.getPlayer().sendMessage(XenforoLogin.instance.langFile("logged_in"));
                event.setCancelled(true);
            }
            return;
        }
        event.setCancelled(true);
        StatusType status = XenforoLogin.instance.logged_in.get(event.getPlayer().getUniqueId());
        switch (status) {
            case NEED_CHECK:
                event.getPlayer().sendMessage(XenforoLogin.instance.langFile("need_check"));
                break;
            case NEED_LOGIN:
                ResultTypeUtils.handle(event.getPlayer(),
                        ForumSystems.getCurrentSystem()
                                .login(event.getPlayer(), event.getMessage())
                                .shouldLogin(true));
                break;
            case NEED_REGISTER_EMAIL:
                if (isEmail(event.getMessage())) {
                    XenforoLogin.instance.logged_in.put(event.getPlayer().getUniqueId(), StatusType.NEED_REGISTER_PASSWORD.setEmail(event.getMessage()));
                    XenforoLogin.instance.message(event.getPlayer());
                } else {
                    event.getPlayer().sendMessage(XenforoLogin.instance.langFile("errors.email"));
                }
                break;
            case NEED_REGISTER_PASSWORD:
                XenforoLogin.instance.logged_in.put(
                        event.getPlayer().getUniqueId(),
                        StatusType.NEED_REGISTER_CONFIRM.setEmail(status.email).setPassword(event.getMessage()));
                XenforoLogin.instance.message(event.getPlayer());
                break;
            case NEED_REGISTER_CONFIRM:
                if (event.getMessage().equals(status.password)) {
                    boolean result = ResultTypeUtils.handle(event.getPlayer(),
                            ForumSystems.getCurrentSystem()
                                    .register(event.getPlayer(), status.password, status.email)
                                    .shouldLogin(true));
                    if (result) {
                        XenforoLogin.instance.logged_in.put(
                                event.getPlayer().getUniqueId(), StatusType.LOGGED_IN);
                    } else {
                        XenforoLogin.instance.logged_in.put(
                                event.getPlayer().getUniqueId(), StatusType.NEED_REGISTER_EMAIL);
                        XenforoLogin.instance.message(event.getPlayer());
                    }
                } else {
                    event.getPlayer().sendMessage(XenforoLogin.instance.langFile("errors.confirm"));
                    XenforoLogin.instance.logged_in.put(
                            event.getPlayer().getUniqueId(), StatusType.NEED_REGISTER_PASSWORD);
                    XenforoLogin.instance.message(event.getPlayer());
                }
                break;
        }
    }

    @Override
    public void eventClass() {
        AsyncPlayerChatEvent.class.getName();
    }

    public boolean isEmail(String email) {
        if (null == email || "".equals(email)) {
            return false;
        }
        Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,10}");
        Matcher m = p.matcher(email);
        return m.matches();
    }
}

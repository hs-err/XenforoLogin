package red.mohist.xenforologin.core.utils;

import red.mohist.xenforologin.core.XenforoLogin;
import red.mohist.xenforologin.core.enums.StatusType;
import red.mohist.xenforologin.core.forums.ForumSystems;

import javax.annotation.Nonnull;

public class LoginTickPlayer {

    static final int showTipTime = XenforoLogin.instance.api.getInt("secure.show_tips_time", 5);
    long startTime = System.currentTimeMillis();
    int loginTimeout = XenforoLogin.instance.api.getInt("secure.max_login_time", 30);
    @Nonnull
    Player player;

    public LoginTickPlayer(@Nonnull Player player) {
        this.player = player;
    }


    public TickResult tick() {
        if (!XenforoLogin.instance.logged_in.containsKey(player.getUniqueId())) {
            boolean result = ResultTypeUtils.handle(player,
                    ForumSystems.getCurrentSystem()
                            .join(player)
                            .shouldLogin(false));
            if (!result) {
                XenforoLogin.instance.api.getLogger().warning(
                        player.getName() + " didn't pass AccountExists test");
                return TickResult.DONE;
            }
            XenforoLogin.instance.message(player);
        }
        XenforoLogin.instance.sendBlankInventoryPacket(player);
        XenforoLogin.instance.message(player);
        if ((System.currentTimeMillis() - startTime) / 1000 > loginTimeout
                && XenforoLogin.instance.logged_in.get(player.getUniqueId()) == StatusType.NEED_LOGIN) {
            return TickResult.CONTINUE;
        }
        if (!player.isOnline() || !XenforoLogin.instance.needCancelled(player)) {
            return TickResult.DONE;
        }
        return TickResult.CONTINUE;
    }

    public enum TickResult {
        DONE, CONTINUE
    }


}

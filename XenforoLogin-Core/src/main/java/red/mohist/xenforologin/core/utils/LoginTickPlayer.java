package red.mohist.xenforologin.core.utils;

import red.mohist.xenforologin.core.XenforoLoginCore;
import red.mohist.xenforologin.core.enums.StatusType;
import red.mohist.xenforologin.core.forums.ForumSystems;
import red.mohist.xenforologin.core.modules.AbstractPlayer;

import javax.annotation.Nonnull;

public class LoginTickPlayer {

    static final int showTipTime = XenforoLoginCore.instance.api.getConfigValueInt("secure.show_tips_time", 5);
    long startTime = System.currentTimeMillis();
    int loginTimeout = XenforoLoginCore.instance.api.getConfigValueInt("secure.max_login_time", 30);
    @Nonnull
    AbstractPlayer player;

    public LoginTickPlayer(@Nonnull AbstractPlayer player) {
        this.player = player;
    }


    public TickResult tick() {
        if (!XenforoLoginCore.instance.logged_in.containsKey(player.getUniqueId())) {
            boolean result = ResultTypeUtils.handle(player,
                    ForumSystems.getCurrentSystem()
                            .join(player)
                            .shouldLogin(false));
            if (!result) {
                XenforoLoginCore.instance.api.getLogger().warning(
                        player.getName() + " didn't pass AccountExists test");
                return TickResult.DONE;
            }
            XenforoLoginCore.instance.message(player);
        }
        XenforoLoginCore.instance.api.sendBlankInventoryPacket(player);
        XenforoLoginCore.instance.message(player);
        if ((System.currentTimeMillis() - startTime) / 1000 > loginTimeout
                && XenforoLoginCore.instance.logged_in.get(player.getUniqueId()) == StatusType.NEED_LOGIN) {
            return TickResult.CONTINUE;
        }
        if (!player.isOnline() || !XenforoLoginCore.instance.needCancelled(player)) {
            return TickResult.DONE;
        }
        return TickResult.CONTINUE;
    }

    public enum TickResult {
        DONE, CONTINUE
    }


}

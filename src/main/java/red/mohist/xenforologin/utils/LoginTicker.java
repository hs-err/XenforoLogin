package red.mohist.xenforologin.utils;

import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import red.mohist.xenforologin.XenforoLogin;

import java.util.Set;

public class LoginTicker implements Runnable {

    private static Set<LoginTickPlayer> tickers = Sets.newConcurrentHashSet();

    public static void register() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(
                XenforoLogin.instance, new LoginTicker(), 0, LoginTickPlayer.showTipTime);
    }

    public static void add(Player player) {
        tickers.add(new LoginTickPlayer(player));
    }

    @Override
    public void run() {
        tickers.removeIf(current -> current.tick() == LoginTickPlayer.TickResult.DONE);
    }
}

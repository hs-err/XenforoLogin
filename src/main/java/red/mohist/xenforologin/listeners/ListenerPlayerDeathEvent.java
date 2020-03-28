package red.mohist.xenforologin.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import red.mohist.xenforologin.BukkitLoader;
import red.mohist.xenforologin.XenforoLogin;
import red.mohist.xenforologin.interfaces.BukkitAPIListener;

public class ListenerPlayerDeathEvent implements BukkitAPIListener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnPlayerDeathEvent(PlayerDeathEvent event) {
        if (XenforoLogin.instance.needCancelled(BukkitLoader.instance.player2info((Player) event.getEntity()))) {
            event.setCancelled(true);
        }
    }

    @Override
    public void eventClass() {
        PlayerDeathEvent.class.getName();
    }
}

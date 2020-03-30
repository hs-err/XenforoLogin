package red.mohist.xenforologin.bukkit.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerVelocityEvent;
import red.mohist.xenforologin.XenforoLogin;
import red.mohist.xenforologin.bukkit.BukkitLoader;
import red.mohist.xenforologin.core.interfaces.BukkitAPIListener;

public class ListenerPlayerVelocityEvent implements BukkitAPIListener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnPlayerVelocityEvent(PlayerVelocityEvent event) {
        if (XenforoLogin.instance.needCancelled(BukkitLoader.instance.player2info(event.getPlayer()))) {
            event.setCancelled(true);
        }
    }

    @Override
    public void eventClass() {
        PlayerVelocityEvent.class.getName();
    }
}

package red.mohist.xenforologin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import red.mohist.xenforologin.Main;
import red.mohist.xenforologin.interfaces.BukkitAPIListener;

public class ListenerPlayerToggleSneakEvent implements BukkitAPIListener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnPlayerToggleSneakEvent(PlayerToggleSneakEvent event) {
        if (Main.instance.needCancelled(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @Override
    public void eventClass() {
        PlayerToggleSneakEvent.class.getName();
    }
}

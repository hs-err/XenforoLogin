package red.mohist.xenforologin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.raid.RaidTriggerEvent;
import red.mohist.xenforologin.BukkitLoader;
import red.mohist.xenforologin.XenforoLogin;
import red.mohist.xenforologin.interfaces.BukkitAPIListener;

public class ListenerRaidTriggerEvent implements BukkitAPIListener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnRaidTriggerEvent(RaidTriggerEvent event) {
        if (XenforoLogin.instance.needCancelled(BukkitLoader.instance.player2info(event.getPlayer()))) {
            event.setCancelled(true);
        }
    }

    @Override
    public void eventClass() {
        RaidTriggerEvent.class.getName();
    }
}

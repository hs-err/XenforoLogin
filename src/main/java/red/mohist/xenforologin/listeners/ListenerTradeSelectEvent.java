package red.mohist.xenforologin.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.TradeSelectEvent;
import red.mohist.xenforologin.Main;
import red.mohist.xenforologin.interfaces.BukkitAPIListener;

public class ListenerTradeSelectEvent implements BukkitAPIListener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnTradeSelectEvent(TradeSelectEvent event) {
        if (Main.instance.needcancelled((Player) event.getWhoClicked())) {
            event.setCancelled(true);
        }
    }

    @Override
    public void eventClass() {
        TradeSelectEvent.class.getName();
    }
}

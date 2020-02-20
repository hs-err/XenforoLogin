package red.mohist.xenforologin.listeners;

import com.google.common.collect.ImmutableMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import red.mohist.xenforologin.XenforoLogin;
import red.mohist.xenforologin.enums.ResultType;
import red.mohist.xenforologin.forums.ForumSystems;
import red.mohist.xenforologin.interfaces.BukkitAPIListener;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import static org.bukkit.Bukkit.getWorld;

public class ListenerChatEvent implements BukkitAPIListener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) throws IOException, InvalidConfigurationException {
        if (!XenforoLogin.instance.needCancelled(event.getPlayer())) {
            if (XenforoLogin.instance.config.getBoolean("secure.cancel_chat_after_login", false)) {
                event.getPlayer().sendMessage(XenforoLogin.instance.langFile("logged_in"));
                event.setCancelled(true);
            }
            return;
        }
        event.setCancelled(true);
        ResultType resultType = ForumSystems.getCurrentSystem().login(event.getPlayer(), event.getMessage());
        switch (resultType) {
            case OK:
                handleOK(event);
                break;
            case PASSWORD_INCORRECT:
                Bukkit.getScheduler().runTask(XenforoLogin.instance, () -> event.getPlayer()
                        .kickPlayer(XenforoLogin.instance.langFile("errors.name_incorrect", ImmutableMap.of(
                                "message", "Username incorrect.",
                                "correct", String.valueOf(resultType.getInheritedObject())
                        ))));
                break;
            case SERVER_ERROR:
                event.getPlayer().sendMessage(String.valueOf(resultType.getInheritedObject()));
                break;
        }

    }

    private void handleOK(AsyncPlayerChatEvent event) throws IOException, InvalidConfigurationException {
        if (XenforoLogin.instance.config.getBoolean("event.tp_back_after_login", true)) {
            XenforoLogin.instance.location_data.load(XenforoLogin.instance.location_file);
            Location spawn_location = Objects.requireNonNull(getWorld("world")).getSpawnLocation();
            Location leave_location = new Location(
                    getWorld(UUID.fromString(Objects.requireNonNull(XenforoLogin.instance.location_data.getString(
                            event.getPlayer().getUniqueId().toString() + ".world",
                            spawn_location.getWorld().getUID().toString())))),
                    XenforoLogin.instance.location_data.getDouble(
                            event.getPlayer().getUniqueId().toString() + ".x", spawn_location.getX()),
                    XenforoLogin.instance.location_data.getDouble(
                            event.getPlayer().getUniqueId().toString() + ".y", spawn_location.getY()),
                    XenforoLogin.instance.location_data.getDouble(
                            event.getPlayer().getUniqueId().toString() + ".z", spawn_location.getZ())
            );
            event.getPlayer().teleportAsync(leave_location);
        }
        XenforoLogin.instance.logged_in.put(event.getPlayer().hashCode(), true);
        event.getPlayer().updateInventory();
        XenforoLogin.instance.getLogger().info("Logging in " + event.getPlayer().getUniqueId());
        event.getPlayer().sendMessage(XenforoLogin.instance.langFile("success"));
    }

    @Override
    public void eventClass() {

    }
}

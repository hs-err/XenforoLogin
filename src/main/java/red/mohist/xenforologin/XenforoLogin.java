package red.mohist.xenforologin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import red.mohist.xenforologin.enums.StatusType;
import red.mohist.xenforologin.forums.ForumSystems;
import red.mohist.xenforologin.interfaces.BukkitAPIListener;
import red.mohist.xenforologin.listeners.protocollib.ListenerProtocolEvent;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.bukkit.Bukkit.getPluginManager;
import static org.bukkit.Bukkit.getWorld;

public final class XenforoLogin extends JavaPlugin implements Listener {

    public static XenforoLogin instance;
    public ConcurrentMap<UUID, StatusType> logged_in;
    public FileConfiguration config;
    public FileConfiguration location_data;
    public File location_file;
    public Location default_location;
    private ListenerProtocolEvent listenerProtocolEvent;

    @Override
    public void onEnable() {
        getLogger().info("Hello, XenforoLogin!");
        instance = this;
        logged_in = new ConcurrentHashMap<>();
        saveDefaultConfig();
        loadConfig();

        ForumSystems.reloadConfig();

        hookProtocolLib();

        registerListeners();
    }

    private void registerListeners() {
        {
            int unavailableCount = 0;
            Set<Class<? extends BukkitAPIListener>> classes = new Reflections("red.mohist.xenforologin.listeners")
                    .getSubTypesOf(BukkitAPIListener.class);
            for (Class<? extends BukkitAPIListener> clazz : classes) {
                BukkitAPIListener listener;
                try {
                    listener = clazz.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    getLogger().warning(clazz.getName() + " is not available.");
                    unavailableCount++;
                    continue;
                }
                if (!listener.isAvailable()) {
                    getLogger().warning(clazz.getName() + " is not available.");
                    unavailableCount++;
                    continue;
                }
                Bukkit.getPluginManager().registerEvents(listener, this);
            }
            if (unavailableCount > 0) {
                getLogger().warning("Warning: Some features in this plugin is not available on this version of bukkit");
                getLogger().warning("If your encountered errors, do NOT report to XenforoLogin.");
                getLogger().warning("Error count: " + unavailableCount);
            }
        }
        getPluginManager().registerEvents(this, this);
    }

    private void hookProtocolLib() {
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null && config.getBoolean("secure.hide_inventory", true)) {
            listenerProtocolEvent = new ListenerProtocolEvent();
            getLogger().info("Found ProtocolLib, hooked into ProtocolLib to use \"hide_inventory\"");
        }
    }

    private void loadConfig() {
        config = getConfig();
        location_file = new File(getDataFolder(), "player_location.yml");
        if (!location_file.exists()) {
            try {
                if (!location_file.createNewFile()) {
                    throw new IOException("File can't be created.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        location_data = YamlConfiguration.loadConfiguration(location_file);
        Location spawn_location = Objects.requireNonNull(getWorld("world")).getSpawnLocation();
        default_location = new Location(
                getWorld(Objects.requireNonNull(config.getString("spawn.world", "world"))),
                config.getDouble("spawn.x", spawn_location.getX()),
                config.getDouble("spawn.y", spawn_location.getY()),
                config.getDouble("spawn.z", spawn_location.getZ())
        );
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public boolean needCancelled(Player player) {
        return !logged_in.getOrDefault(player.getUniqueId(), StatusType.NEED_LOGIN).equals(StatusType.LOGINED);
    }

    public String langFile(String key) {
        String result = config.getString("lang." + key);
        if (result == null) {
            return key;
        }
        return result;
    }

    public String langFile(String key, Map<String, String> data) {
        String result = config.getString("lang." + key);
        if (result == null) {
            StringBuilder resultBuilder = new StringBuilder(key);
            resultBuilder.append("\n");
            for (Map.Entry<String, String> entry : data.entrySet()) {
                resultBuilder.append(entry.getKey()).append(":").append(entry.getValue());
            }
            result = resultBuilder.toString();
            return result;
        }
        for (Map.Entry<String, String> entry : data.entrySet()) {
            result = result.replace("[" + entry.getKey() + "]", entry.getValue());
        }
        return result;
    }

    public void sendBlankInventoryPacket(Player player) {
        if (listenerProtocolEvent != null)
            listenerProtocolEvent.sendBlankInventoryPacket(player);
    }

    public void login(Player player) {
        try {
            if (config.getBoolean("event.tp_back_after_login", true)) {
                location_data.load(location_file);
                Location spawn_location = Objects.requireNonNull(getWorld("world")).getSpawnLocation();
                Location leave_location = new Location(
                        getWorld(UUID.fromString(Objects.requireNonNull(location_data.getString(
                                player.getUniqueId().toString() + ".world",
                                spawn_location.getWorld().getUID().toString())))),
                        XenforoLogin.instance.location_data.getDouble(
                                player.getUniqueId().toString() + ".x", spawn_location.getX()),
                        XenforoLogin.instance.location_data.getDouble(
                                player.getUniqueId().toString() + ".y", spawn_location.getY()),
                        XenforoLogin.instance.location_data.getDouble(
                                player.getUniqueId().toString() + ".z", spawn_location.getZ()),
                        (float) XenforoLogin.instance.location_data.getDouble(
                                player.getUniqueId().toString() + ".yaw", spawn_location.getYaw()),
                        (float) XenforoLogin.instance.location_data.getDouble(
                                player.getUniqueId().toString() + ".pitch", spawn_location.getPitch())
                );
                try {
                    player.teleportAsync(leave_location);
                } catch (NoSuchMethodError e) {
                    XenforoLogin.instance.getLogger().warning("Cannot find method " + e.getMessage());
                    XenforoLogin.instance.getLogger().warning("Using synchronized teleport");
                    Bukkit.getScheduler().runTask(XenforoLogin.instance, () -> player.teleport(leave_location));
                }
            }
            logged_in.put(player.getUniqueId(), StatusType.LOGINED);
            player.updateInventory();
            XenforoLogin.instance.getLogger().info("Logging in " + player.getName());
            player.sendMessage(XenforoLogin.instance.langFile("success"));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void message(Player player) {
        switch (logged_in.get(player.getUniqueId())) {
            case NEED_LOGIN:
                player.sendMessage(langFile("need_login"));
                break;
            case NEED_REGISTER_EMAIL:
                player.sendMessage(langFile("register_email"));
                break;
            case NEED_REGISTER_PASSWORD:
                player.sendMessage(langFile("register_password"));
                break;
            case NEED_REGISTER_CONFIRM:
                player.sendMessage(langFile("register_password_confirm"));
                break;
        }
    }
}


package red.mohist.xenforologin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import red.mohist.xenforologin.forums.ForumSystems;
import red.mohist.xenforologin.interfaces.BukkitAPIListener;
import red.mohist.xenforologin.listeners.protocollib.ListenerProtocolEvent;
import red.mohist.xenforologin.utils.ResultTypeUtils;

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
    public ConcurrentMap<Integer, Boolean> logged_in;
    public FileConfiguration config;
    public FileConfiguration location_data;
    public File location_file;
    public Location default_location;
    private ListenerProtocolEvent listenerProtocolEvent;

    private boolean isAsyncPlayerPreLoginEnabled = false;
    private boolean isAsyncPlayerPreLoginActive = false;

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
            isAsyncPlayerPreLoginEnabled = false;
            isAsyncPlayerPreLoginActive = false;
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
                if (listener.getClass().getName().equals("red.mohist.xenforologin.listeners.ListenerAsyncPlayerPreLoginEvent"))
                    isAsyncPlayerPreLoginEnabled = true;
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void OnJoin(PlayerJoinEvent event) {
        if (isAsyncPlayerPreLoginEnabled ^ isAsyncPlayerPreLoginActive)
            XenforoLogin.instance.getLogger().warning(
                    "AsyncPlayerPreLogin listener is register but not active. This may cause issues. ");
        new Thread(() -> {
            if (!(isAsyncPlayerPreLoginEnabled && isAsyncPlayerPreLoginActive)) {
                boolean result = ResultTypeUtils.handle(event.getPlayer(),
                        ForumSystems.getCurrentSystem()
                                .join(event.getPlayer())
                                .shouldLogin(false));
                if (!result) {
                    XenforoLogin.instance.getLogger().warning(
                            event.getPlayer().getName() + " didn't pass AccountExists test");
                    return;
                }
            }
            logged_in.put(event.getPlayer().hashCode(), false);
            if (config.getBoolean("tp.tp_spawn_before_login", true)) {
                try {
                    event.getPlayer().teleportAsync(default_location);
                } catch (NoSuchMethodError e) {
                    XenforoLogin.instance.getLogger().warning("Cannot find method " + e.getMessage());
                    XenforoLogin.instance.getLogger().warning("Using synchronized teleport");
                    Bukkit.getScheduler().runTask(XenforoLogin.instance, () ->
                            event.getPlayer().teleport(default_location));
                }
            }
            sendBlankInventoryPacket(event.getPlayer());
            int f = 0;
            int s = config.getInt("secure.show_tips_time", 5);
            int t = config.getInt("secure.max_login_time", 30);
            while (true) {
                sendBlankInventoryPacket(event.getPlayer());
                event.getPlayer().sendMessage(langFile("need_login"));
                try {
                    Thread.sleep(s * 1000);
                    f += s;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (f > t) {
                    break;
                }
                if (!event.getPlayer().isOnline() || !needCancelled(event.getPlayer())) {
                    return;
                }
            }
            Bukkit.getScheduler().runTask(XenforoLogin.instance, () -> event.getPlayer()
                    .kickPlayer(langFile("errors.time_out")));

        }).start();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void OnQuit(PlayerQuitEvent event) throws IOException {
        Location leave_location = event.getPlayer().getLocation();
        if (!needCancelled(event.getPlayer())) {
            location_data.set(event.getPlayer().getUniqueId().toString() + ".world", leave_location.getWorld().getUID().toString());
            location_data.set(event.getPlayer().getUniqueId().toString() + ".x", leave_location.getX());
            location_data.set(event.getPlayer().getUniqueId().toString() + ".y", leave_location.getY());
            location_data.set(event.getPlayer().getUniqueId().toString() + ".z", leave_location.getZ());
            location_data.set(event.getPlayer().getUniqueId().toString() + ".yaw", leave_location.getYaw());
            location_data.set(event.getPlayer().getUniqueId().toString() + ".pitch", leave_location.getPitch());
            location_data.save(location_file);
        }
        logged_in.remove(event.getPlayer().hashCode());
    }

    public boolean needCancelled(Player player) {
        return !logged_in.getOrDefault(player.hashCode(), false);
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

    private void sendBlankInventoryPacket(Player player) {
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
            logged_in.put(player.hashCode(), true);
            player.updateInventory();
            XenforoLogin.instance.getLogger().info("Logging in " + player.getUniqueId());
            player.sendMessage(XenforoLogin.instance.langFile("success"));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void asyncPlayerPreLoginActive() {
        isAsyncPlayerPreLoginActive = true;
    }
}


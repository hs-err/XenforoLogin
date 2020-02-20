package red.mohist.xenforologin;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.reflections.Reflections;
import red.mohist.xenforologin.interfaces.BukkitAPIListener;
import red.mohist.xenforologin.listeners.protocollib.ListenerProtocolEvent;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.bukkit.Bukkit.getPluginManager;
import static org.bukkit.Bukkit.getWorld;

public final class Main extends JavaPlugin implements Listener {

    public String api_url;
    public String api_key;
    public ConcurrentMap<Integer, Boolean> logged_in;
    public FileConfiguration config;
    public FileConfiguration location_data;
    public File location_file;
    public Location default_location;
    public static Main instance;
    private ListenerProtocolEvent listenerProtocolEvent;

    @SuppressWarnings({"ConstantConditions"})
    @Override
    public void onEnable() {
        getLogger().info("Hello, XenforoLogin!");
        instance = this;
        logged_in = new ConcurrentHashMap<>();
        saveDefaultConfig();
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
        api_url = config.getString("api.url");
        api_key = config.getString("api.key");
        Location spawn_location = getWorld("world").getSpawnLocation();
        default_location = new Location(
                getWorld(config.getString("spawn.world", "world")),
                config.getDouble("spawn.x", spawn_location.getX()),
                config.getDouble("spawn.y", spawn_location.getY()),
                config.getDouble("spawn.z", spawn_location.getZ())
        );

        if (ListenerProtocolEvent.isAvailable() && config.getBoolean("secure.hide_inventory", true)) {
            listenerProtocolEvent = new ListenerProtocolEvent();
            getLogger().info("Found ProtocolLib, hooked into ProtocolLib to use \"hide_inventory\"");
        }

        {
            int unavailableCount = 0;
            //noinspection SpellCheckingInspection
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

        // getLogger().info("API URL: " + api_url);
        // getLogger().info("API KEY: " + api_key);
        getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void OnJoin(PlayerJoinEvent event) {
        logged_in.put(event.getPlayer().hashCode(), false);
        if (config.getBoolean("tp.tp_spawn_before_login", true)) {
            event.getPlayer().teleport(default_location);
        }
        sendBlankInventoryPacket(event.getPlayer());
        new Thread(() -> {
            ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else if (status == 401) {
                    getLogger().warning(langFile("errors.key", ImmutableMap.of(
                            "key", api_key)));
                    throw new ClientProtocolException("Unexpected response status: " + status);
                } else if (status == 404) {
                    getLogger().warning(langFile("errors.url", ImmutableMap.of(
                            "url", api_url)));
                    throw new ClientProtocolException("Unexpected response status: " + status);
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);

                }
            };
            String result;
            try {
                result = Request.Get(api_url + "/users/find-name?username=" +
                        URLEncoder.encode(event.getPlayer().getName(), "UTF-8"))
                        .addHeader("XF-Api-Key", api_key)
                        .execute().handleResponse(responseHandler);
            } catch (IOException e) {
                kick(event.getPlayer(), langFile("errors.server"));
                e.printStackTrace();
                return;
            }
            if (result == null) {
                kick(event.getPlayer(), langFile("errors.server"));
                new ClientProtocolException("Unexpected response: null").printStackTrace();
                return;
            }
            JsonParser parse = new JsonParser();
            JsonObject json = parse.parse(result).getAsJsonObject();
            if (json == null) {
                kick(event.getPlayer(), langFile("errors.server"));
                new ClientProtocolException("Unexpected json: null").printStackTrace();
                return;
            }
            if (json.get("exact").isJsonNull()) {
                kick(event.getPlayer(), langFile("errors.no_user"));
                return;
            }
            if (!json.getAsJsonObject("exact").get("username").getAsString().equals(event.getPlayer().getName())) {
                kick(event.getPlayer(),
                        langFile("errors.name_incorrect", ImmutableMap.of(
                                "message", "Username incorrect.",
                                "correct", json.getAsJsonObject("exact").get("username").getAsString())));
                return;
            }
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
            kick(event.getPlayer(), langFile("errors.time_out"));

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

    private void kick(Player player, String reason) {
        new BukkitRunnable() {
            @Override
            public void run() {
                player.kickPlayer(reason);
            }
        }.runTask(this);
    }
}


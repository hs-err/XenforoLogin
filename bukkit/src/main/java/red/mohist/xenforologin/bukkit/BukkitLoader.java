package red.mohist.xenforologin.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import red.mohist.xenforologin.bukkit.protocollib.ListenerProtocolEvent;
import red.mohist.xenforologin.core.enums.StatusType;
import red.mohist.xenforologin.core.forums.ForumSystems;
import red.mohist.xenforologin.core.interfaces.BukkitAPIListener;
import red.mohist.xenforologin.core.interfaces.LoaderAPI;
import red.mohist.xenforologin.core.modules.LocationInfo;
import red.mohist.xenforologin.core.modules.PlayerInfo;
import red.mohist.xenforologin.core.utils.LoginTicker;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getPlayer;
import static org.bukkit.Bukkit.getWorld;

public class BukkitLoader extends JavaPlugin implements LoaderAPI{
    public static BukkitLoader instance;
    public ConcurrentMap<UUID, StatusType> logged_in;
    public FileConfiguration config;
    public LocationInfo default_location;
    public XenforoLogin xenforoLogin;
    private ListenerProtocolEvent listenerProtocolEvent;
    @Override
    public void onEnable() {
        instance=this;
        getLogger().info("Hello, XenforoLogin!");
        saveDefaultConfig();

        ForumSystems.reloadConfig();

        hookProtocolLib();

        registerListeners();

        xenforoLogin=new XenforoLogin(this);
    }

    private void hookProtocolLib() {
        if (org.bukkit.Bukkit.getPluginManager().getPlugin("ProtocolLib") != null && config.getBoolean("secure.hide_inventory", true)) {
            listenerProtocolEvent = new ListenerProtocolEvent();
            getLogger().info("Found ProtocolLib, hooked into ProtocolLib to use \"hide_inventory\"");
        }
    }

    private void registerListeners() {
        LoginTicker.register();
        {
            int unavailableCount = 0;
            Set<Class<? extends BukkitAPIListener>> classes = new Reflections("red.mohist.xenforologin.bukkit.listeners")
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
                org.bukkit.Bukkit.getPluginManager().registerEvents(listener, this);
            }
            if (unavailableCount > 0) {
                getLogger().warning("Warning: Some features in this plugin is not available on this version of bukkit");
                getLogger().warning("If your encountered errors, do NOT report to XenforoLogin.");
                getLogger().warning("Error count: " + unavailableCount);
            }
        }
    }
    public void sendBlankInventoryPacket(Player player) {
        if (listenerProtocolEvent != null)
            listenerProtocolEvent.sendBlankInventoryPacket(player);
    }

    @Override
    public LocationInfo getSpawn(String world) {
        Location spawn_location = Objects.requireNonNull(getWorld("world")).getSpawnLocation();
        return new LocationInfo(world,
                spawn_location.getX(),
                spawn_location.getY(),
                spawn_location.getZ(),
                spawn_location.getYaw(),
                spawn_location.getPitch());
    }

    @Override
    public LocationInfo getLocation(PlayerInfo player) {
        Location location = Objects.requireNonNull(getPlayer(player.uuid)).getLocation();
        return new LocationInfo(location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch());
    }

    @Override
    public void teleport(PlayerInfo player, LocationInfo location) {
        Location leave_location=new Location(
                getWorld(location.world),
                location.x,
                location.y,
                location.z,
                location.yaw,
                location.pitch
        );
        try {
            Objects.requireNonNull(getPlayer(player.uuid)).teleportAsync(leave_location);
        } catch (NoSuchMethodError e) {
            warn("Cannot find method " + e.getMessage());
            warn("Using synchronized teleport");
            Bukkit.getScheduler().runTask(this, () -> Objects.requireNonNull(getPlayer(player.uuid)).teleport(leave_location));
        }
    }

    @Override
    public void sendMessage(PlayerInfo player, String message) {
        Objects.requireNonNull(getPlayer(player.uuid)).sendMessage(message);
    }

    @Override
    public void kickPlayer(PlayerInfo player, String message) {
        Bukkit.getScheduler().runTask(this, () -> Objects.requireNonNull(getPlayer(player.uuid))
                .kickPlayer(message));
    }

    @Override
    public void log(String message) {
        getLogger().info(message);
    }

    @Override
    public void info(String message) {
        getLogger().log(Level.FINE,message);
    }

    @Override
    public void warn(String message) {
        getLogger().warning(message);
    }

    @Override
    public Object getConfigValue(String key) {
        return getConfig().get(key);
    }

    @Override
    public Object getConfigValue(String key, Object def) {
        return getConfig().get(key,def);
    }

    @Override
    public Object getConfigValue(String file, String key, Object def) {
        File io;
        io = new File(getDataFolder(), file+".yml");
        if (!io.exists()) {
            try {
                if (!io.createNewFile()) {
                    throw new IOException("File can't be created.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return YamlConfiguration.loadConfiguration(io).get(key,def);
    }

    @Override
    public void setConfigValue(String file, String key, Object value) {
        FileConfiguration data;
        File io;
        io = new File(getDataFolder(), file+".yml");
        data=YamlConfiguration.loadConfiguration(io);
        data.set(key,value);
        try {
            data.save(io);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void login(PlayerInfo player) {
        Objects.requireNonNull(getPlayer(player.uuid)).updateInventory();
    }
    public PlayerInfo player2info(Player player){
        String ip;
        try{
            ip=player.getAddress().getHostName();
        }catch (Exception e){
            ip="0.0.0.0";
        }
        return new PlayerInfo(player.getName(),player.getUniqueId(), ip);
    }
}

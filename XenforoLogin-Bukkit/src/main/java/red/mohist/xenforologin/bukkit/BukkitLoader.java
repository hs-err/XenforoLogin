package red.mohist.xenforologin.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import red.mohist.xenforologin.bukkit.implementation.BukkitPlayer;
import red.mohist.xenforologin.bukkit.interfaces.BukkitAPIListener;
import red.mohist.xenforologin.bukkit.protocollib.ListenerProtocolEvent;
import red.mohist.xenforologin.core.XenforoLoginCore;
import red.mohist.xenforologin.core.interfaces.PlatformAdapter;
import red.mohist.xenforologin.core.modules.AbstractPlayer;
import red.mohist.xenforologin.core.modules.LocationInfo;
import red.mohist.xenforologin.core.utils.LoginTicker;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;

public class BukkitLoader extends JavaPlugin implements PlatformAdapter {
    public static BukkitLoader instance;
    public FileConfiguration config;
    public XenforoLoginCore xenforoLoginCore;
    private ListenerProtocolEvent listenerProtocolEvent;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Hello, XenforoLogin!");

        saveDefaultConfig();
        config = getConfig();

        xenforoLoginCore = new XenforoLoginCore(this);

        hookProtocolLib();

        registerListeners();
    }

    @Override
    public void onDisable() {
        xenforoLoginCore.onDisable();
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
        if (listenerProtocolEvent != null) {
            listenerProtocolEvent.sendBlankInventoryPacket(player);
        }
    }

    @Override
    public LocationInfo getSpawn(String world) {
        Location spawn_location = Objects.requireNonNull(Bukkit.getWorld("world")).getSpawnLocation();
        return new LocationInfo(world,
                spawn_location.getX(),
                spawn_location.getY(),
                spawn_location.getZ(),
                spawn_location.getYaw(),
                spawn_location.getPitch());
    }

    @Override
    public Object getConfigValue(String key) {
        return getConfig().get(key);
    }

    @Override
    public Object getConfigValue(String key, Object def) {
        return getConfig().get(key, def);
    }

    @Override
    public Object getConfigValue(String file, String key, Object def) {
        File io;
        io = new File(getDataFolder(), file + ".yml");
        if (!io.exists()) {
            try {
                if (!io.createNewFile()) {
                    throw new IOException("File can't be created.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return YamlConfiguration.loadConfiguration(io).get(key, def);
    }

    @Override
    public int getConfigValueInt(String key, int def) {
        return getConfig().getInt(key, def);
    }

    @Override
    public void setConfigValue(String file, String key, Object value) {
        FileConfiguration data;
        File io;
        io = new File(getDataFolder(), file + ".yml");
        data = YamlConfiguration.loadConfiguration(io);
        data.set(key, value);
        try {
            data.save(io);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void login(AbstractPlayer player) {
        Objects.requireNonNull(Bukkit.getPlayer(player.getUniqueId())).updateInventory();
    }

    @Override
    public void sendBlankInventoryPacket(AbstractPlayer player) {
        if(listenerProtocolEvent!=null) {
            Player p=Bukkit.getPlayer(player.getUniqueId());
            if(p!=null){
                sendBlankInventoryPacket(p);
            }
        }
    }

    public AbstractPlayer player2info(Player player) {
        return new BukkitPlayer(player);
    }
}

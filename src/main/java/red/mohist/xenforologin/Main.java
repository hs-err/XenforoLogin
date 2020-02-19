package red.mohist.xenforologin;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.*;
import java.util.logging.Logger;

import static org.bukkit.Bukkit.*;

public final class Main extends JavaPlugin implements Listener {

    public String api_url;
    public String api_key;
    public HashMap<Integer, Boolean> logined;
    public FileConfiguration config;
    public FileConfiguration location_data;
    public File location_file;
    public Location default_location;
    public static Main instance;
    private Object ListenerProtocolEvent;

    @SuppressWarnings({"ConstantConditions", "deprecation"})
    @Override
    public void onEnable() {
        getLogger().info("Hello,XenforoLogin!");
        instance=this;
        logined= new HashMap<>();
        saveDefaultConfig();
        config = getConfig();
        location_file = new File(getDataFolder(), "player_location.yml");
        if(!location_file.exists()){
            try {
                if(!location_file.createNewFile()){
                    throw new IOException("File can't be created.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        location_data = YamlConfiguration.loadConfiguration(location_file);
        api_url=config.getString("api.url");
        api_key=config.getString("api.key");
        Location spawn_location=getWorld("world").getSpawnLocation();
        default_location=new Location(
                getWorld(config.getString("spawn.world","world")),
                config.getDouble("spawn.x",spawn_location.getX()),
                config.getDouble("spawn.y",spawn_location.getY()),
                config.getDouble("spawn.z",spawn_location.getZ())
        );
        // 以下来自代码生成器
        if(config.getBoolean("event.PlayerMoveEvent",true)){
            try{
                getPluginManager().registerEvents((Listener) Class.forName("red.mohist.xenforologin.ListenerPlayerMoveEvent").newInstance(), this);
            }catch (Throwable e){
                getLogger().info("PlayerMoveEvent is not available in your server.It's not a bug!DON'T REPORT THIS!");
            }
        }
        if(config.getBoolean("event.PlayerItemConsumeEvent",true)){
            try{
                getPluginManager().registerEvents((Listener) Class.forName("red.mohist.xenforologin.ListenerPlayerItemConsumeEvent").newInstance(), this);
            }catch (Throwable e){
                getLogger().info("PlayerItemConsumeEvent is not available in your server.It's not a bug!DON'T REPORT THIS!");
            }
        }
        if(config.getBoolean("event.PlayerItemDamageEvent",true)){
            try{
                getPluginManager().registerEvents((Listener) Class.forName("red.mohist.xenforologin.ListenerPlayerItemDamageEvent").newInstance(), this);
            }catch (Throwable e){
                getLogger().info("PlayerItemDamageEvent is not available in your server.It's not a bug!DON'T REPORT THIS!");
            }
        }
        if(config.getBoolean("event.PlayerItemHeldEvent",true)){
            try{
                getPluginManager().registerEvents((Listener) Class.forName("red.mohist.xenforologin.ListenerPlayerItemHeldEvent").newInstance(), this);
            }catch (Throwable e){
                getLogger().info("PlayerItemHeldEvent is not available in your server.It's not a bug!DON'T REPORT THIS!");
            }
        }
        if(config.getBoolean("event.PlayerItemMendEvent",true)){
            try{
                getPluginManager().registerEvents((Listener) Class.forName("red.mohist.xenforologin.ListenerPlayerItemMendEvent").newInstance(), this);
            }catch (Throwable e){
                getLogger().info("PlayerItemMendEvent is not available in your server.It's not a bug!DON'T REPORT THIS!");
            }
        }
        if(config.getBoolean("event.PlayerPickupArrowEvent",true)){
            try{
                getPluginManager().registerEvents((Listener) Class.forName("red.mohist.xenforologin.ListenerPlayerPickupArrowEvent").newInstance(), this);
            }catch (Throwable e){
                getLogger().info("PlayerPickupArrowEvent is not available in your server.It's not a bug!DON'T REPORT THIS!");
            }
        }
        if(config.getBoolean("event.PlayerPickupItemEvent",true)){
            try{
                getPluginManager().registerEvents((Listener) Class.forName("red.mohist.xenforologin.ListenerPlayerPickupItemEvent").newInstance(), this);
            }catch (Throwable e){
                getLogger().info("PlayerPickupItemEvent is not available in your server.It's not a bug!DON'T REPORT THIS!");
            }
        }
        if(config.getBoolean("event.PlayerPortalEvent",true)){
            try{
                getPluginManager().registerEvents((Listener) Class.forName("red.mohist.xenforologin.ListenerPlayerPortalEvent").newInstance(), this);
            }catch (Throwable e){
                getLogger().info("PlayerPortalEvent is not available in your server.It's not a bug!DON'T REPORT THIS!");
            }
        }
        if(config.getBoolean("event.PlayerCommandPreprocessEvent",true)){
            try{
                getPluginManager().registerEvents((Listener) Class.forName("red.mohist.xenforologin.ListenerPlayerCommandPreprocessEvent").newInstance(), this);
            }catch (Throwable e){
                getLogger().info("PlayerCommandPreprocessEvent is not available in your server.It's not a bug!DON'T REPORT THIS!");
            }
        }
        if(config.getBoolean("event.PlayerRecipeDiscoverEvent",true)){
            try{
                getPluginManager().registerEvents((Listener) Class.forName("red.mohist.xenforologin.ListenerPlayerRecipeDiscoverEvent").newInstance(), this);
            }catch (Throwable e){
                getLogger().info("PlayerRecipeDiscoverEvent is not available in your server.It's not a bug!DON'T REPORT THIS!");
            }
        }
        if(config.getBoolean("event.PlayerShearEntityEvent",true)){
            try{
                getPluginManager().registerEvents((Listener) Class.forName("red.mohist.xenforologin.ListenerPlayerShearEntityEvent").newInstance(), this);
            }catch (Throwable e){
                getLogger().info("PlayerShearEntityEvent is not available in your server.It's not a bug!DON'T REPORT THIS!");
            }
        }
        if(config.getBoolean("event.PlayerSwapHandItemsEvent",true)){
            try{
                getPluginManager().registerEvents((Listener) Class.forName("red.mohist.xenforologin.ListenerPlayerSwapHandItemsEvent").newInstance(), this);
            }catch (Throwable e){
                getLogger().info("PlayerSwapHandItemsEvent is not available in your server.It's not a bug!DON'T REPORT THIS!");
            }
        }
        if(config.getBoolean("event.PlayerTakeLecternBookEvent",true)){
            try{
                getPluginManager().registerEvents((Listener) Class.forName("red.mohist.xenforologin.ListenerPlayerTakeLecternBookEvent").newInstance(), this);
            }catch (Throwable e){
                getLogger().info("PlayerTakeLecternBookEvent is not available in your server.It's not a bug!DON'T REPORT THIS!");
            }
        }
        if(config.getBoolean("event.PlayerToggleFlightEvent",true)){
            try{
                getPluginManager().registerEvents((Listener) Class.forName("red.mohist.xenforologin.ListenerPlayerToggleFlightEvent").newInstance(), this);
            }catch (Throwable e){
                getLogger().info("PlayerToggleFlightEvent is not available in your server.It's not a bug!DON'T REPORT THIS!");
            }
        }
        if(config.getBoolean("event.PlayerToggleSneakEvent",true)){
            try{
                getPluginManager().registerEvents((Listener) Class.forName("red.mohist.xenforologin.ListenerPlayerToggleSneakEvent").newInstance(), this);
            }catch (Throwable e){
                getLogger().info("PlayerToggleSneakEvent is not available in your server.It's not a bug!DON'T REPORT THIS!");
            }
        }
        if(config.getBoolean("event.PlayerToggleSprintEvent",true)){
            try{
                getPluginManager().registerEvents((Listener) Class.forName("red.mohist.xenforologin.ListenerPlayerToggleSprintEvent").newInstance(), this);
            }catch (Throwable e){
                getLogger().info("PlayerToggleSprintEvent is not available in your server.It's not a bug!DON'T REPORT THIS!");
            }
        }
        if(config.getBoolean("event.PlayerVelocityEvent",true)){
            try{
                getPluginManager().registerEvents((Listener) Class.forName("red.mohist.xenforologin.ListenerPlayerVelocityEvent").newInstance(), this);
            }catch (Throwable e){
                getLogger().info("PlayerVelocityEvent is not available in your server.It's not a bug!DON'T REPORT THIS!");
            }
        }
        if(config.getBoolean("event.BlockBreakEvent",true)){
            try{
                getPluginManager().registerEvents((Listener) Class.forName("red.mohist.xenforologin.ListenerBlockBreakEvent").newInstance(), this);
            }catch (Throwable e){
                getLogger().info("BlockBreakEvent is not available in your server.It's not a bug!DON'T REPORT THIS!");
            }
        }
        if(config.getBoolean("event.BlockDamageEvent",true)){
            try{
                getPluginManager().registerEvents((Listener) Class.forName("red.mohist.xenforologin.ListenerBlockDamageEvent").newInstance(), this);
            }catch (Throwable e){
                getLogger().info("BlockDamageEvent is not available in your server.It's not a bug!DON'T REPORT THIS!");
            }
        }
        if(config.getBoolean("event.BlockDropItemEvent",true)){
            try{
                getPluginManager().registerEvents((Listener) Class.forName("red.mohist.xenforologin.ListenerBlockDropItemEvent").newInstance(), this);
            }catch (Throwable e){
                getLogger().info("BlockDropItemEvent is not available in your server.It's not a bug!DON'T REPORT THIS!");
            }
        }
        if(config.getBoolean("event.BlockFertilizeEvent",true)){
            try{
                getPluginManager().registerEvents((Listener) Class.forName("red.mohist.xenforologin.ListenerBlockFertilizeEvent").newInstance(), this);
            }catch (Throwable e){
                getLogger().info("BlockFertilizeEvent is not available in your server.It's not a bug!DON'T REPORT THIS!");
            }
        }
        if(config.getBoolean("event.BlockMultiPlaceEvent",true)){
            try{
                getPluginManager().registerEvents((Listener) Class.forName("red.mohist.xenforologin.ListenerBlockMultiPlaceEvent").newInstance(), this);
            }catch (Throwable e){
                getLogger().info("BlockMultiPlaceEvent is not available in your server.It's not a bug!DON'T REPORT THIS!");
            }
        }
        if(config.getBoolean("event.BlockPlaceEvent",true)){
            try{
                getPluginManager().registerEvents((Listener) Class.forName("red.mohist.xenforologin.ListenerBlockPlaceEvent").newInstance(), this);
            }catch (Throwable e){
                getLogger().info("BlockPlaceEvent is not available in your server.It's not a bug!DON'T REPORT THIS!");
            }
        }
        if(config.getBoolean("event.SignChangeEvent",true)){
            try{
                getPluginManager().registerEvents((Listener) Class.forName("red.mohist.xenforologin.ListenerSignChangeEvent").newInstance(), this);
            }catch (Throwable e){
                getLogger().info("SignChangeEvent is not available in your server.It's not a bug!DON'T REPORT THIS!");
            }
        }
        if(config.getBoolean("event.PlayerLeashEntityEvent",true)){
            try{
                getPluginManager().registerEvents((Listener) Class.forName("red.mohist.xenforologin.ListenerPlayerLeashEntityEvent").newInstance(), this);
            }catch (Throwable e){
                getLogger().info("PlayerLeashEntityEvent is not available in your server.It's not a bug!DON'T REPORT THIS!");
            }
        }
        if(config.getBoolean("event.PlayerDeathEvent",true)){
            try{
                getPluginManager().registerEvents((Listener) Class.forName("red.mohist.xenforologin.ListenerPlayerDeathEvent").newInstance(), this);
            }catch (Throwable e){
                getLogger().info("PlayerDeathEvent is not available in your server.It's not a bug!DON'T REPORT THIS!");
            }
        }
        if(config.getBoolean("event.InventoryClickEvent",true)){
            try{
                getPluginManager().registerEvents((Listener) Class.forName("red.mohist.xenforologin.ListenerInventoryClickEvent").newInstance(), this);
            }catch (Throwable e){
                getLogger().info("InventoryClickEvent is not available in your server.It's not a bug!DON'T REPORT THIS!");
            }
        }
        if(config.getBoolean("event.InventoryDragEvent",true)){
            try{
                getPluginManager().registerEvents((Listener) Class.forName("red.mohist.xenforologin.ListenerInventoryDragEvent").newInstance(), this);
            }catch (Throwable e){
                getLogger().info("InventoryDragEvent is not available in your server.It's not a bug!DON'T REPORT THIS!");
            }
        }
        if(config.getBoolean("event.InventoryOpenEvent",true)){
            try{
                getPluginManager().registerEvents((Listener) Class.forName("red.mohist.xenforologin.ListenerInventoryOpenEvent").newInstance(), this);
            }catch (Throwable e){
                getLogger().info("InventoryOpenEvent is not available in your server.It's not a bug!DON'T REPORT THIS!");
            }
        }
        if(config.getBoolean("event.TradeSelectEvent",true)){
            try{
                getPluginManager().registerEvents((Listener) Class.forName("red.mohist.xenforologin.ListenerTradeSelectEvent").newInstance(), this);
            }catch (Throwable e){
                getLogger().info("TradeSelectEvent is not available in your server.It's not a bug!DON'T REPORT THIS!");
            }
        }
        if(config.getBoolean("event.RaidTriggerEvent",true)){
            try{
                getPluginManager().registerEvents((Listener) Class.forName("red.mohist.xenforologin.ListenerRaidTriggerEvent").newInstance(), this);
            }catch (Throwable e){
                getLogger().info("RaidTriggerEvent is not available in your server.It's not a bug!DON'T REPORT THIS!");
            }
        }
        if(config.getBoolean("event.EntityDamageEvent",true)){
            try{
                getPluginManager().registerEvents((Listener) Class.forName("red.mohist.xenforologin.ListenerEntityDamageEvent").newInstance(), this);
            }catch (Throwable e){
                getLogger().info("EntityDamageEvent is not available in your server.It's not a bug!DON'T REPORT THIS!");
            }
        }
        if(config.getBoolean("event.EntityDamageByEntityEvent",true)){
            try{
                getPluginManager().registerEvents((Listener) Class.forName("red.mohist.xenforologin.ListenerEntityDamageByEntityEvent").newInstance(), this);
            }catch (Throwable e){
                getLogger().info("EntityDamageByEntityEvent is not available in your server.It's not a bug!DON'T REPORT THIS!");
            }
        }
        if(config.getBoolean("secure.hidden_bagpack",true)){
            try{
                ListenerProtocolEvent=Class.forName("red.mohist.xenforologin.ListenerProtocolEvent").newInstance();
            }catch (Throwable e){
                getLogger().info("ProtocolEvent is not available in your server.It's not a bug!DON'T REPORT THIS!");
            }
        }

        getLogger().info("API URL: "+api_url);
        getLogger().info("API KEY: "+api_key);
        getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) throws IOException, InvalidConfigurationException {
        if(!needcancelled(event.getPlayer())) {
            if(config.getBoolean("secure.cancell_chat_after_login",false)) {
                event.getPlayer().sendMessage(t("logined"));
                event.setCancelled(true);
            }
            return;
        }
        event.setCancelled(true);
        ResponseHandler<String> responseHandler = response -> {
            int status = response.getStatusLine().getStatusCode();
            if (status == 200 || status == 400) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            }else if(status == 403) {
                getLogger().warning(t("errors.key", ImmutableMap.of(
                        "key", api_key)));
                throw new ClientProtocolException("Unexpected response status: " + status);
            }else if(status == 404) {
                getLogger().warning(t("errors.url", ImmutableMap.of(
                        "url", api_url)));
                throw new ClientProtocolException("Unexpected response status: " + status);
            }else{
                throw new ClientProtocolException("Unexpected response status: " + status);

            }
        };
        String result= Request.Post(api_url+"/auth")
                .bodyForm(Form.form().add("login",  event.getPlayer().getName())
                        .add("password" ,event.getMessage()).build())
                .addHeader("XF-Api-Key",api_key)
                .execute().handleResponse(responseHandler);


        if(result==null){
            throw new ClientProtocolException("Unexpected response: null");
        }
        JsonParser parse =new JsonParser();
        JsonObject json=parse.parse(result).getAsJsonObject();
        if(json == null) {
            throw new ClientProtocolException("Unexpected json: null");
        }
        if(json.get("success")!=null && json.get("success").getAsBoolean()){
            json.get("user").getAsJsonObject().get("username").getAsString();
            if(json.get("user").getAsJsonObject().get("username").getAsString().equals(event.getPlayer().getName())) {
                logined.put(event.getPlayer().hashCode(), true);
                if(config.getBoolean("event.tp_back_after_login",true)) {
                    location_data.load(location_file);
                    Location spawn_location = getWorld("world").getSpawnLocation();
                    Location leave_location = new Location(
                            getWorld(UUID.fromString(location_data.getString(
                                    event.getPlayer().getUniqueId().toString() + ".world",
                                    spawn_location.getWorld().getUID().toString()))),
                            location_data.getDouble(event.getPlayer().getUniqueId().toString() + ".x", spawn_location.getX()),
                            location_data.getDouble(event.getPlayer().getUniqueId().toString() + ".y", spawn_location.getY()),
                            location_data.getDouble(event.getPlayer().getUniqueId().toString() + ".z", spawn_location.getZ())
                    );
                    event.getPlayer().teleportAsync(leave_location);
                }
                event.getPlayer().updateInventory();
                getLogger().info("set true: " + event.getPlayer().getUniqueId());
                event.getPlayer().sendMessage(t("success"));
            }else{
                event.getPlayer().kickPlayer(t("errors.name_incorrect",ImmutableMap.of(
                        "message", "Username incorrect.",
                        "correct",json.get("user").getAsJsonObject().get("username").getAsString()
                )));
            }
        }else{
            JsonArray errors=json.get("errors").getAsJsonArray();
            int k=errors.size();
            for(int i=0;i<k;i++){
                JsonObject error=errors.get(i).getAsJsonObject();
                event.getPlayer().sendMessage(t("errors."+error.get("code").getAsString(),ImmutableMap.of(
                        "message", error.get("message").getAsString()
                )));
            }
        }

    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnJoin(PlayerJoinEvent event) {
        logined.put(event.getPlayer().hashCode(),false);
        if(config.getBoolean("tp.tp_spawn_before_login",true)){
            event.getPlayer().teleport(default_location);
        }
        sendBlankInventoryPacket(event.getPlayer());
        new Thread(() -> {
            ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                }else if(status == 401) {
                    getLogger().warning(t("errors.key", ImmutableMap.of(
                            "key", api_key)));
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }else if(status == 404) {
                    getLogger().warning(t("errors.url", ImmutableMap.of(
                            "url", api_url)));
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }else{
                    throw new ClientProtocolException("Unexpected response status: " + status);

                }
            };
            String result= null;
            try {
                result = Request.Get(api_url+"/users/find-name?username="+
                        URLEncoder.encode(event.getPlayer().getName(),"UTF-8"))
                        .addHeader("XF-Api-Key",api_key)
                        .execute().handleResponse(responseHandler);
            } catch (IOException e) {
                kick(event.getPlayer(),t("errors.server"));
                e.printStackTrace();
                return;
            }
            if(result==null){
                kick(event.getPlayer(),t("errors.server"));
                new ClientProtocolException("Unexpected response: null").printStackTrace();
            }
            JsonParser parse =new JsonParser();
            JsonObject json=parse.parse(result).getAsJsonObject();
            if(json == null) {
                kick(event.getPlayer(),t("errors.server"));
                new ClientProtocolException("Unexpected json: null").printStackTrace();
            }
            if (json.get("exact").isJsonNull()) {
                kick(event.getPlayer(),t("errors.no_user"));
                return;
            }
            if(!json.getAsJsonObject("exact").get("username").getAsString().equals(event.getPlayer().getName())) {
                kick(event.getPlayer(),
                        t("errors.name_incorrect",ImmutableMap.of(
                                "message", "Username incorrect.",
                                "correct",json.getAsJsonObject("exact").get("username").getAsString())));
                return;
            }
            int f=0;
            int s=config.getInt("secure.show_tips_time",5);
            int t=config.getInt("secure.max_login_time",30);
            while(true){
                sendBlankInventoryPacket(event.getPlayer());
                event.getPlayer().sendMessage(t("need_login"));
                try {
                    Thread.sleep(s*1000);
                    f+=s;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(f>t){
                    break;
                }
                if(!event.getPlayer().isOnline() || !needcancelled(event.getPlayer())){
                    return;
                }
            }
            kick(event.getPlayer(),t("errors.time_out"));

        }).start();
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void OnQuit(PlayerQuitEvent event) throws IOException {
        Location leave_location=event.getPlayer().getLocation();
        if(!needcancelled(event.getPlayer())){
            location_data.set(event.getPlayer().getUniqueId().toString()+".world",leave_location.getWorld().getUID().toString());
            location_data.set(event.getPlayer().getUniqueId().toString()+".x",leave_location.getX());
            location_data.set(event.getPlayer().getUniqueId().toString()+".y",leave_location.getY());
            location_data.set(event.getPlayer().getUniqueId().toString()+".z",leave_location.getZ());
            location_data.save(location_file);
        }
        logined.remove(event.getPlayer().hashCode());
    }
    public boolean needcancelled(Player player){
        return !logined.getOrDefault(player.hashCode(), false);
    }
    private String t(String key){
        String result=config.getString("lang."+key);
        if(result==null){
            return key;
        }
        return result;
    }
    private String t(String key, Map<String,String> data){
        String result=config.getString("lang."+key);
        if(result==null){
            StringBuilder resultBuilder = new StringBuilder(key);
            resultBuilder.append("\n");
            for(Map.Entry<String, String> entry : data.entrySet()){
                resultBuilder.append(entry.getKey()).append(":").append(entry.getValue());
            }
            result = resultBuilder.toString();
            return result;
        }
        for(Map.Entry<String, String> entry : data.entrySet()){
            result=result.replace("["+entry.getKey()+"]",entry.getValue());
        }
        return result;
    }
    private void sendBlankInventoryPacket(Player player){
        if(ListenerProtocolEvent!=null){
            try {
                ListenerProtocolEvent
                        .getClass()
                        .getMethod("sendBlankInventoryPacket", new Class[]{Player.class})
                        .invoke(ListenerProtocolEvent, player);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }
    private void kick(Player player,String reason){
        new BukkitRunnable() {
            @Override
            public void run() {
                player.kickPlayer(reason);
            }
        }.runTask(this);
    }
}


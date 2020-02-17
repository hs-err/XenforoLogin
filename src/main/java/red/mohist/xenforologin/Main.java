package red.mohist.xenforologin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
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
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.TradeSelectEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.raid.RaidTriggerEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.*;

import static org.bukkit.Bukkit.*;

public final class Main extends JavaPlugin implements Listener {

    private String api_url;
    private String api_key;
    private HashMap<Integer, Boolean> logined;
    private FileConfiguration config;
    private FileConfiguration location_data;
    private File location_file;
    private Location default_location;
    private ProtocolManager protocolManager;

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onEnable() {
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
        getLogger().info("Hello,XenforoLogin!");
        getLogger().info("API URL: "+api_url);
        getLogger().info("API KEY: "+api_key);
        getPluginManager().registerEvents(this, this);
        protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(
                new PacketAdapter(this, ListenerPriority.LOWEST,
                        PacketType.Play.Server.WINDOW_ITEMS,PacketType.Play.Server.SET_SLOT) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        if (event.getPacket().getIntegers().read(0) == 0 && needcancelled(event.getPlayer())) {
                            event.setCancelled(true);
                        }
                    }
                });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) throws IOException, InvalidConfigurationException {
        if(!needcancelled(event.getPlayer())) {
            event.getPlayer().sendMessage(t("logined"));
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
        if(json != null){
            if(json.get("success")!=null && json.get("success").getAsBoolean()){
                json.get("user").getAsJsonObject().get("username").getAsString();
                if(json.get("user").getAsJsonObject().get("username").getAsString().equals(event.getPlayer().getName())) {
                    logined.put(event.getPlayer().hashCode(), true);
                    location_data.load(location_file);
                    Location spawn_location=getWorld("world").getSpawnLocation();
                    Location leave_location=new Location(
                            getWorld(UUID.fromString(location_data.getString(
                                    event.getPlayer().getUniqueId().toString()+".world",
                                    spawn_location.getWorld().getUID().toString()))),
                            location_data.getDouble(event.getPlayer().getUniqueId().toString()+".x",spawn_location.getX()),
                            location_data.getDouble(event.getPlayer().getUniqueId().toString()+".y",spawn_location.getY()),
                            location_data.getDouble(event.getPlayer().getUniqueId().toString()+".z",spawn_location.getZ())
                    );
                    event.getPlayer().updateInventory();
                    event.getPlayer().teleportAsync(leave_location);
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
        }else{
            throw new ClientProtocolException("Unexpected json: null");
        }

    }
    @EventHandler
    public void OnJoin(PlayerJoinEvent event) {
        logined.put(event.getPlayer().hashCode(),false);
        event.getPlayer().teleport(default_location);
        sendBlankInventoryPacket(event.getPlayer());
        JavaPlugin plugin=this;
        new Thread(){
            @Override
            public void run() {
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
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            event.getPlayer().kickPlayer(t("errors.server"));
                        }
                    }.runTask(plugin);
                    e.printStackTrace();
                    return;
                }


                if(result==null){
                    new ClientProtocolException("Unexpected response: null").printStackTrace();
                }
                JsonParser parse =new JsonParser();
                JsonObject json=parse.parse(result).getAsJsonObject();
                if(json != null){
                    if (json.get("exact").isJsonNull()) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                event.getPlayer().kickPlayer(t("errors.no_user"));
                            }
                        }.runTask(plugin);
                    } else if(!json.getAsJsonObject("exact").get("username").getAsString().equals(event.getPlayer().getName())) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                event.getPlayer().kickPlayer(t("errors.name_incorrect",ImmutableMap.of(
                                        "message", "Username incorrect.",
                                        "correct",json.getAsJsonObject("exact").get("username").getAsString()
                                )));
                            }
                        }.runTask(plugin);
                    }else{
                        for(int i=0;i<6;i++){
                            sendBlankInventoryPacket(event.getPlayer());
                            event.getPlayer().sendMessage(t("need_login"));
                            try {
                                sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if(!event.getPlayer().isOnline() || !needcancelled(event.getPlayer())){
                                return;
                            }
                        }
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                event.getPlayer().kickPlayer(t("errors.time_out"));
                            }
                        }.runTask(plugin);
                    }
                }else{
                    new ClientProtocolException("Unexpected json: null").printStackTrace();
                }
            }
        }.start();
    }
    @EventHandler
    public void OnQuit(PlayerQuitEvent event) throws IOException {
        Location leave_location=event.getPlayer().getLocation();
        if(!needcancelled(event.getPlayer())){
            location_data.set(event.getPlayer().getUniqueId().toString()+".world",leave_location.getWorld().getUID().toString());
            location_data.set(event.getPlayer().getUniqueId().toString()+".x",leave_location.getX());
            location_data.set(event.getPlayer().getUniqueId().toString()+".y",leave_location.getY());
            location_data.set(event.getPlayer().getUniqueId().toString()+".z",leave_location.getZ());
            location_data.save(location_file);
        }
        event.getPlayer().teleport(default_location);
        logined.remove(event.getPlayer().hashCode());
    }
    @EventHandler
    public void OnMove(PlayerMoveEvent event){
        if(needcancelled(event.getPlayer())){
            Location location=event.getTo();
            location.setX(default_location.getX());
            location.setZ(default_location.getZ());
            event.setTo(location);
        }
    }
    private boolean needcancelled(Player player){
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
    public void sendBlankInventoryPacket(Player player) {
        PacketContainer inventoryPacket = protocolManager.createPacket(PacketType.Play.Server.WINDOW_ITEMS);
        inventoryPacket.getIntegers().write(0, 0);
        ItemStack[] blankInventory = new ItemStack[45];
        int k=blankInventory.length;
        for(int i=0;i<k;i++){
            blankInventory[i]=new ItemStack(Material.AIR);
        }
        StructureModifier<ItemStack[]> itemArrayModifier = inventoryPacket.getItemArrayModifier();
        if (itemArrayModifier.size() > 0) {
            itemArrayModifier.write(0, blankInventory);
        } else {
            StructureModifier<List<ItemStack>> itemListModifier = inventoryPacket.getItemListModifier();
            itemListModifier.write(0, Arrays.asList(blankInventory));
        }
        try {
            protocolManager.sendServerPacket(player, inventoryPacket, false);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    /** Notice:No code below *
     * Notice:No code below *
     * Notice:No code below *
     * Notice:No code below *
     * Notice:No code below *
     * Notice:No code below *
     * Notice:No code below *
     * Notice:No code below *
     * Notice:No code below *
     * Notice:No code below *
     * Notice:No code below *
     * Notice:No code below *
     * Notice:No code below *
     * Notice:No code below *
     * Notice:No code below *
     * Notice:No code below *
     * Notice:No code below *
     * Notice:No code below *
     * Notice:No code below *
     * Notice:No code below *
     * Notice:No code below *
     * Notice:No code below *
     * Notice:No code below *
     * Notice:No code below **/
    @EventHandler
    public void OnPlayerItemConsumeEvent(PlayerItemConsumeEvent event){
        if(needcancelled(event.getPlayer())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void OnPlayerItemDamageEvent(PlayerItemDamageEvent event){
        if(needcancelled(event.getPlayer())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void OnPlayerItemHeldEvent(PlayerItemHeldEvent event){
        if(needcancelled(event.getPlayer())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void OnPlayerItemMendEvent(PlayerItemMendEvent event){
        if(needcancelled(event.getPlayer())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void OnPlayerPickupArrowEvent(PlayerPickupArrowEvent event){
        if(needcancelled(event.getPlayer())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void OnPlayerPickupItemEvent(PlayerPickupItemEvent event){
        if(needcancelled(event.getPlayer())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void OnPlayerPortalEvent(PlayerPortalEvent event){
        if(needcancelled(event.getPlayer())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void OnPlayerRecipeDiscoverEvent(PlayerRecipeDiscoverEvent event){
        if(needcancelled(event.getPlayer())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void OnPlayerShearEntityEvent(PlayerShearEntityEvent event){
        if(needcancelled(event.getPlayer())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void OnPlayerSwapHandItemsEvent(PlayerSwapHandItemsEvent event){
        if(needcancelled(event.getPlayer())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void OnPlayerTakeLecternBookEvent(PlayerTakeLecternBookEvent event){
        if(needcancelled(event.getPlayer())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void OnPlayerToggleFlightEvent(PlayerToggleFlightEvent event){
        if(needcancelled(event.getPlayer())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void OnPlayerToggleSneakEvent(PlayerToggleSneakEvent event){
        if(needcancelled(event.getPlayer())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void OnPlayerToggleSprintEvent(PlayerToggleSprintEvent event){
        if(needcancelled(event.getPlayer())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void OnPlayerVelocityEvent(PlayerVelocityEvent event){
        if(needcancelled(event.getPlayer())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void OnBlockBreakEvent(BlockBreakEvent event){
        if(needcancelled(event.getPlayer())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void OnBlockDamageEvent(BlockDamageEvent event){
        if(needcancelled(event.getPlayer())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void OnBlockDropItemEvent(BlockDropItemEvent event){
        if(needcancelled(event.getPlayer())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void OnBlockFertilizeEvent(BlockFertilizeEvent event){
        if(needcancelled(Objects.requireNonNull(event.getPlayer()))){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void OnBlockMultiPlaceEvent(BlockMultiPlaceEvent event){
        if(needcancelled(event.getPlayer())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void OnBlockPlaceEvent(BlockPlaceEvent event){
        if(needcancelled(event.getPlayer())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void OnSignChangeEvent(SignChangeEvent event){
        if(needcancelled(event.getPlayer())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void OnPlayerLeashEntityEvent(PlayerLeashEntityEvent event){
        if(needcancelled(event.getPlayer())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void OnPlayerDeathEvent(PlayerDeathEvent event){
        if(needcancelled(event.getEntity())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void OnInventoryClickEvent(InventoryClickEvent event){
        if(needcancelled((Player)event.getWhoClicked())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void OnInventoryDragEvent(InventoryDragEvent event){
        if(needcancelled((Player)event.getWhoClicked())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void OnInventoryOpenEvent(InventoryOpenEvent event){
        if(needcancelled((Player)event.getPlayer())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void OnTradeSelectEvent(TradeSelectEvent event){
        if(needcancelled((Player)event.getWhoClicked())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void OnRaidTriggerEvent(RaidTriggerEvent event){
        if(needcancelled(event.getPlayer())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void OnEntityDamageEvent(EntityDamageEvent event){
        if(event.getEntityType()== EntityType.PLAYER){
            if(needcancelled((Player)event.getEntity())){
                event.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void OnEntityDamageByEntityEvent(EntityDamageByEntityEvent event){
        if(event.getEntityType()== EntityType.PLAYER){
            if(needcancelled((Player)event.getEntity())){
                event.setCancelled(true);
            }
        }
        if(event.getDamager().getType() == EntityType.PLAYER){
            if(needcancelled((Player)event.getDamager())){
                event.setCancelled(true);
            }
        }
    }
}


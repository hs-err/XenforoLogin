package red.mohist.xenforologin.listeners;

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
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import red.mohist.xenforologin.Main;
import red.mohist.xenforologin.interfaces.BukkitAPIListener;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import static org.bukkit.Bukkit.getWorld;

public class ListenerChatEvent implements BukkitAPIListener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) throws IOException, InvalidConfigurationException {
        if (!Main.instance.needCancelled(event.getPlayer())) {
            if (Main.instance.config.getBoolean("secure.cancel_chat_after_login", false)) {
                event.getPlayer().sendMessage(Main.instance.langFile("logged_in"));
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
            } else if (status == 403) {
                Main.instance.getLogger().warning(Main.instance.langFile("errors.key", ImmutableMap.of(
                        "key", Main.instance.api_key)));
                throw new ClientProtocolException("Unexpected response status: " + status);
            } else if (status == 404) {
                Main.instance.getLogger().warning(Main.instance.langFile("errors.url", ImmutableMap.of(
                        "url", Main.instance.api_url)));
                throw new ClientProtocolException("Unexpected response status: " + status);
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);

            }
        };

        String result = Request.Post(Main.instance.api_url + "/auth")
                .bodyForm(Form.form().add("login", event.getPlayer().getName())
                        .add("password", event.getMessage()).build())
                .addHeader("XF-Api-Key", Main.instance.api_key)
                .execute().handleResponse(responseHandler);


        if (result == null) {
            throw new ClientProtocolException("Unexpected response: null");
        }
        JsonParser parse = new JsonParser();
        JsonObject json = parse.parse(result).getAsJsonObject();
        if (json == null) {
            throw new ClientProtocolException("Unexpected json: null");
        }
        if (json.get("success") != null && json.get("success").getAsBoolean()) {
            json.get("user").getAsJsonObject().get("username").getAsString();
            if (json.get("user").getAsJsonObject().get("username").getAsString().equals(event.getPlayer().getName())) {
                Main.instance.logged_in.put(event.getPlayer().hashCode(), true);
                if (Main.instance.config.getBoolean("event.tp_back_after_login", true)) {
                    Main.instance.location_data.load(Main.instance.location_file);
                    Location spawn_location = Objects.requireNonNull(getWorld("world")).getSpawnLocation();
                    Location leave_location = new Location(
                            getWorld(UUID.fromString(Objects.requireNonNull(Main.instance.location_data.getString(
                                    event.getPlayer().getUniqueId().toString() + ".world",
                                    spawn_location.getWorld().getUID().toString())))),
                            Main.instance.location_data.getDouble(event.getPlayer().getUniqueId().toString() + ".x", spawn_location.getX()),
                            Main.instance.location_data.getDouble(event.getPlayer().getUniqueId().toString() + ".y", spawn_location.getY()),
                            Main.instance.location_data.getDouble(event.getPlayer().getUniqueId().toString() + ".z", spawn_location.getZ())
                    );
                    event.getPlayer().teleportAsync(leave_location);
                }
                event.getPlayer().updateInventory();
                Main.instance.getLogger().info("Logging in " + event.getPlayer().getUniqueId());
                event.getPlayer().sendMessage(Main.instance.langFile("success"));
            } else {
                event.getPlayer().kickPlayer(Main.instance.langFile("errors.name_incorrect", ImmutableMap.of(
                        "message", "Username incorrect.",
                        "correct", json.get("user").getAsJsonObject().get("username").getAsString()
                )));
            }
        } else {
            JsonArray errors = json.get("errors").getAsJsonArray();
            int k = errors.size();
            for (int i = 0; i < k; i++) {
                JsonObject error = errors.get(i).getAsJsonObject();
                event.getPlayer().sendMessage(Main.instance.langFile("errors." + error.get("code").getAsString(), ImmutableMap.of(
                        "message", error.get("message").getAsString()
                )));
            }
        }

    }

    @Override
    public void eventClass() {

    }
}

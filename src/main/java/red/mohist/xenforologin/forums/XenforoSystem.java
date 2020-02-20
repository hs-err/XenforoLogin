package red.mohist.xenforologin.forums;

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
import org.bukkit.entity.Player;
import red.mohist.xenforologin.XenforoLogin;
import red.mohist.xenforologin.enums.ResultType;
import red.mohist.xenforologin.interfaces.ForumSystem;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getWorld;

public class XenforoSystem implements ForumSystem {

    private final String url;
    private final String key;

    public XenforoSystem(String url, String key) {
        this.url = url;
        this.key = key;
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Nonnull
    @Override
    public ResultType register(Player player, String password, String email) {
        return null;
    }

    @Nonnull
    @Override
    public ResultType login(Player player, String password) {
        try {
            ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                if (status == 200 || status == 400) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else if (status == 403) {
                    XenforoLogin.instance.getLogger().warning(XenforoLogin.instance.langFile("errors.key", ImmutableMap.of(
                            "key", XenforoLogin.instance.api_key)));
                    throw new ClientProtocolException("Unexpected response status: " + status);
                } else if (status == 404) {
                    XenforoLogin.instance.getLogger().warning(XenforoLogin.instance.langFile("errors.url", ImmutableMap.of(
                            "url", XenforoLogin.instance.api_url)));
                    throw new ClientProtocolException("Unexpected response status: " + status);
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);

                }
            };

            String result = Request.Post(XenforoLogin.instance.api_url + "/auth")
                    .bodyForm(Form.form().add("login", player.getName())
                            .add("password", password).build())
                    .addHeader("XF-Api-Key", XenforoLogin.instance.api_key)
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
                if (json.get("user").getAsJsonObject().get("username").getAsString().equals(player.getName())) {
                    XenforoLogin.instance.logged_in.put(player.hashCode(), true);
                    if (XenforoLogin.instance.config.getBoolean("event.tp_back_after_login", true)) {
                        XenforoLogin.instance.location_data.load(XenforoLogin.instance.location_file);
                        Location spawn_location = Objects.requireNonNull(getWorld("world")).getSpawnLocation();
                        Location leave_location = new Location(
                                getWorld(UUID.fromString(Objects.requireNonNull(XenforoLogin.instance.location_data.getString(
                                        player.getUniqueId().toString() + ".world",
                                        spawn_location.getWorld().getUID().toString())))),
                                XenforoLogin.instance.location_data.getDouble(player.getUniqueId().toString() + ".x", spawn_location.getX()),
                                XenforoLogin.instance.location_data.getDouble(player.getUniqueId().toString() + ".y", spawn_location.getY()),
                                XenforoLogin.instance.location_data.getDouble(player.getUniqueId().toString() + ".z", spawn_location.getZ())
                        );
                        player.teleportAsync(leave_location);
                    }
                    player.updateInventory();
                    XenforoLogin.instance.getLogger().info("Logging in " + player.getUniqueId());
                    player.sendMessage(XenforoLogin.instance.langFile("success"));
                } else {
                    player.kickPlayer(XenforoLogin.instance.langFile("errors.name_incorrect", ImmutableMap.of(
                            "message", "Username incorrect.",
                            "correct", json.get("user").getAsJsonObject().get("username").getAsString()
                    )));
                }
            } else {
                JsonArray errors = json.get("errors").getAsJsonArray();
                int k = errors.size();
                for (int i = 0; i < k; i++) {
                    JsonObject error = errors.get(i).getAsJsonObject();
                    player.sendMessage(XenforoLogin.instance.langFile("errors." + error.get("code").getAsString(), ImmutableMap.of(
                            "message", error.get("message").getAsString()
                    )));
                }
            }
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Error while checking player " + player.getName() + " data", e);
            return ResultType.SERVER_ERROR;
        }
        return ResultType.OK;
    }

    @Nonnull
    @Override
    public ResultType join(Player player) {
        return null;
    }
}

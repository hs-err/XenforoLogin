package red.mohist.xenforologin.core;

import red.mohist.xenforologin.core.enums.ResultType;
import red.mohist.xenforologin.core.enums.StatusType;
import red.mohist.xenforologin.core.forums.ForumSystems;
import red.mohist.xenforologin.core.interfaces.PlatformAdapter;
import red.mohist.xenforologin.core.modules.AbstractPlayer;
import red.mohist.xenforologin.core.modules.LocationInfo;
import red.mohist.xenforologin.core.utils.LoginTicker;
import red.mohist.xenforologin.core.utils.ResultTypeUtils;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class XenforoLoginCore {

    public static XenforoLoginCore instance;
    public PlatformAdapter api;
    public ConcurrentMap<UUID, StatusType> logged_in;
    public File location_file;
    public LocationInfo default_location;

    public XenforoLoginCore(PlatformAdapter platformAdapter) {
        instance = this;
        api = platformAdapter;
        logged_in = new ConcurrentHashMap<>();
        loadConfig();

        ForumSystems.reloadConfig();
        LoginTicker.register();
    }

    public void onDisable() {
        LoginTicker.unregister();
    }

    private void loadConfig() {
        LocationInfo spawn_location = api.getSpawn("world");
        default_location = new LocationInfo(
                (String) api.getConfigValue("spawn.world", "world"),
                (Double) api.getConfigValue("spawn.x", spawn_location.x),
                (Double) api.getConfigValue("spawn.y", spawn_location.y),
                (Double) api.getConfigValue("spawn.z", spawn_location.z),
                (Float) api.getConfigValue("spawn.yaw", spawn_location.yaw),
                (Float) api.getConfigValue("spawn.pitch", spawn_location.pitch)
        );
    }

    public boolean needCancelled(AbstractPlayer player) {
        return !logged_in.getOrDefault(player.getUniqueId(), StatusType.NEED_LOGIN).equals(StatusType.LOGGED_IN);
    }

    public String langFile(String key) {
        String result = (String) api.getConfigValue("lang." + key);
        if (result == null) {
            return key;
        }
        return result;
    }

    public String langFile(String key, Map<String, String> data) {
        String result = (String) api.getConfigValue("lang." + key);
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

    public void login(AbstractPlayer player) {
        try {
            if (api.getConfigValue("event.tp_back_after_login", "true").equals("true")) {
                api.getConfigValue("player_location");
                LocationInfo spawn_location = api.getSpawn("world");
                player.teleport(new LocationInfo(
                        (String) api.getConfigValue("player_location",
                                String.format("%s.world", player.getUniqueId()), spawn_location.world),
                        (Double) api.getConfigValue("player_location",
                                String.format("%s.x", player.getUniqueId()), spawn_location.x),
                        (Double) api.getConfigValue("player_location",
                                String.format("%s.y", player.getUniqueId()), spawn_location.y),
                        (Double) api.getConfigValue("player_location",
                                String.format("%s.y", player.getUniqueId()), spawn_location.z),
                        (Float) api.getConfigValue("player_location",
                                String.format("%s.yaw", player.getUniqueId()), spawn_location.yaw),
                        (Float) api.getConfigValue("player_location",
                                String.format("%s.pitch", player.getUniqueId()), spawn_location.pitch)
                ));
            }
            logged_in.put(player.getUniqueId(), StatusType.LOGGED_IN);
            api.login(player);
            api.getLogger().info("Logging in " + player.getUniqueId());
            player.sendMessage(XenforoLoginCore.instance.langFile("success"));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void message(AbstractPlayer player) {
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

    public void onQuit(AbstractPlayer player) {
        LocationInfo leave_location = player.getLocation();
        if (!needCancelled(player)) {
            api.setConfigValue("player_location",
                    String.format("%s.world", player.getUniqueId().toString()), leave_location.world);
            api.setConfigValue("player_location",
                    String.format("%s.x", player.getUniqueId().toString()), leave_location.x);
            api.setConfigValue("player_location",
                    String.format("%s.y", player.getUniqueId().toString()), leave_location.y);
            api.setConfigValue("player_location",
                    String.format("%s.z", player.getUniqueId().toString()), leave_location.z);
            api.setConfigValue("player_location",
                    String.format("%s.yaw", player.getUniqueId().toString()), leave_location.yaw);
            api.setConfigValue("player_location",
                    String.format("%s.pitch", player.getUniqueId().toString()), leave_location.pitch);
        }
        logged_in.remove(player.getUniqueId());
    }

    public String canJoin(AbstractPlayer player) {
        if (XenforoLoginCore.instance.logged_in.containsKey(player.getUniqueId())) {
            return null;
        }
        ResultType resultType = ForumSystems.getCurrentSystem()
                .join(player.getName())
                .shouldLogin(false);
        switch (resultType) {
            case OK:
                XenforoLoginCore.instance.logged_in.put(player.getUniqueId(), StatusType.NEED_LOGIN);
                return null;
            case ERROR_NAME:
                return XenforoLoginCore.instance.langFile("errors.name_incorrect",
                        resultType.getInheritedObject());
            case NO_USER:
                if (XenforoLoginCore.instance.api.getConfigValue("api.register", "false") == "true") {
                    XenforoLoginCore.instance.logged_in.put(player.getUniqueId(), StatusType.NEED_REGISTER_EMAIL);
                    return null;
                } else {
                    return XenforoLoginCore.instance.langFile("errors.no_user");
                }
            case UNKNOWN:
                return XenforoLoginCore.instance.langFile("errors.unknown", resultType.getInheritedObject());
        }
        return XenforoLoginCore.instance.langFile("errors.server");
    }

    public void onChat(AbstractPlayer player, String message) {
        StatusType status = XenforoLoginCore.instance.logged_in.get(player.getUniqueId());
        switch (status) {
            case NEED_CHECK:
                player.sendMessage(XenforoLoginCore.instance.langFile("need_check"));
                break;
            case NEED_LOGIN:
                ResultTypeUtils.handle(player,
                        ForumSystems.getCurrentSystem()
                                .login(player, message)
                                .shouldLogin(true));
                break;
            case NEED_REGISTER_EMAIL:
                if (isEmail(message)) {
                    logged_in.put(player.getUniqueId(), StatusType.NEED_REGISTER_PASSWORD.setEmail(message));
                    message(player);
                } else {
                    player.sendMessage(XenforoLoginCore.instance.langFile("errors.email"));
                }
                break;
            case NEED_REGISTER_PASSWORD:
                XenforoLoginCore.instance.logged_in.put(
                        player.getUniqueId(),
                        StatusType.NEED_REGISTER_CONFIRM.setEmail(status.email).setPassword(message));
                message(player);
                break;
            case NEED_REGISTER_CONFIRM:
                if (message.equals(status.password)) {
                    boolean result = ResultTypeUtils.handle(player,
                            ForumSystems.getCurrentSystem()
                                    .register(player, status.password, status.email)
                                    .shouldLogin(true));
                    if (result) {
                        XenforoLoginCore.instance.logged_in.put(
                                player.getUniqueId(), StatusType.LOGGED_IN);
                    } else {
                        XenforoLoginCore.instance.logged_in.put(
                                player.getUniqueId(), StatusType.NEED_REGISTER_EMAIL);
                        XenforoLoginCore.instance.message(player);
                    }
                } else {
                    player.sendMessage(XenforoLoginCore.instance.langFile("errors.confirm"));
                    XenforoLoginCore.instance.logged_in.put(
                            player.getUniqueId(), StatusType.NEED_REGISTER_PASSWORD);
                    XenforoLoginCore.instance.message(player);
                }
                break;
        }
    }

    public boolean isEmail(String email) {
        if (null == email || "".equals(email)) {
            return false;
        }
        Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,10}");
        Matcher m = p.matcher(email);
        return m.matches();
    }
}
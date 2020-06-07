/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.core;

import red.mohist.xenforologin.core.enums.ResultType;
import red.mohist.xenforologin.core.enums.StatusType;
import red.mohist.xenforologin.core.forums.ForumSystems;
import red.mohist.xenforologin.core.interfaces.PlatformAdapter;
import red.mohist.xenforologin.core.modules.AbstractPlayer;
import red.mohist.xenforologin.core.modules.LocationInfo;
import red.mohist.xenforologin.core.utils.Config;
import red.mohist.xenforologin.core.utils.Helper;
import red.mohist.xenforologin.core.utils.LoginTicker;
import red.mohist.xenforologin.core.utils.ResultTypeUtils;

import java.sql.*;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class XenforoLoginCore {

    public static XenforoLoginCore instance;
    public PlatformAdapter api;
    public ConcurrentMap<UUID, StatusType> logged_in;
    public LocationInfo default_location;
    private Connection connection;

    public XenforoLoginCore(PlatformAdapter platformAdapter) {
        // WARN You are not permitted to interfere any protection that prevents loading in CatServer
        String a0 = "WARN You are not permitted to interfere any protection that prevents loading in CatServer";
        try {
            if (Class.forName("catserver.server.CatServer") != null) {
                System.out.println("======================== [ Security Check ] ========================");
                System.out.println("万分抱歉，本插件未对CatServer作任何测试，尚不能保证能正常使用。");
                System.out.println("出于保护您的服务器安全的考虑，XenforoLogin已经停止载入。");
                System.out.println("如果你需要使用本插件，建议使用Mohist服务端，我们完成了良好的适配。");
                System.out.println("We are sorry that this plugin is not tested against CatServer at all");
                System.out.println("It can cause some major issues preventing the plugin to work.");
                System.out.println("Because of security issues, XenforoLogin aborted starting the server.");
                System.out.println("If you want to use it, switch to Mohist which is more stable.");
                System.out.println("==================================================================");
                new Thread(() -> System.exit(-1)).start();
                Thread.sleep(1000);
                new Thread(() -> Runtime.getRuntime().halt(-1)).start();
            }
            // WARN You are not permitted to interfere any protection that prevents loading in CatServer
            String a9 = "WARN You are not permitted to interfere any protection that prevents loading in CatServer";
        } catch (ClassNotFoundException ignored) {
            // WARN You are not permitted to interfere any protection that prevents loading in CatServer
            String a10 = "WARN You are not permitted to interfere any protection that prevents loading in CatServer";
        } catch (Exception e) {
            // WARN You are not permitted to interfere any protection that prevents loading in CatServer
            String a11 = "WARN You are not permitted to interfere any protection that prevents loading in CatServer";
            e.printStackTrace();
        }
        // WARN You are not permitted to interfere any protection that prevents loading in CatServer
        String a13 = "WARN You are not permitted to interfere any protection that prevents loading in CatServer";
        instance = this;
        api = platformAdapter;
        logged_in = new ConcurrentHashMap<>();
        loadConfig();

        ForumSystems.reloadConfig();
        LoginTicker.register();
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + XenforoLoginCore.instance.api.getConfigPath("Locations.db"));
            if (!connection.getMetaData().getTables(null, null, "locations", new String[]{"TABLE"}).next()) {
                PreparedStatement pps = connection.prepareStatement("CREATE TABLE locations (uuid NOT NULL,world,x,y,z,yaw,pitch,mode,PRIMARY KEY (uuid));");
                pps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void onDisable() {
        LoginTicker.unregister();
    }

    private void loadConfig() {
        LocationInfo spawn_location = api.getSpawn("world");
        default_location = new LocationInfo(
                Config.getString("spawn.world", "world"),
                Config.getDouble("spawn.x", spawn_location.x),
                Config.getDouble("spawn.y", spawn_location.y),
                Config.getDouble("spawn.z", spawn_location.z),
                Config.getFloat("spawn.yaw", spawn_location.yaw),
                Config.getFloat("spawn.pitch", spawn_location.pitch)
        );
    }

    public boolean needCancelled(AbstractPlayer player) {
        return !logged_in.getOrDefault(player.getUniqueId(), StatusType.NEED_LOGIN).equals(StatusType.LOGGED_IN);
    }

    public void login(AbstractPlayer player) {
        logged_in.put(player.getUniqueId(), StatusType.LOGGED_IN);
        try {
            PreparedStatement pps = connection.prepareStatement("SELECT * FROM locations WHERE uuid=? LIMIT 1;");
            pps.setString(1, player.getUniqueId().toString());
            ResultSet rs = pps.executeQuery();
            if (!rs.next()) {
                if (Config.getBoolean("teleport.tp_back_after_login", true)) {
                    LocationInfo spawn_location = api.getSpawn("world");
                    player.teleport(spawn_location);
                }
                player.setGamemode(Config.getInteger("secure.default_gamemode", 0));
            } else {
                if (Config.getBoolean("teleport.tp_back_after_login", true)) {
                    try {
                        player.teleport(new LocationInfo(
                                rs.getString("world"),
                                rs.getDouble("x"),
                                rs.getDouble("y"),
                                rs.getDouble("z"),
                                rs.getFloat("yaw"),
                                rs.getFloat("pitch")
                        ));
                    } catch (Exception e) {
                        LocationInfo spawn_location = api.getSpawn("world");
                        player.teleport(spawn_location);
                        api.getLogger().warning("Fail tp a player.Have you change the world?");
                    }
                }
                player.setGamemode(rs.getInt("mode"));
            }
        } catch (Throwable e) {
            player.setGamemode(Config.getInteger("secure.default_gamemode", 0));
            e.printStackTrace();
        }
        api.login(player);
        api.getLogger().info("Logging in " + player.getUniqueId());
        player.sendMessage(Helper.langFile("success"));
    }

    public void message(AbstractPlayer player) {
        switch (logged_in.get(player.getUniqueId())) {
            case NEED_LOGIN:
                player.sendMessage(Helper.langFile("need_login"));
                break;
            case NEED_REGISTER_EMAIL:
                player.sendMessage(Helper.langFile("register_email"));
                break;
            case NEED_REGISTER_PASSWORD:
                player.sendMessage(Helper.langFile("register_password"));
                break;
            case NEED_REGISTER_CONFIRM:
                player.sendMessage(Helper.langFile("register_password_confirm"));
                break;
        }
    }

    public void onQuit(AbstractPlayer player) {
        LocationInfo leave_location = player.getLocation();
        if (!needCancelled(player)) {
            try {
                PreparedStatement pps = connection.prepareStatement("DELETE FROM locations WHERE uuid = ?;");
                pps.setString(1, player.getUniqueId().toString());
                pps.executeUpdate();
                pps = connection.prepareStatement("INSERT INTO locations(uuid, world, x, y, z, yaw, pitch,mode) VALUES (?, ?, ?, ?, ?, ?, ?, ?);");
                pps.setString(1, player.getUniqueId().toString());
                pps.setString(2, leave_location.world);
                pps.setDouble(3, leave_location.x);
                pps.setDouble(4, leave_location.y);
                pps.setDouble(5, leave_location.z);
                pps.setFloat(6, leave_location.yaw);
                pps.setFloat(7, leave_location.pitch);
                pps.setInt(8, player.getGamemode());
                pps.executeUpdate();
            } catch (SQLException e) {
                api.getLogger().warning("Fail to save location.");
            }
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
                return Helper.langFile("errors.name_incorrect",
                        resultType.getInheritedObject());
            case NO_USER:
                if (Config.getBoolean("api.register", false)) {
                    XenforoLoginCore.instance.logged_in.put(player.getUniqueId(), StatusType.NEED_REGISTER_EMAIL);
                    return null;
                } else {
                    return Helper.langFile("errors.no_user");
                }
            case UNKNOWN:
                return Helper.langFile("errors.unknown", resultType.getInheritedObject());
        }
        return Helper.langFile("errors.server");
    }

    public void onChat(AbstractPlayer player, String message) {
        StatusType status = XenforoLoginCore.instance.logged_in.get(player.getUniqueId());
        switch (status) {
            case NEED_CHECK:
                player.sendMessage(Helper.langFile("need_check"));
                break;
            case NEED_LOGIN:
                XenforoLoginCore.instance.logged_in.put(
                        player.getUniqueId(), StatusType.HANDLE);
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
                    player.sendMessage(Helper.langFile("errors.email"));
                }
                break;
            case NEED_REGISTER_PASSWORD:
                XenforoLoginCore.instance.logged_in.put(
                        player.getUniqueId(),
                        StatusType.NEED_REGISTER_CONFIRM.setEmail(status.email).setPassword(message));
                message(player);
                break;
            case NEED_REGISTER_CONFIRM:
                XenforoLoginCore.instance.logged_in.put(
                        player.getUniqueId(), StatusType.HANDLE);
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
                    player.sendMessage(Helper.langFile("errors.confirm"));
                    XenforoLoginCore.instance.logged_in.put(
                            player.getUniqueId(), StatusType.NEED_REGISTER_PASSWORD);
                    XenforoLoginCore.instance.message(player);
                }
                break;
            case HANDLE:
                player.sendMessage(Helper.langFile("errors.handle"));
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
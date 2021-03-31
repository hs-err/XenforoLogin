/*
 * Copyright 2021 Mohist-Community
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package red.mohist.sodionauth.core.services;

import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;
import org.knownspace.minitask.ITask;
import org.knownspace.minitask.locks.UniqueFlag;
import org.knownspace.minitask.locks.Unlocker;
import red.mohist.sodionauth.core.SodionAuthCore;
import red.mohist.sodionauth.core.authbackends.AuthBackendSystems;
import red.mohist.sodionauth.core.enums.ResultType;
import red.mohist.sodionauth.core.enums.StatusType;
import red.mohist.sodionauth.core.events.BootEvent;
import red.mohist.sodionauth.core.events.player.CanJoinEvent;
import red.mohist.sodionauth.core.events.player.ChatEvent;
import red.mohist.sodionauth.core.events.player.JoinEvent;
import red.mohist.sodionauth.core.events.player.QuitEvent;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.modules.LocationInfo;
import red.mohist.sodionauth.core.modules.PlayerInfo;
import red.mohist.sodionauth.core.protection.SecuritySystems;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.Helper;
import red.mohist.sodionauth.core.utils.LoginTicker;
import red.mohist.sodionauth.core.utils.ResultTypeUtils;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthService {

    public LocationInfo default_location;
    public LocationInfo spawn_location;
    public ConcurrentMap<UUID, StatusType> logged_in;

    @Subscribe
    public void onBoot(BootEvent event) throws IOException {
        Helper.getLogger().info("Initializing auth service...");
        AuthBackendSystems.reloadConfig();
        spawn_location = SodionAuthCore.instance.api.getSpawn(SodionAuthCore.instance.api.getDefaultWorld());
        default_location = new LocationInfo(
                Config.spawn.getWorld(SodionAuthCore.instance.api.getDefaultWorld()),
                Config.spawn.getX(spawn_location.x),
                Config.spawn.getY(spawn_location.y),
                Config.spawn.getZ(spawn_location.z),
                Config.spawn.getYaw(spawn_location.yaw),
                Config.spawn.getPitch(spawn_location.pitch)
        );

        logged_in = new ConcurrentHashMap<>();
        Helper.getLogger().info("Check for existing players...");
        for (AbstractPlayer player : SodionAuthCore.instance.api.getAllPlayer()) {
            new CanJoinEvent(player).post();
            new JoinEvent(player).post();
        }
        LoginTicker.register();
    }

    @Subscribe
    public void onDown(QuitEvent event) {
        for (AbstractPlayer abstractPlayer : SodionAuthCore.instance.api.getAllPlayer()) {
            new QuitEvent(event.getPlayer()).post();
        }
    }

    @Subscribe
    public void onQuit(QuitEvent event) {
        AbstractPlayer player = event.getPlayer();
        LocationInfo leave_location = player.getLocation();
        Service.threadPool.dbUniqueFlag.lock().then(() -> {
            try (Unlocker<UniqueFlag> unlocker = new Unlocker<>(Service.threadPool.dbUniqueFlag)) {
                if (!needCancelled(player)) {
                    try {
                        PreparedStatement pps = Service.session
                                .prepareStatement("DELETE FROM last_info WHERE uuid = ?;");
                        pps.setString(1, player.getUniqueId().toString());
                        pps.executeUpdate();
                        pps = Service.session
                                .prepareStatement("INSERT INTO last_info(uuid, info) VALUES (?, ?);");
                        pps.setString(1, player.getUniqueId().toString());
                        pps.setString(2, new Gson().toJson(player.getPlayerInfo()));
                        pps.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Helper.getLogger().warn("Fail to save location.");
                    }
                }
                player.teleport(default_location);
                Service.auth.logged_in.remove(player.getUniqueId());
            } catch (Exception ignore) {
            }
        });
    }

    @Subscribe
    public void onChat(ChatEvent event) {
        AbstractPlayer player = event.getPlayer();
        String message = event.getMessage();
        StatusType status = Service.auth.logged_in.get(player.getUniqueId());
        switch (status) {
            case NEED_CHECK:
                player.sendMessage(player.getLang().getNeedLogin());
                break;
            case NEED_LOGIN:
                String canLogin = SecuritySystems.canLogin(player);
                if (canLogin != null) {
                    player.sendMessage(canLogin);
                    return;
                }
                Service.auth.logged_in.put(
                        player.getUniqueId(), StatusType.HANDLE);
                Service.threadPool.startup.startTask(() ->
                        AuthBackendSystems.getCurrentSystem().login(player, message).shouldLogin(true))
                        .then((result)->{
                            ResultTypeUtils.handle(player,result);
                        });
                break;
            case NEED_REGISTER_EMAIL:
                if (isEmail(message)) {
                    Service.auth.logged_in.put(player.getUniqueId(), StatusType.NEED_REGISTER_PASSWORD.setEmail(message));
                    sendTip(player);
                } else {
                    player.sendMessage(player.getLang().getErrors().getEmail());
                }
                break;
            case NEED_REGISTER_PASSWORD:
                Service.auth.logged_in.put(
                        player.getUniqueId(),
                        StatusType.NEED_REGISTER_CONFIRM.setEmail(status.email).setPassword(message));
                sendTip(player);
                break;
            case NEED_REGISTER_CONFIRM:
                String canRegister = SecuritySystems.canRegister(player);
                if (canRegister != null) {
                    player.sendMessage(canRegister);
                    return;
                }
                Service.auth.logged_in.put(
                        player.getUniqueId(), StatusType.HANDLE);
                if (message.equals(status.password)) {
                    registerAsync(player, status.email, status.password).then((result)->{
                        if(result){
                            Service.auth.logged_in.put(
                                    player.getUniqueId(), StatusType.LOGGED_IN);
                        }else{
                            Service.auth.logged_in.put(
                                    player.getUniqueId(), StatusType.NEED_REGISTER_EMAIL);
                            sendTip(player);
                        }
                    });
                } else {
                    player.sendMessage(player.getLang().getErrors().getConfirm());
                    Service.auth.logged_in.put(
                            player.getUniqueId(), StatusType.NEED_REGISTER_PASSWORD);
                    sendTip(player);
                }
                break;
            case HANDLE:
                player.sendMessage(player.getLang().getErrors().getHandle());
                break;
        }
    }

    public void sendTip(AbstractPlayer player) {
        switch (Service.auth.logged_in.get(player.getUniqueId())) {
            case NEED_LOGIN:
                player.sendMessage(player.getLang().getNeedLogin());
                break;
            case NEED_REGISTER_EMAIL:
                player.sendMessage(player.getLang().getRegisterEmail());
                break;
            case NEED_REGISTER_PASSWORD:
                player.sendMessage(player.getLang().getRegisterPassword());
                break;
            case NEED_REGISTER_CONFIRM:
                player.sendMessage(player.getLang().getRegisterPasswordConfirm());
                break;
        }
    }

    public boolean needCancelled(AbstractPlayer player) {
        return !logged_in.getOrDefault(player.getUniqueId(), StatusType.NEED_LOGIN).equals(StatusType.LOGGED_IN);
    }

    @Subscribe
    public void onCanJoin(CanJoinEvent event) throws ExecutionException, InterruptedException {
        ITask<Void> i = Service.threadPool.startup.startTask(() -> {
            AbstractPlayer player = event.getPlayer();
            logged_in.put(player.getUniqueId(), StatusType.HANDLE);
            if (Service.auth.logged_in.containsKey(player.getUniqueId())
                    && Service.auth.logged_in.get(player.getUniqueId()) != StatusType.HANDLE) {
                return null;
            }
            Service.auth.logged_in.put(player.getUniqueId(), StatusType.HANDLE);

            ResultType resultType = AuthBackendSystems.getCurrentSystem()
                    .join(player)
                    .shouldLogin(false);
            switch (resultType) {
                case OK:
                    Service.auth.logged_in.put(player.getUniqueId(), StatusType.NEED_LOGIN);
                    return null;
                case ERROR_NAME:
                    return player.getLang().getErrors().getNameIncorrect(
                            resultType.getInheritedObject());
                case NO_USER:
                    if (Config.api.getAllowRegister()) {
                        Service.auth.logged_in.put(player.getUniqueId(), StatusType.NEED_REGISTER_EMAIL);
                        return null;
                    } else {
                        return player.getLang().getErrors().getNoUser();
                    }
                case UNKNOWN:
                    return player.getLang().getErrors().getUnknown(resultType.getInheritedObject());
            }
            return player.getLang().getErrors().getUnknown();
        }).then(result -> {
            if (result != null) {
                event.setCancelled(result);
            }
        });
        if (!event.isAsynchronous()) {
            i.get();
        }
    }

    @Subscribe
    public void onJoin(JoinEvent event) {
        AbstractPlayer player = event.getPlayer();
        SodionAuthCore.instance.api.sendBlankInventoryPacket(player);
        if (Config.teleport.getTpSpawnBeforeLogin()) {
            player.teleport(default_location);
        }
        if (Config.security.getSpectatorLogin()) {
            player.setGameMode(3);
        }
        LoginTicker.add(player);
        Service.threadPool.dbUniqueFlag.lock().then(() -> {
            try (Unlocker<UniqueFlag> unlocker = new Unlocker<>(Service.threadPool.dbUniqueFlag)) {
                if (Config.session.getEnable()) {
                    PreparedStatement pps = Service.session.prepareStatement("SELECT * FROM sessions WHERE uuid=? AND ip=? AND time>? LIMIT 1;");
                    pps.setString(1, player.getUniqueId().toString());
                    pps.setString(2, player.getAddress().getHostAddress());
                    pps.setInt(3, (int) (System.currentTimeMillis() / 1000 - Config.session.getTimeout()));
                    ResultSet rs = pps.executeQuery();
                    if (rs.next()) {
                        player.sendMessage(player.getLang().getSession());
                        loginAsync(player);
                    }
                }
            } catch (Throwable e) {
                Helper.getLogger().warn("Fail use session.");
                e.printStackTrace();
            }
        });
    }

    public ITask<Void> loginAsync(AbstractPlayer player) {
        return Service.threadPool.dbUniqueFlag.lock().then(() -> {
            try {
                // check if already login
                if (Service.auth.logged_in.getOrDefault(player.getUniqueId(), StatusType.NEED_LOGIN)
                        .equals(StatusType.LOGGED_IN)) {
                    return;
                }
                Service.auth.logged_in.put(player.getUniqueId(), StatusType.LOGGED_IN);

                // restore playerInfo
                try {
                    PreparedStatement pps = Service.session.prepareStatement("SELECT * FROM last_info WHERE uuid=? LIMIT 1;");
                    pps.setString(1, player.getUniqueId().toString());
                    ResultSet rs = pps.executeQuery();
                    if (!rs.next()) {
                        player.setPlayerInfo(new PlayerInfo());
                    } else {
                        player.setPlayerInfo(new Gson().fromJson(rs.getString("info"), PlayerInfo.class));
                    }
                } catch (Throwable e) {
                    player.setGameMode(Config.security.getDefaultGamemode());
                    e.printStackTrace();
                }

                // remove playerInfo
                try {
                    PreparedStatement pps = Service.session.prepareStatement("DELETE FROM sessions WHERE uuid = ?;");
                    pps.setString(1, player.getUniqueId().toString());
                    pps.executeUpdate();

                    pps = Service.session.prepareStatement("INSERT INTO sessions(uuid, ip, time) VALUES (?, ?, ?);");
                    pps.setString(1, player.getUniqueId().toString());
                    pps.setString(2, player.getAddress().getHostAddress());
                    pps.setInt(3, (int) (System.currentTimeMillis() / 1000));
                    pps.executeUpdate();
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                SodionAuthCore.instance.api.onLogin(player);
                Helper.getLogger().info("Logging in " + player.getUniqueId());
                player.sendMessage(player.getLang().getSuccess());
            }catch (Throwable e){
                e.printStackTrace();
            }
            Service.threadPool.dbUniqueFlag.unlock();
        });
    }

    public ITask<Boolean> registerAsync(AbstractPlayer player, String email, String password) {
        return Service.threadPool.startup.startTask(() ->{
            return AuthBackendSystems.getCurrentSystem()
                    .register(player, password, email).shouldLogin(true);
        }).then((result)->{
            return ResultTypeUtils.handle(player,result);
        });
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

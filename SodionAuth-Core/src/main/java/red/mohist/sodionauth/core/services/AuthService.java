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

import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.Subscribe;
import org.knownspace.minitask.ITask;
import red.mohist.sodionauth.core.SodionAuthCore;
import red.mohist.sodionauth.core.database.entities.User;
import red.mohist.sodionauth.core.enums.PlayerStatus;
import red.mohist.sodionauth.core.events.BootEvent;
import red.mohist.sodionauth.core.events.DownEvent;
import red.mohist.sodionauth.core.events.player.*;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.modules.LocationInfo;
import red.mohist.sodionauth.core.protection.SecuritySystems;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.Helper;
import red.mohist.sodionauth.core.utils.LoginTicker;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

public class AuthService {

    public LocationInfo default_location;
    public ConcurrentMap<UUID, PlayerStatus> logged_in;

    @Subscribe
    public void onBoot(BootEvent event) throws IOException {
        Helper.getLogger().info("Initializing auth service...");

        LocationInfo spawn_location = SodionAuthCore.instance.api.getSpawn(SodionAuthCore.instance.api.getDefaultWorld());
        default_location = new LocationInfo(
                Config.spawn.world != null ? Config.spawn.world : SodionAuthCore.instance.api.getDefaultWorld(),
                Config.spawn.x != null ? Config.spawn.x : spawn_location.x,
                Config.spawn.y != null ? Config.spawn.y : spawn_location.y,
                Config.spawn.z != null ? Config.spawn.z : spawn_location.z,
                Config.spawn.yaw != null ? Config.spawn.yaw : spawn_location.yaw,
                Config.spawn.pitch != null ? Config.spawn.pitch : spawn_location.pitch
        );

        logged_in = new ConcurrentHashMap<>();
        Helper.getLogger().info("Check for existing players...");
        for (AbstractPlayer player : SodionAuthCore.instance.api.getAllPlayer()) {
            new CanJoinEvent(player).post();
            new JoinEvent(player).post();
        }
        Service.eventBus.register(new LoginTicker());
    }

    @Subscribe
    public void onDown(DownEvent event) {
        for (AbstractPlayer abstractPlayer : SodionAuthCore.instance.api.getAllPlayer()) {
            new QuitEvent(abstractPlayer).post();
        }
    }

    @Subscribe
    public void onChat(PlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }
        AbstractPlayer player = event.getPlayer();
        String message = event.getMessage();
        switch (player.getStatus().type) {
            case NEED_CHECK:
                event.setCancelled(true);
                player.sendMessage(player.getLang().needLogin);
                break;
            case NEED_LOGIN:
                event.setCancelled(true);
                String canLogin = SecuritySystems.canLogin(player);
                if (canLogin != null) {
                    player.sendMessage(canLogin);
                    return;
                }
                User user = User.getByName(player.getName());

                if (user == null) {
                    //if (Config.api.allowRegister) {
                    Service.auth.logged_in.put(player.getUniqueId(), PlayerStatus.NEED_REGISTER_EMAIL());
                    //} else {
                    //    player.kick(player.getLang().errors.noUser);
                    //}
                } else if (!user.getName().equals(player.getName())) {
                    player.kick(player.getLang().errors.getNameIncorrect(
                            ImmutableMap.of("correct", user.getName())));
                } else if (user.verifyPassword(message)) {
                    Service.auth.login(player);
                } else {
                    player.kick(player.getLang().errors.password);
                }
                break;
            case HANDLE:
                event.setCancelled(true);
                player.sendMessage(player.getLang().errors.handle);
                break;
        }
    }

    public void sendTip(AbstractPlayer player) {
        switch (Service.auth.logged_in.get(player.getUniqueId()).type) {
            case NEED_LOGIN:
                player.sendMessage(player.getLang().needLogin);
                break;
            case NEED_REGISTER_EMAIL:
                player.sendMessage(player.getLang().registerEmail);
                break;
            case NEED_REGISTER_PASSWORD:
                player.sendMessage(player.getLang().registerPassword);
                break;
            case NEED_REGISTER_CONFIRM:
                player.sendMessage(player.getLang().registerPasswordConfirm);
                break;
        }
    }

    public boolean needCancelled(AbstractPlayer player) {
        return !logged_in.getOrDefault(player.getUniqueId(), PlayerStatus.NEED_LOGIN()).type.equals(PlayerStatus.StatusType.LOGGED_IN);
    }

    @Subscribe
    public void onCanJoin(CanJoinEvent event) throws ExecutionException, InterruptedException {
        ITask<Void> i = Service.threadPool.startup.startTask(() -> {
            try {
                AbstractPlayer player = event.getPlayer();
                logged_in.put(player.getUniqueId(), PlayerStatus.HANDLE());
                if (Service.auth.logged_in.containsKey(player.getUniqueId())
                        && Service.auth.logged_in.get(player.getUniqueId()).type != PlayerStatus.StatusType.HANDLE) {
                    return null;
                }
                Service.auth.logged_in.put(player.getUniqueId(), PlayerStatus.HANDLE());

                User user = User.getByName(player.getName());

                if (user == null) {
                    //if (Config.api.allowRegister) {
                    Service.auth.logged_in.put(player.getUniqueId(), PlayerStatus.NEED_REGISTER_EMAIL());
                    return null;
                    //} else {
                    //    return player.getLang().errors.noUser;
                    //}
                } else if (!user.getName().equals(player.getName())) {
                    return player.getLang().errors.getNameIncorrect(
                            ImmutableMap.of("correct", user.getName()));
                } else {
                    Service.auth.logged_in.put(player.getUniqueId(), PlayerStatus.NEED_LOGIN());
                    return null;
                }
            } catch (Exception e) {
                Helper.getLogger().warn("Exception during check player " + event.getPlayer().getName(), e);
                return event.getPlayer().getLang().errors.server;
            }
        }).then((result) -> {
            if (result != null) {
                event.setCancelled(true);
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
        if (Config.teleport.tpSpawnBeforeLogin) {
            player.teleport(default_location);
        }
        if (Config.security.spectatorLogin) {
            player.setGameMode(3);
        }
        LoginTicker.add(player);
        /*
        Service.threadPool.dbUniqueFlag.lock().then(() -> {
            try (Unlocker<UniqueFlag> unlocker = new Unlocker<>(Service.threadPool.dbUniqueFlag)) {
                if (Config.session.enable) {
                    PreparedStatement pps = Service.session.prepareStatement("SELECT * FROM sessions WHERE uuid=? AND ip=? AND time>? LIMIT 1;");
                    pps.setString(1, player.getUniqueId().toString());
                    pps.setString(2, player.getAddress().getHostAddress());
                    pps.setInt(3, (int) (System.currentTimeMillis() / 1000 - Config.session.timeout));
                    ResultSet rs = pps.executeQuery();
                    if (rs.next()) {
                        player.sendMessage(player.getLang().session);
                        login(player);
                    }
                }
            } catch (Exception e) {
                Helper.getLogger().warn("Fail use session.");
                e.printStackTrace();
            }
        });
        */
    }

    public void login(AbstractPlayer player) {
        // check if already login
        if (Service.auth.logged_in.getOrDefault(player.getUniqueId(), PlayerStatus.NEED_LOGIN()).type
                .equals(PlayerStatus.StatusType.LOGGED_IN)) {
            return;
        }
        Service.auth.logged_in.put(player.getUniqueId(), PlayerStatus.LOGGED_IN());
        new LoginEvent(player).post();
    }
}

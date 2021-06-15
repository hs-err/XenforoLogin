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
import com.google.gson.Gson;
import org.hibernate.Session;
import org.knownspace.minitask.ITask;
import red.mohist.sodionauth.core.SodionAuthCore;
import red.mohist.sodionauth.core.authbackends.AuthBackend;
import red.mohist.sodionauth.core.authbackends.AuthBackends;
import red.mohist.sodionauth.core.entities.AuthLastInfo;
import red.mohist.sodionauth.core.entities.User;
import red.mohist.sodionauth.core.enums.PlayerStatus;
import red.mohist.sodionauth.core.events.BootEvent;
import red.mohist.sodionauth.core.events.DownEvent;
import red.mohist.sodionauth.core.events.player.*;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.modules.LocationInfo;
import red.mohist.sodionauth.core.modules.PlayerInfo;
import red.mohist.sodionauth.core.protection.SecuritySystems;
import red.mohist.sodionauth.core.repositories.AuthLastInfoRepository;
import red.mohist.sodionauth.core.repositories.UserRepository;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.Helper;
import red.mohist.sodionauth.core.utils.LoginTicker;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

public class AuthService {

    public LocationInfo default_location;
    public ConcurrentMap<UUID, PlayerStatus> logged_in;
    public Set<String> skipLoginByName = new HashSet<>();
    public Set<UUID> skipLoginByUUID = new HashSet<>();

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

        Pattern uuidPattern = Pattern.compile("[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}");
        for (String s : Config.security.bypassCheck) {
            if (uuidPattern.matcher(s).matches()) {
                skipLoginByUUID.add(UUID.fromString(s));
            }
            skipLoginByName.add(s);
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
                Service.auth.logged_in.put(player.getUniqueId(), PlayerStatus.HANDLE());
                Service.threadPool.startup.startTask(() -> {
                    try (Session session = Service.database.sessionFactory.openSession()) {
                        session.beginTransaction();

                        User user = UserRepository.getByName(session, player.getName());

                        if (user == null) {
                            //if (Config.api.allowRegister) {
                            Service.auth.logged_in.put(player.getUniqueId(), PlayerStatus.NEED_REGISTER_EMAIL());
                            //} else {
                            //    player.kick(player.getLang().errors.noUser);
                            //}
                        } else if (!user.getName().equals(player.getName())) {
                            player.kick(player.getLang().errors.getNameIncorrect(
                                    ImmutableMap.of("correct", user.getName())));
                        } else if (user.verifyPassword(session, message)) {
                            Service.auth.login(player);
                        } else {
                            player.kick(player.getLang().errors.password);
                        }

                        session.getTransaction().commit();
                    } catch (Exception e) {
                        Service.auth.logged_in.put(player.getUniqueId(), PlayerStatus.NEED_LOGIN());
                        player.sendMessage(player.getLang().errors.server);
                        Helper.getLogger().warn("Exception during attempt login for player " + player.getName(), e);
                    }
                });
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
        PlayerStatus status = logged_in.get(player.getUniqueId());
        if (status == null) {
            return !skipLoginByUUID.contains(player.getUniqueId())
                    && !skipLoginByName.contains(player.getName());
        }
        return !status.type.equals(PlayerStatus.StatusType.LOGGED_IN);
    }

    @Subscribe
    public void onCanJoin(CanJoinEvent event) throws ExecutionException, InterruptedException {
        ITask<Void> i = Service.threadPool.startup.startTask(() -> {
            try (Session session = Service.database.sessionFactory.openSession()) {
                session.beginTransaction();

                AbstractPlayer player = event.getPlayer();
                logged_in.put(player.getUniqueId(), PlayerStatus.HANDLE());
                if (Service.auth.logged_in.containsKey(player.getUniqueId())
                        && Service.auth.logged_in.get(player.getUniqueId()).type != PlayerStatus.StatusType.HANDLE) {
                    return null;
                }
                Service.auth.logged_in.put(player.getUniqueId(), PlayerStatus.HANDLE());

                User user = UserRepository.getByName(session, player.getName());

                String result;
                if (user == null) {
                    AtomicReference<String> willReturn = new AtomicReference<>(player.getLang().errors.noUser);
                    AtomicBoolean findFirst = new AtomicBoolean(false);
                    AuthBackends.authBackendMap.forEach((typeName, authBackend) -> {
                        if (findFirst.get()) {
                            return;
                        }
                        User fakeUser = new User().setName(event.getPlayer().getName());
                        if (authBackend.allowLogin) {
                            AuthBackend.GetResult authBackendResult = authBackend.get(fakeUser);
                            if (authBackendResult.type.equals(AuthBackend.GetResultType.SUCCESS)) {
                                willReturn.set(null);
                                fakeUser = new User().setName(authBackendResult.name)
                                        .setEmail(authBackendResult.email);
                                session.save(fakeUser);
                                session.save(
                                        fakeUser.createAuthInfo().setType(typeName));
                                if (!authBackendResult.name.equals(player.getName())) {
                                    willReturn.set(player.getLang().errors.getNameIncorrect(ImmutableMap.of("correct", authBackendResult.name)));
                                } else {
                                    Service.auth.logged_in.put(player.getUniqueId(), PlayerStatus.NEED_LOGIN());
                                }
                                findFirst.set(true);
                            }
                        }
                    });

                    if (!findFirst.get() && !Config.database.passwordHash.equals("")) {
                        Service.auth.logged_in.put(player.getUniqueId(), PlayerStatus.NEED_REGISTER_EMAIL());
                        result = null;
                    } else {
                        result = willReturn.get();
                    }
                } else if (!user.getName().equals(player.getName())) {
                    result = player.getLang().errors.getNameIncorrect(
                            ImmutableMap.of("correct", user.getName()));
                } else {
                    Service.auth.logged_in.put(player.getUniqueId(), PlayerStatus.NEED_LOGIN());
                    result = null;
                }

                session.getTransaction().commit();
                return result;
            } catch (Exception e) {
                Helper.getLogger().warn("Exception during check player " + event.getPlayer().getName(), e);
                return event.getPlayer().getLang().errors.server;
            }
        }).then((result) -> {
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
        PlayerInfo playerInfo = player.getPlayerInfo();
        Service.threadPool.startup.startTask(() -> {
            try (Session session = Service.database.sessionFactory.openSession()) {
                session.beginTransaction();

                if (AuthLastInfoRepository.get(session, player.getUniqueId()) == null) {
                    Helper.getLogger().info("Can't find " + player.getName() + "'s info. Create one");
                    session.save(new AuthLastInfo()
                            .setUuid(player.getUniqueId())
                            .setInfo(
                                    session.getLobHelper().createBlob(
                                            new Gson().toJson(playerInfo).getBytes(StandardCharsets.UTF_8))));
                }


                session.getTransaction().commit();
            } catch (Exception e) {
                Helper.getLogger().warn("Exception during get player " + event.getPlayer().getName() + " lastInfo.", e);
            }
        });
        SodionAuthCore.instance.api.sendBlankInventoryPacket(player);
        if (Config.teleport.tpSpawnBeforeLogin) {
            player.teleport(default_location);
        }
        if (Config.security.spectatorLogin) {
            player.setGameMode(3);
        }
        LoginTicker.add(player);
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

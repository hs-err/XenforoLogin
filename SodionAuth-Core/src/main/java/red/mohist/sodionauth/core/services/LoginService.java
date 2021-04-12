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
import org.knownspace.minitask.ITask;
import org.knownspace.minitask.locks.UniqueFlag;
import org.knownspace.minitask.locks.Unlocker;
import red.mohist.sodionauth.core.SodionAuthCore;
import red.mohist.sodionauth.core.database.entities.User;
import red.mohist.sodionauth.core.enums.StatusType;
import red.mohist.sodionauth.core.events.BootEvent;
import red.mohist.sodionauth.core.events.player.*;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.modules.LocationInfo;
import red.mohist.sodionauth.core.modules.PlayerInfo;
import red.mohist.sodionauth.core.protection.SecuritySystems;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.Helper;
import red.mohist.sodionauth.core.utils.LoginTicker;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

public class LoginService {

    public LocationInfo spawn_location;

    public LoginService(){
        spawn_location = SodionAuthCore.instance.api.getSpawn(SodionAuthCore.instance.api.getDefaultWorld());
    }

    @Subscribe
    public void onQuit(QuitEvent event) {
        AbstractPlayer player = event.getPlayer();
        LocationInfo leave_location = player.getLocation();
        Service.threadPool.dbUniqueFlag.lock().then(() -> {
            try (Unlocker<UniqueFlag> unlocker = new Unlocker<>(Service.threadPool.dbUniqueFlag)) {
                if (!Service.auth.needCancelled(player)) {
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
                player.teleport(Service.auth.default_location);
                Service.auth.logged_in.remove(player.getUniqueId());
            } catch (Exception ignore) {
            }
        });
    }

    @Subscribe
    public void loginAsync(LoginEvent event) {
        Service.threadPool.startup.startTask(()->{
            AbstractPlayer player = event.getPlayer();
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
                player.setGameMode(Config.security.defaultGamemode);
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
            player.sendMessage(player.getLang().loginSuccess);
        });
    }
}

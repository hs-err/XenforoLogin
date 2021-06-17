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
import org.hibernate.Session;
import red.mohist.sodionauth.core.database.entities.AuthLastInfo;
import red.mohist.sodionauth.core.database.entities.AuthSession;
import red.mohist.sodionauth.core.events.BootEvent;
import red.mohist.sodionauth.core.events.player.JoinEvent;
import red.mohist.sodionauth.core.events.player.QuitEvent;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.database.repositories.AuthLastInfoRepository;
import red.mohist.sodionauth.core.database.repositories.AuthSessionRepository;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.Helper;

import java.nio.charset.StandardCharsets;

public class SessionService {
    @Subscribe
    public void onBoot(BootEvent event) {
        Helper.getLogger().info("Initializing session service...");
    }

    @Subscribe
    public void onQuit(QuitEvent event) {
        AbstractPlayer player = event.getPlayer();
        Service.threadPool.startup.startTask(() -> {
            try (Session session = Service.database.sessionFactory.openSession()) {
                session.beginTransaction();

                if (!Service.auth.needCancelled(player)) {
                    AuthLastInfo authLastInfo = AuthLastInfoRepository.get(session, player.getUniqueId());
                    if (authLastInfo == null) {
                        authLastInfo = new AuthLastInfo();
                    }
                    session.save(authLastInfo.setUuid(event.getPlayer().getUniqueId())
                            .setInfo(
                                    session.getLobHelper().createBlob(
                                            new Gson().toJson(player.getPlayerInfo()).getBytes(StandardCharsets.UTF_8))
                            ));
                    if (Config.session.enable && player.getAddress() != null) {
                        session.save(new AuthSession().setUuid(player.getUniqueId())
                                .setIp(player.getAddress().getHostAddress())
                                .setTime(Long.toString(System.currentTimeMillis() / 1000)));
                    }
                }
                player.teleport(Service.auth.default_location);
                Service.auth.logged_in.remove(player.getUniqueId());

                session.getTransaction().commit();
            } catch (Exception e) {
                Helper.getLogger().warn("Exception during save player " + event.getPlayer().getName() + " lastInfo.", e);
            }
        });
    }

    @Subscribe
    public void onJoin(JoinEvent event) {
        try (Session session = Service.database.sessionFactory.openSession()) {
            session.beginTransaction();

            AbstractPlayer player = event.getPlayer();
            if (Config.session.enable) {
                AuthSession authSession = AuthSessionRepository.get(session, player.getUniqueId());
                if (authSession != null) {
                    if (Long.parseLong(authSession.getTime()) > (System.currentTimeMillis() / 1000 - Config.session.timeout)
                            && authSession.getIp().equals(player.getAddress().getHostAddress())) {
                        player.sendMessage(player.getLang().session);
                        Service.auth.login(player);
                    }
                }
            }

            session.getTransaction().commit();
        }
    }
}

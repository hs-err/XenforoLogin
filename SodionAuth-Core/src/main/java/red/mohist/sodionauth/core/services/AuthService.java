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
import red.mohist.sodionauth.core.SodionAuthCore;
import red.mohist.sodionauth.core.authbackends.AuthBackendSystems;
import red.mohist.sodionauth.core.enums.StatusType;
import red.mohist.sodionauth.core.events.BootEvent;
import red.mohist.sodionauth.core.events.player.ChatEvent;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.protection.SecuritySystems;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.ResultTypeUtils;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthService {
    public ConcurrentMap<UUID, StatusType> logged_in;
    @Subscribe
    public void onBoot(BootEvent event) throws IOException {
        AuthBackendSystems.reloadConfig();
        logged_in = new ConcurrentHashMap<>();
    }
    @Subscribe
    public void onChat(ChatEvent event){
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
                Service.async.executor.execute(() -> ResultTypeUtils.handle(player,
                        AuthBackendSystems.getCurrentSystem().login(player, message).shouldLogin(true)));
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
                    Service.async.executor.execute(() -> {
                        if (register(player, status.email, status.password)) {
                            Service.auth.logged_in.put(
                                    player.getUniqueId(), StatusType.LOGGED_IN);
                        } else {
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
    public boolean needCancelled(AbstractPlayer player){
        return !logged_in.getOrDefault(player.getUniqueId(), StatusType.NEED_LOGIN).equals(StatusType.LOGGED_IN);
    }
    public boolean register(AbstractPlayer player, String email, String password) {
        return ResultTypeUtils.handle(player,
                AuthBackendSystems.getCurrentSystem()
                        .register(player, password, email).shouldLogin(true));
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

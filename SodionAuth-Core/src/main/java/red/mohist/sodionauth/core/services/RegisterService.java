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
import org.knownspace.minitask.ITask;
import red.mohist.sodionauth.core.database.entities.AuthInfo;
import red.mohist.sodionauth.core.database.entities.User;
import red.mohist.sodionauth.core.enums.StatusType;
import red.mohist.sodionauth.core.events.player.PlayerChatEvent;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.protection.SecuritySystems;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterService {

    @Subscribe
    public void onChat(PlayerChatEvent event) {
        if(event.isCancelled()){
            return;
        }
        AbstractPlayer player = event.getPlayer();
        String message = event.getMessage();
        StatusType status = player.getStatus();
        switch (status) {
            case NEED_REGISTER_EMAIL:
                event.setCancelled(true);
                if (isEmail(message)) {
                    Service.auth.logged_in.put(player.getUniqueId(), StatusType.NEED_REGISTER_PASSWORD.setEmail(message));
                    Service.auth.sendTip(player);
                } else {
                    player.sendMessage(player.getLang().errors.email);
                }
                break;
            case NEED_REGISTER_PASSWORD:
                event.setCancelled(true);
                Service.auth.logged_in.put(
                        player.getUniqueId(),
                        StatusType.NEED_REGISTER_CONFIRM.setEmail(status.email).setPassword(message));
                Service.auth.sendTip(player);
                break;
            case NEED_REGISTER_CONFIRM:
                event.setCancelled(true);
                String canRegister = SecuritySystems.canRegister(player);
                if (canRegister != null) {
                    player.sendMessage(canRegister);
                    return;
                }
                Service.auth.logged_in.put(
                        player.getUniqueId(), StatusType.HANDLE);
                if (message.equals(status.password)) {
                    registerAsync(player, status.email, status.password).then((result) -> {
                        if (result) {
                            Service.auth.logged_in.put(
                                    player.getUniqueId(), StatusType.LOGGED_IN);
                        } else {
                            Service.auth.logged_in.put(
                                    player.getUniqueId(), StatusType.NEED_REGISTER_EMAIL);
                            Service.auth.sendTip(player);
                        }
                    });
                } else {
                    player.sendMessage(player.getLang().errors.confirm);
                    Service.auth.logged_in.put(
                            player.getUniqueId(), StatusType.NEED_REGISTER_PASSWORD);
                    Service.auth.sendTip(player);
                }
                break;
        }
    }

    public boolean verifyPassword(User user,String password){
        for (AuthInfo authInfo : user.getAuthInfo()) {
            if(password.equals(authInfo.getData())){
                return true;
            }
        }
        return false;
    }
    public RegisterResult register(String username,String email,String password){
        User user = User.getByName(username);
        if(user != null){
            return RegisterResult.USERNAME_EXIST;
        }

        user = new User().setEmail(email.toLowerCase()).first();
        if(user != null){
            return RegisterResult.EMAIL_EXIST;
        }

        user = new User().setName(username).setEmail(email);
        user.save();
        user.createAuthInfo()
                .setType("password:plain")
                .setData(password)
                .save();
        return RegisterResult.OK;
    }

    public ITask<Boolean> registerAsync(AbstractPlayer player, String email, String password) {
        return Service.threadPool.startup.startTask(
                () ->Service.register.register(player.getName(),email,password)
        ).then((result)->{
            switch (result){
                case OK:
                    Service.auth.login(player);
                    return true;
                case USERNAME_EXIST:
                    player.kick(player.getLang().errors.usernameExist);
                    return false;
                case EMAIL_EXIST:
                    player.kick(player.getLang().errors.email);
                    return false;
                default:
                    return false;
            }
        });
    }

    public boolean registerSync(AbstractPlayer player, String email, String password){
        switch (Service.register.register(player.getName(),email,password)){
            case OK:
                Service.auth.login(player);
                return true;
            case USERNAME_EXIST:
                player.kick(player.getLang().errors.usernameExist);
                return false;
            case EMAIL_EXIST:
                player.kick(player.getLang().errors.email);
                return false;
            default:
                return false;
        }
    }

    public enum RegisterResult {
        OK,
        USERNAME_EXIST,
        EMAIL_EXIST
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

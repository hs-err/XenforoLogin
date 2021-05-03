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
import red.mohist.sodionauth.core.authbackends.AuthBackend;
import red.mohist.sodionauth.core.authbackends.AuthBackends;
import red.mohist.sodionauth.core.database.entities.AuthInfo;
import red.mohist.sodionauth.core.database.entities.User;
import red.mohist.sodionauth.core.enums.PlayerStatus;
import red.mohist.sodionauth.core.events.player.PlayerChatEvent;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.protection.SecuritySystems;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.Helper;
import red.mohist.sodionauth.core.utils.Lang;
import red.mohist.sodionauth.core.utils.hasher.HasherTools;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterService {

    @Subscribe
    public void onChat(PlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }
        AbstractPlayer player = event.getPlayer();
        String message = event.getMessage();
        PlayerStatus status = player.getStatus();
        switch (status.type) {
            case NEED_REGISTER_EMAIL:
                event.setCancelled(true);
                if (isEmail(message)) {
                    Service.auth.logged_in.put(player.getUniqueId(), PlayerStatus.NEED_REGISTER_PASSWORD().setEmail(message));
                    Service.auth.sendTip(player);
                } else {
                    player.sendMessage(player.getLang().errors.email);
                }
                break;
            case NEED_REGISTER_PASSWORD:
                event.setCancelled(true);
                Service.auth.logged_in.put(
                        player.getUniqueId(),
                        PlayerStatus.HANDLE());
                Service.passwordStrength.verifyAsync(player, status.email, message).then((result) -> {
                    try {
                        if (result == null) {
                            Service.auth.logged_in.put(player.getUniqueId(), PlayerStatus.NEED_REGISTER_PASSWORD().setEmail(message));
                            Service.auth.sendTip(player);
                            return;
                        }
                        player.sendMessage(player.getLang().getPasswordStrength(
                                ImmutableMap.of("strength",
                                        String.valueOf(result.getEntropy().intValue())
                                )));
                        if (result.isMinimumEntropyMet()) {
                            Service.auth.logged_in.put(
                                    player.getUniqueId(),
                                    PlayerStatus.NEED_REGISTER_CONFIRM().setEmail(status.email).setPassword(message));
                        } else {
                            Service.auth.logged_in.put(player.getUniqueId(), PlayerStatus.NEED_REGISTER_PASSWORD().setEmail(status.email));
                            Service.passwordStrength.sendTip(player, result);
                        }
                        Service.auth.sendTip(player);
                    }catch (Exception e){
                        Helper.getLogger().warn("Can't check password for "+player.getName(),e);
                        Service.auth.logged_in.put(player.getUniqueId(), PlayerStatus.NEED_REGISTER_PASSWORD().setEmail(status.email));
                        player.sendMessage(player.getLang().errors.server);
                        Service.auth.sendTip(player);
                    }
                });
                break;
            case NEED_REGISTER_CONFIRM:
                event.setCancelled(true);
                String canRegister = SecuritySystems.canRegister(player);
                if (canRegister != null) {
                    player.sendMessage(canRegister);
                    return;
                }
                Service.auth.logged_in.put(
                        player.getUniqueId(), PlayerStatus.HANDLE());
                if (message.equals(status.password)) {
                    registerAsync(player, status.email, status.password).then((result) -> {
                        if (result) {
                            Service.auth.logged_in.put(
                                    player.getUniqueId(), PlayerStatus.LOGGED_IN());
                        } else {
                            Service.auth.logged_in.put(
                                    player.getUniqueId(), PlayerStatus.NEED_REGISTER_EMAIL());
                            Service.auth.sendTip(player);
                        }
                    });
                } else {
                    player.sendMessage(player.getLang().errors.confirm);
                    Service.auth.logged_in.put(
                            player.getUniqueId(), PlayerStatus.NEED_REGISTER_PASSWORD());
                    Service.auth.sendTip(player);
                }
                break;
        }
    }

    public boolean verifyPassword(User user, String password) {
        for (AuthInfo authInfo : user.getAuthInfo()) {
            if (password.equals(authInfo.getData())) {
                return true;
            }
        }
        return false;
    }

    public RegisterResult register(String username, String email, String password) {
        User user = User.getByName(username);
        if (user != null) {
            return RegisterResult.USERNAME_EXIST();
        }

        user = new User().setEmail(email.toLowerCase()).first();
        if (user != null) {
            return RegisterResult.EMAIL_EXIST();
        }

        user = new User().setName(username).setEmail(email);
        user.save();

        RegisterResult result = RegisterResult.OK();

        if (Config.database.passwordHash != null) {
            user.createAuthInfo()
                    .setType("password:" + Config.database.passwordHash)
                    .setData(HasherTools.getByName(Config.database.passwordHash).hash(password))
                    .save();
            result.friendlyName = Lang.def.thisServer;
        }

        User finalUser = user;
        AuthBackends.authBackendMap.forEach((typeName, authBackend) -> {
            if (authBackend.allowRegister) {
                AuthBackend.GetResult getResult;
                switch (authBackend.register(finalUser, password)) {
                    case SUCCESS:
                        finalUser.createAuthInfo()
                                .setType(typeName)
                                .save();
                        result.setChild(RegisterResult.OK().setFriendlyName(authBackend.friendlyName));
                        break;
                    case EMAIL_EXIST:
                        getResult = authBackend.get(finalUser);
                        if (getResult.type.equals(AuthBackend.GetResultType.SUCCESS)) {
                            if (authBackend.login(finalUser, finalUser.createAuthInfo()
                                    .setType(typeName), password).type.equals(AuthBackend.LoginResultType.SUCCESS)) {
                                result.setChild(RegisterResult.OK().setFriendlyName(authBackend.friendlyName));
                                break;
                            }
                        }
                        result.setChild(RegisterResult.EMAIL_EXIST().setFriendlyName(authBackend.friendlyName));
                        break;
                    case NAME_EXIST:
                        getResult = authBackend.get(finalUser);
                        if (getResult.type.equals(AuthBackend.GetResultType.SUCCESS)) {
                            if (authBackend.login(finalUser, finalUser.createAuthInfo()
                                    .setType(typeName), password).type.equals(AuthBackend.LoginResultType.SUCCESS)) {
                                result.setChild(RegisterResult.OK().setFriendlyName(authBackend.friendlyName));
                                break;
                            }
                        }
                        result.setChild(RegisterResult.USERNAME_EXIST().setFriendlyName(authBackend.friendlyName));
                        break;
                    default:
                        result.setChild(RegisterResult.FAILED().setFriendlyName(authBackend.friendlyName));
                }
            }
        });
        return result;
    }

    public ITask<Boolean> registerAsync(AbstractPlayer player, String email, String password) {
        return Service.threadPool.startup.startTask(() -> {
            try {
                return Service.register.register(player.getName(), email, password);
            } catch (Exception e) {
                Helper.getLogger().warn("Exception during register for player " + player.getName(), e);
                return RegisterResult.FAILED();
            }
        }).then((result) -> {
            try {
                return handleResult(player, result);
            } catch (Exception e) {
                Helper.getLogger().warn("Exception during register for player " + player.getName(), e);
                player.sendMessage(player.getLang().errors.server);
                return false;
            }
        });
    }

    public boolean registerSync(AbstractPlayer player, String email, String password) {
        RegisterResult result = Service.register.register(player.getName(), email, password);
        return handleResult(player, result);
    }

    private boolean handleResult(AbstractPlayer player, RegisterResult result) {
        switch (result.type) {
            case OK:
                if (result.friendlyName != null) {
                    player.sendMessage(player.getLang().getRegisterSuccess(ImmutableMap.of("name",result.friendlyName)));
                }
                for (RegisterResult childResult : result.child) {
                    if (childResult.friendlyName != null) {
                        switch (childResult.type) {
                            case OK:
                                player.sendMessage(player.getLang().getRegisterSuccess(ImmutableMap.of("name",childResult.friendlyName)));
                                break;
                            case USERNAME_EXIST:
                                player.sendMessage(player.getLang().errors.getRegisterFailed(
                                        ImmutableMap.of("name",childResult.friendlyName,
                                                "reason","USERNAME_EXIST")));
                                break;
                            case EMAIL_EXIST:
                                player.sendMessage(player.getLang().errors.getRegisterFailed(
                                        ImmutableMap.of("name",childResult.friendlyName,
                                                "reason","EMAIL_EXIST")));
                                break;
                            default:
                                player.sendMessage(player.getLang().errors.getRegisterFailed(
                                        ImmutableMap.of("name",childResult.friendlyName,
                                                "reason","UNKNOWN")));
                        }
                    }
                }
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

    public boolean isEmail(String email) {
        if (null == email || "".equals(email)) {
            return false;
        }
        Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,10}");
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public enum ResultType {
        OK,
        USERNAME_EXIST,
        EMAIL_EXIST,
        FAILED;
    }

    public static class RegisterResult {
        public ResultType type;
        public String friendlyName;
        public ArrayList<RegisterResult> child = new ArrayList<>();

        public RegisterResult(ResultType type) {
            this.type = type;
        }

        public static RegisterResult OK() {
            return new RegisterResult(ResultType.OK);
        }

        public static RegisterResult USERNAME_EXIST() {
            return new RegisterResult(ResultType.USERNAME_EXIST);
        }

        public static RegisterResult EMAIL_EXIST() {
            return new RegisterResult(ResultType.EMAIL_EXIST);
        }

        public static RegisterResult FAILED() {
            return new RegisterResult(ResultType.FAILED);
        }

        public RegisterResult setChild(RegisterResult child) {
            this.child.add(child);
            return this;
        }

        public RegisterResult setFriendlyName(String friendlyName) {
            this.friendlyName = friendlyName;
            return this;
        }
    }
}

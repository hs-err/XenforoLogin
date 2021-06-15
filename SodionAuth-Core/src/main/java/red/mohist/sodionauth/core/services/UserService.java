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

import org.hibernate.Session;
import red.mohist.sodionauth.core.authbackends.AuthBackend;
import red.mohist.sodionauth.core.authbackends.AuthBackends;
import red.mohist.sodionauth.core.entities.AuthInfo;
import red.mohist.sodionauth.core.entities.User;
import red.mohist.sodionauth.core.repositories.AuthinfoRepository;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.hasher.HasherTools;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class UserService {

    public UserService() {

    }

    public boolean verifyPassword(Session session, User user, String password) {
        Map<String, AuthInfo> authInfoMap = new HashMap<>();
        for (AuthInfo authInfo : AuthinfoRepository.getByUser(session, user)) {
            authInfoMap.put(authInfo.getType(), authInfo);
        }
        AtomicReference<Boolean> hasPassword = new AtomicReference<>(false);
        AtomicBoolean loginResult = new AtomicBoolean(false);
        authInfoMap.forEach((key, authInfo) -> {
            if (authInfo.getType().startsWith("password:")) {
                hasPassword.set(true);
                String hashName = authInfo.getType().substring("password:".length());
                if (HasherTools.getByName(hashName)
                        .verify(authInfo.getData(), password)) {
                    if (!hashName.equals(Config.database.passwordHash)) {
                        session.delete(authInfo);
                        session.save(user.createAuthInfo()
                                .setType("password:" + Config.database.passwordHash)
                                .setData(HasherTools.getDefault().hash(password)));
                    }
                    loginResult.set(true);
                }
            } else {
                AuthBackend authBackend = AuthBackends.getByName(authInfo.getType());
                if (authBackend.allowLogin && authBackend
                        .login(user, authInfo, password)
                        .type.equals(AuthBackend.LoginResultType.SUCCESS)) {
                    loginResult.set(true);
                }
            }
        });
        if (loginResult.get()) {
            if (!hasPassword.get() && !Config.database.passwordHash.equals("")) {
                session.save(user.createAuthInfo()
                        .setType("password:" + Config.database.passwordHash)
                        .setData(HasherTools.getDefault().hash(password)));
            }
            Map<String, Boolean> unLinkedTypes = new HashMap<>();
            AuthBackends.authBackendMap.forEach((key, authBackend) -> {
                unLinkedTypes.put(key, false);
            });
            authInfoMap.forEach((key, authInfo) -> {
                unLinkedTypes.put(key, true);
            });
            unLinkedTypes.forEach((key, linked) -> {
                if (!linked) {
                    AuthBackend authBackend = AuthBackends.getByName(key);
                    if (authBackend.allowRegister) {
                        AuthBackend.GetResult getResult = authBackend.get(user);
                        if (getResult.type.equals(AuthBackend.GetResultType.SUCCESS) &&
                                getResult.name.equals(user.getName())) {
                            if (authBackend.login(user, user.createAuthInfo()
                                    .setType(key), password).type.equals(AuthBackend.LoginResultType.SUCCESS)) {
                                session.save(user.createAuthInfo().setType(key));
                            }
                        }
                    }
                }
            });
        }
        return loginResult.get();
    }
}

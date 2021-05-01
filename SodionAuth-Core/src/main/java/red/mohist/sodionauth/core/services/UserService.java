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

import red.mohist.sodionauth.core.authbackends.AuthBackend;
import red.mohist.sodionauth.core.authbackends.AuthBackends;
import red.mohist.sodionauth.core.database.entities.AuthInfo;
import red.mohist.sodionauth.core.database.entities.User;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.hasher.HasherTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class UserService {

    public UserService() {
        Service.database.mapper.initEntity(User.class);

        Service.database.mapper.initEntity(AuthInfo.class);

    }

    public boolean verifyPassword(User user, String password) {
        Map<String,AuthInfo> authInfoMap = new HashMap<>();
        for (AuthInfo authInfo : user.getAuthInfo()) {
            authInfoMap.put(authInfo.getType(),authInfo);
        }
        AtomicBoolean loginResult = new AtomicBoolean(false);
        authInfoMap.forEach((key,authInfo)->{
            if(authInfo.getType().startsWith("password:")){
                String hashName = authInfo.getType().substring("password:".length());
                if (HasherTools.getByName(hashName)
                        .verify(authInfo.getData(),password)) {
                    if(!hashName.equals(Config.database.passwordHash)){
                        authInfo.delete();
                        user.createAuthInfo()
                                .setType("password:"+Config.database.passwordHash)
                                .setData(HasherTools.getDefault().hash(password))
                                .save();
                    }
                    loginResult.set(true);
                }
            }else{
                AuthBackend authBackend = AuthBackends.getByName(authInfo.getType());
                if (authBackend.allowLogin && authBackend
                        .login(user,authInfo,password)
                        .equals(AuthBackend.LoginResult.SUCCESS)) {
                    loginResult.set(true);
                }
            }
        });
        if(loginResult.get()){
            Map<String,Boolean> unLinkedTypes = new HashMap<>();
            AuthBackends.authBackendMap.forEach((key,authBackend)->{
                unLinkedTypes.put(key,false);
            });
            authInfoMap.forEach((key,authInfo)->{
                unLinkedTypes.put(key,true);
            });
            unLinkedTypes.forEach((key,linked)->{
                if(!linked){
                    AuthBackend authBackend = AuthBackends.getByName(key);
                    if(authBackend.allowRegister) {
                        AuthBackend.GetResult getResult = authBackend.get(user);
                        if (getResult.equals(AuthBackend.GetResult.SUCCESS)) {
                            if (authBackend.login(user, user.createAuthInfo()
                                    .setType(key), password).equals(AuthBackend.LoginResult.SUCCESS)) {
                                user.createAuthInfo().setType(key).save();
                            }
                        }
                    }
                }
            });
        }
        return loginResult.get();
    }
}

/*
 * Copyright 2020 Mohist-Community
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

package red.mohist.sodionauth.yggdrasilserver.provider;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import red.mohist.sodionauth.core.SodionAuthCore;
import red.mohist.sodionauth.core.authbackends.AuthBackendSystems;
import red.mohist.sodionauth.core.enums.ResultType;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.yggdrasilserver.implementation.PlainPlayer;
import red.mohist.sodionauth.yggdrasilserver.modules.Profile;
import red.mohist.sodionauth.yggdrasilserver.modules.Texture;
import red.mohist.sodionauth.yggdrasilserver.modules.User;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.UUID;

public class SessionProvider {
    public static SessionProvider instance;
    public Cache<String,Session> cache;
    public SessionProvider() {
        instance = this;
        cache = CacheBuilder.newBuilder().build();
    }
    public void join(String username,String serverId,String ip){
        cache.put(username,new Session(serverId,ip));
    }
    public void join(String username,String serverId){
        cache.put(username,new Session(serverId,null));
    }
    public boolean verify(String username,String serverId,String ip){
        Session session=cache.getIfPresent(username);
        if(session == null){
            return false;
        }
        if(!session.serverId.equals(serverId)){
            return false;
        }
        if(ip != null
                && session.ip != null
                && !session.ip.equals(ip)){
            return false;
        }
        return true;
    }
    static class Session{
        public String serverId;
        public String ip;
        public Session(String serverId, String ip) {
            this.serverId=serverId;
            this.ip=ip;
        }
    }
}

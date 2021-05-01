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

package red.mohist.sodionauth.core.database.entities;

import org.teasoft.honey.osql.core.ConditionImpl;
import red.mohist.sodionauth.core.database.annotations.limits.NotNull;
import red.mohist.sodionauth.core.database.annotations.limits.PrimaryKey;
import red.mohist.sodionauth.core.services.Service;
import red.mohist.sodionauth.core.utils.Helper;

import java.util.List;
import java.util.UUID;

public class Session extends Entity {
    @PrimaryKey
    protected Integer id;
    @NotNull
    protected String uuid;
    @NotNull
    protected String ip;
    @NotNull
    protected String time;

    public static Session getByUuid(UUID uuid) {
        return new Session().setUuid(Helper.toStringUuid(uuid)).first();
    }

    public Session[] get() {
        return (Session[]) Service.database.suid.select(this).toArray();
    }

    public Session first() {
        List<Session> session = Service.database.suid.select(this, new ConditionImpl().size(1));
        if (session.size() > 0) {
            return session.get(0);
        } else {
            return null;
        }
    }

    public Integer getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public Session setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }
    public Session setUuid(UUID uuid) {
        this.uuid = Helper.toStringUuid(uuid);
        return this;
    }

    public String getIp() {
        return ip;
    }

    public Session setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getTime() {
        return time;
    }

    public Session setTime(String time) {
        this.time = time;
        return this;
    }
}

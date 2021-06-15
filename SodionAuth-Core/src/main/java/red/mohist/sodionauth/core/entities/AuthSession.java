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

package red.mohist.sodionauth.core.entities;

import com.eloli.sodioncore.orm.SodionEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class AuthSession extends SodionEntity {
    @Id
    @Column
    protected UUID uuid;

    @Column
    protected String ip;

    @Column
    protected String time;

    public UUID getUuid() {
        return uuid;
    }

    public AuthSession setUuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public AuthSession setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getTime() {
        return time;
    }

    public AuthSession setTime(String time) {
        this.time = time;
        return this;
    }
}

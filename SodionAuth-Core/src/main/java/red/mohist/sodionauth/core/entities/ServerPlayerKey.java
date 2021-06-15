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

import red.mohist.sodionauth.core.utils.Config;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class ServerPlayerKey implements Serializable {
    protected UUID uuid;
    protected String serverId;

    public ServerPlayerKey() {
        //
    }

    public ServerPlayerKey(UUID uuid) {
        this.uuid = uuid;
        this.serverId = Config.serverId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerPlayerKey that = (ServerPlayerKey) o;
        return Objects.equals(uuid, that.uuid) && Objects.equals(serverId, that.serverId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, serverId);
    }
}

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

import java.util.List;

public class User extends Entity {
    @PrimaryKey
    protected Integer id;
    @NotNull
    protected String name;
    @NotNull
    protected String lowerName;
    protected String email;
    protected Boolean verified;
    protected String accessToken;
    protected String clientToken;
    protected String skinRestore;
    protected String skinHash;

    public static User getByName(String name) {
        return new User().setLowerName(name).first();
    }

    public static User get(Integer id) {
        return new User().setId(id).first();
    }

    public User[] get() {
        return (User[]) Service.database.suid.select(this).toArray();
    }

    public User first() {
        List<User> users = Service.database.suid.select(this, new ConditionImpl().size(1));
        if (users.size() > 0) {
            return users.get(0);
        } else {
            return null;
        }
    }

    public Integer getId() {
        return id;
    }

    protected User setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        lowerName = name.toLowerCase();
        return this;
    }

    public User setLowerName(String lowerName) {
        this.lowerName = lowerName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public Boolean getVerified() {
        return verified;
    }

    public User setVerified(Boolean verified) {
        this.verified = verified;
        return this;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public User setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public String getClientToken() {
        return clientToken;
    }

    public User setClientToken(String clientToken) {
        this.clientToken = clientToken;
        return this;
    }

    public String getSkinRestore() {
        return skinRestore;
    }

    public User setSkinRestore(String skinRestore) {
        this.skinRestore = skinRestore;
        return this;
    }

    public String getSkinHash() {
        return skinHash;
    }

    public User setSkinHash(String skinHash) {
        this.skinHash = skinHash;
        return this;
    }

    public AuthInfo[] getAuthInfo() {
        return Service.database.suid.select(new AuthInfo().setUserId(this.getId())).toArray(new AuthInfo[0]);
    }

    public AuthInfo createAuthInfo() {
        return new AuthInfo().setUserId(this.getId());
    }

    public User addAuthInfo(AuthInfo authInfo) {
        Service.database.suid.insert(authInfo.setUserId(this.getId()));
        return this;
    }

    public User clearAuthInfo() {
        Service.database.suid.delete(new AuthInfo().setUserId(this.getId()));
        return this;
    }


    public boolean verifyPassword(String password) {
        return Service.user.verifyPassword(this, password);
    }
}

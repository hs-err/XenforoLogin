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
import org.hibernate.Session;
import red.mohist.sodionauth.core.services.Service;

import javax.persistence.Column;
import javax.persistence.Id;

public class User extends SodionEntity {
    @Id
    @Column
    protected Integer id;

    @Column(nullable = false)
    protected String name;

    @Column(nullable = false)
    protected String lowerName;

    @Column(nullable = false)
    protected String email;

    @Column
    protected Boolean verified;

    @Column
    protected String accessToken;

    @Column
    protected String clientToken;

    @Column
    protected String skinRestore;

    @Column
    protected String skinHash;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        this.lowerName = name;
        return this;
    }

    public String getLowerName() {
        return lowerName;
    }

    public User setLowerName(String name) {
        this.lowerName = name.toLowerCase();
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

    public AuthInfo createAuthInfo() {
        return new AuthInfo().setUserId(this.id);
    }

    public boolean verifyPassword(Session session, String password) {
        return Service.user.verifyPassword(session, this, password);
    }
}

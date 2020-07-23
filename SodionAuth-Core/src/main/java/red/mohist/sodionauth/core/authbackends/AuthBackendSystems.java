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

package red.mohist.sodionauth.core.authbackends;

import red.mohist.sodionauth.core.authbackends.implementations.MysqlSystem;
import red.mohist.sodionauth.core.authbackends.implementations.SqliteSystem;
import red.mohist.sodionauth.core.authbackends.implementations.WebSystem;
import red.mohist.sodionauth.core.authbackends.implementations.XenforoSystem;
import red.mohist.sodionauth.core.utils.Config;

public class AuthBackendSystems {
    private static AuthBackendSystem currentSystem = null;

    public static void reloadConfig() {
        AuthBackendSystem cs;
        switch (Config.api.getSystem()) {
            case "xenforo":
                cs = new XenforoSystem(Config.api.getXenforo().getUrl(),
                        Config.api.getXenforo().getKey());
                break;
            case "web":
                cs = new WebSystem(Config.api.getWeb().getUrl(),
                        Config.api.getWeb().getKey());
                break;
            case "sqlite":
                cs = new SqliteSystem(Config.api.getSqlite().getPath(),
                        Config.api.getSqlite().getAbsolute(),
                        Config.api.getSqlite().getTableName(),
                        Config.api.getSqlite().getEmailField(),
                        Config.api.getSqlite().getUsernameField(),
                        Config.api.getSqlite().getPasswordField(),
                        Config.api.getSqlite().getSaltField(),
                        Config.api.getSqlite().getSaltLength(),
                        Config.api.getSqlite().getPasswordHash());
                break;
            case "mysql":
                cs = new MysqlSystem(Config.api.getMysql().getHost(),
                        Config.api.getMysql().getUsername(),
                        Config.api.getMysql().getPassword(),
                        Config.api.getMysql().getDatabase(),
                        Config.api.getMysql().getTableName(),
                        Config.api.getMysql().getEmailField(),
                        Config.api.getMysql().getUsernameField(),
                        Config.api.getMysql().getPasswordField(),
                        Config.api.getMysql().getSaltField(),
                        Config.api.getMysql().getSaltLength(),
                        Config.api.getMysql().getPasswordHash());
                break;
            default:
                cs = null;
        }
        if (cs == null) throw new NullPointerException();
        currentSystem = cs;
    }

    public static AuthBackendSystem getCurrentSystem() {
        return currentSystem;
    }

}

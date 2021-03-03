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

import com.google.common.eventbus.Subscribe;
import red.mohist.sodionauth.core.events.BootEvent;
import red.mohist.sodionauth.core.events.DownEvent;
import red.mohist.sodionauth.core.utils.Helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SessionService {
    private Connection connection;

    @Subscribe
    public void onBoot(BootEvent event) throws SQLException {
        Helper.getLogger().info("Initializing session service...");
        connection = DriverManager.getConnection("jdbc:sqlite:" + Helper.getConfigPath("SodionAuth.db"));
        if (!connection.getMetaData().getTables(null, null, "last_info", new String[]{"TABLE"}).next()) {
            PreparedStatement pps = connection.prepareStatement("CREATE TABLE last_info (uuid NOT NULL,info,PRIMARY KEY (uuid));");
            pps.executeUpdate();
        }
        if (!connection.getMetaData().getTables(null, null, "sessions", new String[]{"TABLE"}).next()) {
            PreparedStatement pps = connection.prepareStatement("CREATE TABLE sessions (uuid NOT NULL,ip,time,PRIMARY KEY (uuid));");
            pps.executeUpdate();
        }
    }

    @Subscribe
    public void onDown(DownEvent event) {
        try {
            connection.close();
        } catch (Throwable ignored) {
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public PreparedStatement prepareStatement(String var1) throws SQLException {
        return connection.prepareStatement(var1);
    }
}

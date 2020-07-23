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

package red.mohist.sodionauth.core.authbackends.implementations;

import com.google.common.collect.ImmutableMap;
import red.mohist.sodionauth.core.authbackends.AuthBackendSystem;
import red.mohist.sodionauth.core.enums.ResultType;
import red.mohist.sodionauth.core.hasher.HasherTool;
import red.mohist.sodionauth.core.hasher.HasherTools;
import red.mohist.sodionauth.core.modules.AbstractPlayer;

import javax.annotation.Nonnull;
import java.sql.*;

@SuppressWarnings("FieldCanBeLocal")
public class MysqlSystem implements AuthBackendSystem {
    private Connection connection;
    private final String host;
    private final String username;
    private final String password;
    private final String database;
    private final String tableName;
    private final String emailField;
    private final String usernameField;
    private final String passwordField;
    private final String saltField;
    private final int saltLength;
    private final String passwordHash;
    private final HasherTool hasherTool;

    public MysqlSystem(String host, String username, String password, String database, String tableName, String emailField, String usernameField, String passwordField, String saltField, int saltLength, String passwordHash) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.database = database;
        this.tableName = tableName;
        this.emailField = emailField;
        this.usernameField = usernameField;
        this.passwordField = passwordField;
        this.saltField = saltField;
        this.saltLength = saltLength;
        this.passwordHash = passwordHash;
        HasherTools.loadHasher(passwordHash, saltLength);
        this.hasherTool = HasherTools.getCurrentSystem();
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database + "?useSSL=false&serverTimezone=UTC", username, password);
            if (!connection.getMetaData().getTables(null, null, tableName, new String[]{"TABLE"}).next()) {
                PreparedStatement pps;
                if (hasherTool.needSalt()) {
                    pps = connection.prepareStatement(
                            "CREATE TABLE " + tableName + " (`id` int(11) NOT NULL AUTO_INCREMENT,`" + emailField + "` varchar(255) NOT NULL,`" + usernameField + "` varchar(32) NOT NULL,`" + passwordField + "` varchar(255) NOT NULL,`" + saltField + "` varchar(32) NOT NULL, PRIMARY KEY (`id`,`" + usernameField + "`));");

                } else {
                    pps = connection.prepareStatement(
                            "CREATE TABLE " + tableName + " (`id` int(11) NOT NULL AUTO_INCREMENT,`" + emailField + "` varchar(255) NOT NULL,`" + usernameField + "` varchar(32) NOT NULL,`" + passwordField + "` varchar(255) NOT NULL, PRIMARY KEY (`id`,`" + usernameField + "`));");
                }
                pps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Nonnull
    @Override
    public ResultType register(AbstractPlayer player, String password, String email) {
        try {
            PreparedStatement pps = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE lower(`" + usernameField + "`)=? LIMIT 1;");
            pps.setString(1, player.getName().toLowerCase());
            ResultSet rs = pps.executeQuery();
            if (rs.next()) {
                return ResultType.USER_EXIST;
            }

            pps = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE lower(`" + emailField + "`)=? LIMIT 1;");
            pps.setString(1, email);
            rs = pps.executeQuery();
            if (rs.next()) {
                return ResultType.EMAIL_EXIST;
            }

            if (hasherTool.needSalt()) {
                String salt = hasherTool.generateSalt();
                pps = connection.prepareStatement(
                        "INSERT INTO " + tableName + " (`" + emailField + "`, `" + usernameField + "`, `" + passwordField + "`,`" + saltField + "`) VALUES (?, ?, ?, ?);");
                pps.setString(1, email);
                pps.setString(2, player.getName());
                pps.setString(3, hasherTool.hash(password, salt));
                pps.setString(4, salt);
            } else {
                pps = connection.prepareStatement(
                        "INSERT INTO " + tableName + " (`" + emailField + "`, `" + usernameField + "`, `" + passwordField + "`) VALUES (?, ?, ?);");
                pps.setString(1, email);
                pps.setString(2, player.getName());
                pps.setString(3, hasherTool.hash(password));
            }
            pps.executeUpdate();

            return ResultType.OK;
        } catch (SQLException e) {
            e.printStackTrace();
            return ResultType.SERVER_ERROR;
        }
    }

    @Nonnull
    @Override
    public ResultType login(AbstractPlayer player, String password) {
        try {
            PreparedStatement pps = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE lower(`" + usernameField + "`)=? LIMIT 1;");
            pps.setString(1, player.getName().toLowerCase());
            ResultSet rs = pps.executeQuery();
            if (!rs.next()) {
                return ResultType.NO_USER;
            }
            if (!rs.getString(usernameField).equals(player.getName())) {
                return ResultType.ERROR_NAME.inheritedObject(ImmutableMap.of(
                        "correct", rs.getString(usernameField)));
            }
            if (hasherTool.needSalt()) {
                if (!hasherTool.verify(rs.getString(passwordField), password, rs.getString(saltField))) {
                    return ResultType.PASSWORD_INCORRECT;
                }
            } else {
                if (!hasherTool.verify(rs.getString(passwordField), password)) {
                    return ResultType.PASSWORD_INCORRECT;
                }
            }
            return ResultType.OK;
        } catch (SQLException e) {
            e.printStackTrace();
            return ResultType.SERVER_ERROR;
        }
    }

    @Nonnull
    @Override
    public ResultType join(AbstractPlayer player) {
        return join(player.getName());
    }

    @Nonnull
    @Override
    public ResultType join(String name) {
        try {
            PreparedStatement pps = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE lower(`" + usernameField + "`)=? LIMIT 1;");
            pps.setString(1, name.toLowerCase());
            ResultSet rs = pps.executeQuery();
            if (!rs.next()) {
                return ResultType.NO_USER;
            }
            if (!rs.getString(usernameField).equals(name)) {
                return ResultType.ERROR_NAME.inheritedObject(ImmutableMap.of(
                        "correct", rs.getString(usernameField)));
            }
            return ResultType.OK;
        } catch (SQLException e) {
            e.printStackTrace();
            return ResultType.SERVER_ERROR;
        }
    }
}

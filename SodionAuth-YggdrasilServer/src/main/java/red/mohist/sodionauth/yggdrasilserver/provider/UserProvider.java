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

import red.mohist.sodionauth.core.authbackends.AuthBackendSystems;
import red.mohist.sodionauth.core.enums.ResultType;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.yggdrasilserver.implementation.PlainPlayer;
import red.mohist.sodionauth.yggdrasilserver.modules.Profile;
import red.mohist.sodionauth.yggdrasilserver.modules.Texture;
import red.mohist.sodionauth.yggdrasilserver.modules.TokenPair;
import red.mohist.sodionauth.yggdrasilserver.modules.User;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.UUID;

public class UserProvider {
    public static UserProvider instance;
    public Connection connection;

    public UserProvider() throws SQLException {
        instance = this;
        connection = DriverManager.getConnection("jdbc:sqlite:yggdrasil.db");
        if (!connection.getMetaData().getTables(null, null, "tokens", new String[]{"TABLE"}).next()) {
            PreparedStatement pps;
            pps = connection.prepareStatement(
                    "CREATE TABLE tokens (" +
                            "`id` INTEGER NOT NULL," +
                            "`username` TEXT NOT NULL," +
                            "`accessToken` TEXT NOT NULL," +
                            "`clientToken` TEXT NOT NULL," +
                            "`status` INTEGER NOT NULL," +
                            "`timestamp` INTEGER NOT NULL," +
                            " PRIMARY KEY (`accessToken`,`accessToken`));");
            pps.executeUpdate();
        }
    }

    public String login(String username, String password, String clientToken) throws SQLException {
        if (AuthBackendSystems.getCurrentSystem().login(new PlainPlayer(username), password) == ResultType.OK) {
            String accessToken = UUID.randomUUID().toString();
            addToken(username, clientToken, accessToken);
            return accessToken;
        }
        return null;
    }

    public void addToken(String username, String clientToken, String accessToken) throws SQLException {
        PreparedStatement pps;
        pps = connection.prepareStatement(
                "INSERT INTO tokens (`username`,`accessToken`,`clientToken`,`status`,`timestamp`)VALUES(?,?,?,?,?);");
        pps.setString(1, username);
        pps.setString(2, accessToken);
        pps.setString(3, clientToken);
        pps.setInt(4, 0);
        pps.setInt(5, (int) (System.currentTimeMillis() / 1000));
        pps.execute();
    }

    public boolean verifyToken(String username, String clientToken, String accessToken) throws SQLException {
        PreparedStatement pps;
        refresh();

        pps = connection.prepareStatement("SELECT * FROM tokens WHERE `username`=? AND `clientToken`=? AND `accessToken`=? LIMIT 1;");
        pps.setString(1, username);
        pps.setString(2, clientToken);
        pps.setString(3, accessToken);
        ResultSet rs = pps.executeQuery();
        if (!rs.next()) {
            return false;
        }
        if (rs.getInt("status") == 0) {
            return true;
        } else if (rs.getInt("status") == 1) {
            pps = connection.prepareStatement(
                    "UPDATE tokens SET `status` = ? WHERE `username` = ?;");
            pps.setInt(1, 1);
            pps.setString(2, username);
            pps.execute();

            pps = connection.prepareStatement(
                    "UPDATE tokens SET `status` = ? WHERE `username` = ? AND `clientToken` = ? AND `accessToken` = ?;");
            pps.setInt(1, 0);
            pps.setString(2, username);
            pps.setString(3, clientToken);
            pps.setString(4, accessToken);
            pps.execute();
            return true;
        } else {
            return false;
        }
    }

    public TokenPair refreshToken(String username, String clientToken, String accessToken) throws SQLException {
        PreparedStatement pps;
        refresh();
        if (clientToken == null) {
            pps = connection.prepareStatement("SELECT * FROM tokens WHERE `username` = ? AND `accessToken`=? LIMIT 1;");
            pps.setString(1, username);
            pps.setString(2, accessToken);
        } else {
            pps = connection.prepareStatement("SELECT * FROM tokens WHERE `username` = ? AND `clientToken`=? AND `accessToken`=? LIMIT 1;");
            pps.setString(1, username);
            pps.setString(2, clientToken);
            pps.setString(3, accessToken);
        }
        ResultSet rs = pps.executeQuery();
        if (!rs.next()) {
            return null;
        }
        clientToken = rs.getString("clientToken");
        if (rs.getInt("status") == 0 || rs.getInt("status") == 1) {
            pps = connection.prepareStatement(
                    "UPDATE tokens SET `status` = ? WHERE `username`=?;");
            pps.setInt(1, 1);
            pps.setString(2, username);
            pps.execute();

            pps = connection.prepareStatement(
                    "UPDATE tokens SET `status` = ? , `accessToken` = ? WHERE `username` = ? AND `accessToken` = ?;");
            pps.setInt(1, 0);
            pps.setString(4, accessToken);
            accessToken = UUID.randomUUID().toString();
            pps.setString(2, accessToken);
            pps.setString(3, username);
            pps.execute();
            return new TokenPair()
                    .setAccessToken(accessToken)
                    .setClientToken(clientToken);
        } else {
            return null;
        }
    }

    public void invalidateToken(String accessToken) throws SQLException {
        PreparedStatement pps = connection.prepareStatement(
                "DELETE FROM tokens WHERE `accessToken` = ?");
        pps.setString(1, accessToken);
        pps.execute();
    }

    public boolean signout(String username, String password) throws SQLException {
        if (AuthBackendSystems.getCurrentSystem().login(new PlainPlayer(username), password) != ResultType.OK) {
            return false;
        }
        PreparedStatement pps = connection.prepareStatement(
                "DELETE FROM tokens WHERE `username` = ?");
        pps.setString(1, username);
        pps.execute();
        return true;
    }

    public void refresh() throws SQLException {
        PreparedStatement pps = connection.prepareStatement(
                "DELETE FROM tokens WHERE `timestamp` < ?");
        pps.setInt(1, (int) (System.currentTimeMillis() / 1000 - Config.yggdrasil.getToken().getTimeToFullyExpired()));
        pps.execute();
    }

    public User getUser(String username) {
        return new User()
                .setId(plainUUID(getUUID(username)));
    }

    public Profile getProfile(String username) {
        return new Profile()
                .setId(plainUUID(getUUID(username)))
                .setName(username);
        //.addProperties("textures",
        //        Base64.getEncoder().encodeToString(
        //                new Gson().toJson(getTexture(username)).getBytes()
        //        )
        //);
    }

    // TODO: 增加皮肤支持
    public Texture getTexture(String username) {
        return null;
    }

    private UUID getUUID(String username) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
    }

    private String plainUUID(UUID uuid) {
        return uuid.toString().replaceAll("-", "");
    }

}

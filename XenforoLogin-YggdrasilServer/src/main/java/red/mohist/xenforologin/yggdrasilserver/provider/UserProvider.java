/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.yggdrasilserver.provider;

import red.mohist.xenforologin.core.utils.Config;

import java.sql.*;

public class UserProvider {
    public static UserProvider instance;
    public Connection connection;
    public UserProvider() throws SQLException {
        instance=this;
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
    public void addToken(String username,String clientToken,String accessToken) throws SQLException {
        PreparedStatement pps = connection.prepareStatement(
                "DELETE FROM table_name WHERE `timestamp` < ?");
        pps.setInt(1, (int) (System.currentTimeMillis() / 1000 - Config.getInteger("yggdrasil.token.time-to-fully-expired")));
        pps.execute();

        pps = connection.prepareStatement(
                "INSERT INTO tokens (`username`,`accessToken`,`clientToken`,`status`,`timestamp`)VALUES('username','accessToken','clientToken','status','timestamp');");
        pps.setString(1, username);
        pps.setString(2, accessToken);
        pps.setString(3, clientToken);
        pps.setInt(4, 0);
        pps.setInt(5, (int) (System.currentTimeMillis() / 1000 - Config.getInteger("yggdrasil.token.time-to-fully-expired")));
        pps.execute();
    }
    public boolean verifyToken(String username,String clientToken,String accessToken) throws SQLException {
        PreparedStatement pps = connection.prepareStatement(
                "DELETE FROM table_name WHERE `timestamp` < ?");
        pps.setInt(1, (int) (System.currentTimeMillis() / 1000 - Config.getInteger("yggdrasil.token.time-to-fully-expired")));
        pps.execute();

        pps = connection.prepareStatement("SELECT * FROM tokens WHERE `username`=? AND `clientToken`=? AND `accessToken`=? LIMIT 1;");
        pps.setString(1, username);
        pps.setString(2, clientToken);
        pps.setString(3, accessToken);
        ResultSet rs = pps.executeQuery();
        if (!rs.next()) {
            return false;
        }
        if(rs.getInt("status")==0){
            return true;
        }else if(rs.getInt("status")==1){
            pps = connection.prepareStatement(
                    "UPDATE tokens SET `status` = ? WHERE username = ?;");
            pps.setInt(1, 1);
            pps.setString(2, username);
            pps.execute();

            pps = connection.prepareStatement(
                    "UPDATE tokens SET `status` = ? WHERE username = ? AND clientToken = ? AND accessToken = ?;");
            pps.setInt(1, 0);
            pps.setString(2, username);
            pps.setString(3, clientToken);
            pps.setString(4, accessToken);
            pps.execute();

            return true;
        }else{
            return false;
        }
    }
}

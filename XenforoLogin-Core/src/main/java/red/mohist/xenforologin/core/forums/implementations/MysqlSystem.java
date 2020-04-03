/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.core.forums.implementations;

import com.google.common.collect.ImmutableMap;
import red.mohist.xenforologin.core.XenforoLoginCore;
import red.mohist.xenforologin.core.enums.ResultType;
import red.mohist.xenforologin.core.forums.ForumSystem;
import red.mohist.xenforologin.core.modules.AbstractPlayer;

import javax.annotation.Nonnull;
import java.sql.*;

public class MysqlSystem implements ForumSystem {
    private Connection connection;
    private String host;
    private String username;
    private String password;
    private String database;
    private String table_name;
    private String email_field;
    private String username_field;
    private String password_field;
    private String password_hash;
    public MysqlSystem(String host, String username,String password, String database,String table_name, String email_field, String username_field, String password_field, String password_hash) {
        this.host=host;
        this.username=username;
        this.password=password;
        this.database=database;
        this.table_name=table_name;
        this.email_field=email_field;
        this.username_field=username_field;
        this.password_field=password_field;
        this.password_hash=password_hash;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://"+host+"/"+database+"?useSSL=false&serverTimezone=UTC",username,password);
            if(!connection.getMetaData().getTables(null,null,table_name,new String[]{ "TABLE" }).next()){
                PreparedStatement pps = connection.prepareStatement(
                        "CREATE TABLE "+table_name+" (`id` int(11) NOT NULL AUTO_INCREMENT,`"+email_field+"` varchar(255) NOT NULL,`"+username_field+"` varchar(32) NOT NULL,`"+password_field+"` varchar(255) NOT NULL, PRIMARY KEY (`id`,`username`));");
                pps.executeUpdate();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Nonnull
    @Override
    public ResultType register(AbstractPlayer player, String password, String email) {
        try {
            PreparedStatement pps = connection.prepareStatement("SELECT * FROM "+table_name+" WHERE lower(`"+username_field+"`)=? LIMIT 1;");
            pps.setString(1,player.getName().toLowerCase());
            ResultSet rs = pps.executeQuery();
            if(rs.next()){
                return ResultType.USER_EXIST;
            }

            pps = connection.prepareStatement("SELECT * FROM "+table_name+" WHERE lower(`"+email_field+"`)=? LIMIT 1;");
            pps.setString(1,email);
            rs = pps.executeQuery();
            if(rs.next()){
                return ResultType.EMAIL_EXIST;
            }

            pps = connection.prepareStatement(
                    "INSERT INTO "+table_name+" (`"+email_field+"`, `"+username_field+"`, `"+password_field+"`) VALUES (?, ?, ?);");
            pps.setString(1,email);
            pps.setString(2,player.getName());
            pps.setString(3,password);
            pps.executeUpdate();

            return ResultType.OK;
        }catch (SQLException e){
            e.printStackTrace();
            return ResultType.SERVER_ERROR;
        }
    }

    @Nonnull
    @Override
    public ResultType login(AbstractPlayer player, String password) {
        try {
            PreparedStatement pps = connection.prepareStatement("SELECT * FROM "+table_name+" WHERE lower(`"+username_field+"`)=? LIMIT 1;");
            pps.setString(1,player.getName().toLowerCase());
            ResultSet rs = pps.executeQuery();
            if(!rs.next()){
                return ResultType.NO_USER;
            }
            if(!rs.getString("username").equals(player.getName())){
                return ResultType.ERROR_NAME.inheritedObject(ImmutableMap.of(
                        "correct", rs.getString("username")));
            }
            if(!rs.getString("password").equals(password)){
                return ResultType.PASSWORD_INCORRECT;
            }
            return ResultType.OK;
        }catch (SQLException e){
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
            PreparedStatement pps = connection.prepareStatement("SELECT * FROM "+table_name+" WHERE lower(`"+username_field+"`)=? LIMIT 1;");
            pps.setString(1,name.toLowerCase());
            ResultSet rs = pps.executeQuery();
            if(!rs.next()){
                return ResultType.NO_USER;
            }
            if(!rs.getString("username").equals(name)){
                return ResultType.ERROR_NAME.inheritedObject(ImmutableMap.of(
                        "correct", rs.getString("username")));
            }
            return ResultType.OK;
        }catch (SQLException e){
            e.printStackTrace();
            return ResultType.SERVER_ERROR;
        }
    }
}

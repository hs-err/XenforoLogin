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

public class SqliteSystem implements ForumSystem {
    private Connection connection;
    private String table_name;
    private String email_field;
    private String username_field;
    private String password_field;
    private String password_hash;
    public SqliteSystem(String path, boolean absolute, String table_name, String email_field, String username_field, String password_field,String password_hash) {
        this.table_name=table_name;
        this.email_field=email_field;
        this.username_field=username_field;
        this.password_field=password_field;
        this.password_hash=password_hash;
        try {
            if(absolute){
                connection = DriverManager.getConnection("jdbc:sqlite:" + path);
                XenforoLoginCore.instance.api.getLogger().info(path);
            }else{
                connection = DriverManager.getConnection("jdbc:sqlite:" + XenforoLoginCore.instance.api.getConfigPath(path));
                XenforoLoginCore.instance.api.getLogger().info(XenforoLoginCore.instance.api.getConfigPath(path));
            }
            if(!connection.getMetaData().getTables(null,null,table_name,new String[]{ "TABLE" }).next()){
                PreparedStatement pps = connection.prepareStatement(
                        "CREATE TABLE "+table_name+" (`id` INTEGER NOT NULL,`"+email_field+"` TEXT NOT NULL,`"+username_field+"` TEXT NOT NULL,`"+password_field+"` TEXT NOT NULL, PRIMARY KEY (`id`));");
                pps.executeUpdate();
            };
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
            PreparedStatement pps = connection.prepareStatement("SELECT * FROM "+table_name+" WHERE lower("+username_field+")=? LIMIT 1;");
            pps.setString(1,player.getName().toLowerCase());
            ResultSet rs = pps.executeQuery();
            if(rs.next()){
                return ResultType.USER_EXIST;
            }

            pps = connection.prepareStatement("SELECT * FROM "+table_name+" WHERE lower("+email_field+")=? LIMIT 1;");
            pps.setString(1,email);
            rs = pps.executeQuery();
            if(rs.next()){
                return ResultType.EMAIL_EXIST;
            }

            pps = connection.prepareStatement(
                    "INSERT INTO "+table_name+" ("+email_field+", "+username_field+", "+password_field+") VALUES (?, ?, ?);");
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
            PreparedStatement pps = connection.prepareStatement("SELECT * FROM "+table_name+" WHERE lower("+username_field+")=? LIMIT 1;");
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
            PreparedStatement pps = connection.prepareStatement("SELECT * FROM "+table_name+" WHERE lower("+username_field+")=? LIMIT 1;");
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

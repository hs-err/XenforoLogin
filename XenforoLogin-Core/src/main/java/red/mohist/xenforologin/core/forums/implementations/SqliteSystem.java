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
import red.mohist.xenforologin.core.hasher.HasherTool;
import red.mohist.xenforologin.core.hasher.HasherTools;
import red.mohist.xenforologin.core.modules.AbstractPlayer;

import javax.annotation.Nonnull;
import java.sql.*;

public class SqliteSystem implements ForumSystem {
    private Connection connection;
    private String tableName;
    private String emailField;
    private String usernameField;
    private String passwordField;
    private String saltField;
    private int saltLength;
    private String passwordHash;
    private HasherTool hasherTool;
    public SqliteSystem(String path, boolean absolute, String tableName, String emailField, String usernameField, String passwordField,String saltField,int saltLength,String passwordHash) {
        this.tableName=tableName;
        this.emailField=emailField;
        this.usernameField=usernameField;
        this.passwordField=passwordField;
        this.saltField=saltField;
        this.saltLength=saltLength;
        this.passwordHash=passwordHash;
        HasherTools.loadHasher(passwordHash,saltLength);
        hasherTool = HasherTools.getCurrentSystem();
        try {
            if(absolute){
                connection = DriverManager.getConnection("jdbc:sqlite:" + path);
                XenforoLoginCore.instance.api.getLogger().info(path);
            }else{
                connection = DriverManager.getConnection("jdbc:sqlite:" + XenforoLoginCore.instance.api.getConfigPath(path));
                XenforoLoginCore.instance.api.getLogger().info(XenforoLoginCore.instance.api.getConfigPath(path));
            }
            if(!connection.getMetaData().getTables(null,null,tableName,new String[]{ "TABLE" }).next()){
                PreparedStatement pps;
                if(hasherTool.needSalt()){
                    pps = connection.prepareStatement(
                            "CREATE TABLE "+tableName+" (`id` INTEGER NOT NULL,`"+emailField+"` TEXT NOT NULL,`"+usernameField+"` TEXT NOT NULL,`"+passwordField+"` TEXT NOT NULL,`"+saltField+"` TEXT NOT NULL, PRIMARY KEY (`id`,`"+usernameField+"`));");
                }else{
                    pps = connection.prepareStatement(
                            "CREATE TABLE "+tableName+" (`id` INTEGER NOT NULL,`"+emailField+"` TEXT NOT NULL,`"+usernameField+"` TEXT NOT NULL,`"+passwordField+"` TEXT NOT NULL, PRIMARY KEY (`id`,`"+usernameField+"`));");
                }
                pps.executeUpdate();
            };
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Nonnull
    @Override
    public ResultType register(AbstractPlayer player, String password, String email) {
        try {
            PreparedStatement pps = connection.prepareStatement("SELECT * FROM "+tableName+" WHERE lower("+usernameField+")=? LIMIT 1;");
            pps.setString(1,player.getName().toLowerCase());
            ResultSet rs = pps.executeQuery();
            if(rs.next()){
                return ResultType.USER_EXIST;
            }

            pps = connection.prepareStatement("SELECT * FROM "+tableName+" WHERE lower("+emailField+")=? LIMIT 1;");
            pps.setString(1,email);
            rs = pps.executeQuery();
            if(rs.next()){
                return ResultType.EMAIL_EXIST;
            }

            if(hasherTool.needSalt()){
                String salt= hasherTool.generateSalt();
                pps = connection.prepareStatement(
                        "INSERT INTO "+tableName+" ("+emailField+", "+usernameField+", "+passwordField+",salt) VALUES (?, ?, ?, ?);");
                pps.setString(1,email);
                pps.setString(2,player.getName());
                pps.setString(3, hasherTool.hash(password,salt));
                pps.setString(4,salt);
            }else{
                pps = connection.prepareStatement(
                        "INSERT INTO "+tableName+" ("+emailField+", "+usernameField+", "+passwordField+") VALUES (?, ?, ?);");
                pps.setString(1,email);
                pps.setString(2,player.getName());
                pps.setString(3, hasherTool.hash(password));
            }
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
            PreparedStatement pps = connection.prepareStatement("SELECT * FROM "+tableName+" WHERE lower("+usernameField+")=? LIMIT 1;");
            pps.setString(1,player.getName().toLowerCase());
            ResultSet rs = pps.executeQuery();
            if(!rs.next()){
                return ResultType.NO_USER;
            }
            if(!rs.getString(usernameField).equals(player.getName())){
                return ResultType.ERROR_NAME.inheritedObject(ImmutableMap.of(
                        "correct", rs.getString(usernameField)));
            }
            if(hasherTool.needSalt()) {
                if (!hasherTool.verify(rs.getString(passwordField), password, rs.getString(saltField))) {
                    return ResultType.PASSWORD_INCORRECT;
                }
            }else{
                if (!hasherTool.verify(rs.getString(passwordField), password)) {
                    return ResultType.PASSWORD_INCORRECT;
                }
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
            PreparedStatement pps = connection.prepareStatement("SELECT * FROM "+tableName+" WHERE lower("+usernameField+")=? LIMIT 1;");
            pps.setString(1,name.toLowerCase());
            ResultSet rs = pps.executeQuery();
            if(!rs.next()){
                return ResultType.NO_USER;
            }
            if(!rs.getString(usernameField).equals(name)){
                return ResultType.ERROR_NAME.inheritedObject(ImmutableMap.of(
                        "correct", rs.getString(usernameField)));
            }
            return ResultType.OK;
        }catch (SQLException e){
            e.printStackTrace();
            return ResultType.SERVER_ERROR;
        }
    }
}

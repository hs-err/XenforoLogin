package red.mohist.xenforologin.core.forums.implementations;

import com.google.common.collect.ImmutableMap;
import red.mohist.xenforologin.core.enums.ResultType;
import red.mohist.xenforologin.core.forums.ForumSystem;
import red.mohist.xenforologin.core.modules.AbstractPlayer;

import javax.annotation.Nonnull;
import javax.xml.transform.Result;
import java.sql.*;

public class SqliteSystem implements ForumSystem {
    private Connection connection;
    public SqliteSystem(String path) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + path);
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
            PreparedStatement pps = connection.prepareStatement("SELECT * FROM users WHERE lower(`username`)=? LIMIT 1;");
            pps.setString(1,player.getName().toLowerCase());
            ResultSet rs = pps.executeQuery();
            if(rs.next()){
                return ResultType.USER_EXIST;
            }

            pps = connection.prepareStatement("SELECT * FROM users WHERE lower(`email`)=? LIMIT 1;");
            pps.setString(1,email);
            rs = pps.executeQuery();
            if(rs.next()){
                return ResultType.EMAIL_EXIST;
            }

            pps = connection.prepareStatement(
                    "INSERT INTO users (`email`, `username`, `password`) VALUES (?, ?, ?);");
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
            PreparedStatement pps = connection.prepareStatement("SELECT * FROM users WHERE lower(`username`)=? LIMIT 1;");
            pps.setString(1,player.getName().toLowerCase());
            ResultSet rs = pps.executeQuery();
            if(!rs.next()){
                return ResultType.NO_USER;
            }
            if(!rs.getString("username").equals(player.getName())){
                return ResultType.ERROR_NAME.inheritedObject(ImmutableMap.of(
                        "correct", rs.getString("username")));
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
            PreparedStatement pps = connection.prepareStatement("SELECT * FROM users WHERE lower(`username`)=? LIMIT 1;");
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

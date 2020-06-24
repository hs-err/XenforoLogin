/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.yggdrasilserver;

import red.mohist.xenforologin.core.utils.Config;
import red.mohist.xenforologin.core.utils.Helper;
import red.mohist.xenforologin.yggdrasilserver.modules.TokenPair;
import red.mohist.xenforologin.yggdrasilserver.provider.UserProvider;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.sql.SQLException;

public class YggdrasilServerCore {
    private int port ;
    public static YggdrasilServerCore instance;
    public RSAPublicKey rsaPublicKey;
    public RSAPrivateKey rsaPrivateKey;
    public KeyPair rsaKeyPair;
    public YggdrasilServerCore() throws NoSuchAlgorithmException, SQLException {
        this.port = Config.getInteger("yggdrasil.server.port");
        instance=this;
        new UserProvider();
        generalKey();
    }

    public void start() throws Exception{
        String accessToken = UserProvider.instance.login("logos","PoweredBy1090","client");
        if(accessToken==null){
            Helper.getLogger().info("Login fail");
            return;
        }

        if(!UserProvider.instance.verifyToken("logos","client",accessToken)){
            Helper.getLogger().info("Verify before fresh fail");
            return;
        }

        TokenPair tokenPair=UserProvider.instance.refreshToken("logos","client",accessToken);
        if(tokenPair==null){
            Helper.getLogger().info("Refresh with client fail");
            return;
        }
        accessToken=tokenPair.accessToken;
        tokenPair=UserProvider.instance.refreshToken("logos",null,accessToken);
        if(tokenPair==null){
            Helper.getLogger().info("Refresh without client fail");
            return;
        }
        accessToken=tokenPair.accessToken;

        if(!UserProvider.instance.verifyToken("logos","client",accessToken)){
            Helper.getLogger().info("Verify after refresh fail");
            return;
        }

        UserProvider.instance.invalidateToken(accessToken);

        if(UserProvider.instance.verifyToken("logos","client",accessToken)){
            Helper.getLogger().info("Verify after invalidate still success");
            return;
        }

        accessToken = UserProvider.instance.login("logos","PoweredBy1090","ctoken");
        if(accessToken==null){
            Helper.getLogger().info("Login.2 fail");
            return;
        }

        if(!UserProvider.instance.signout("logos","PoweredBy1090")){
            Helper.getLogger().info("SignOut fail");
            return;
        }

        if(UserProvider.instance.verifyToken("logos","ctoken",accessToken)){
            Helper.getLogger().info("Verify after sign out still success");
            return;
        }

        Helper.getLogger().info("2333");
    }
    private void generalKey() throws NoSuchAlgorithmException {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(4096, new SecureRandom());
        rsaKeyPair=gen.genKeyPair();
        rsaPublicKey = (RSAPublicKey) rsaKeyPair.getPublic();
        rsaPrivateKey = (RSAPrivateKey) rsaKeyPair.getPrivate();
    }
}

/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.yggdrasilserver;


import com.google.gson.Gson;
import org.apache.http.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.logging.log4j.LogManager;
import org.w3c.dom.Entity;
import red.mohist.xenforologin.core.XenforoLoginCore;
import red.mohist.xenforologin.core.utils.Config;
import red.mohist.xenforologin.core.utils.Helper;
import red.mohist.xenforologin.yggdrasilserver.modules.RequestConfig;

import java.io.*;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Base64;

public class YggdrasilServerCore {
    private RSAPublicKey rsaPublicKey;
    private RSAPrivateKey rsaPrivateKey;
    public YggdrasilServerCore() throws IOException, NoSuchAlgorithmException {
        generalKey();

       ServerBootstrap.bootstrap()
               .setListenerPort(8080)
               .registerHandler("/", (request, response, context) -> {
                   RequestConfig requestConfig=new RequestConfig();
                   requestConfig
                           .addMeta("serverName",Config.getString("yggdrasil.serverName","XenforoLoginCore"))
                           .addMeta("implementationName","XenforoLoginYggdrasil")
                           .addMeta("implementationVersion","testTEST");
                   ArrayList<String> skinDomains=Config.getStringArray("yggdrasil.skinDomains");
                   //skinDomains.forEach(requestConfig::addSkinDomains);
                   requestConfig.setSignaturePublickey("-----BEGIN PUBLIC KEY-----\n" +
                           Base64.getMimeEncoder(76, new byte[] { '\n' }).encodeToString(rsaPublicKey.getEncoded()) +
                           "\n-----END PUBLIC KEY-----\n");
                   response.setEntity(new StringEntity(new Gson().toJson(requestConfig)));
               })
               .setExceptionLogger(Throwable::printStackTrace)
               .create().start();
    }
    private void generalKey() throws NoSuchAlgorithmException {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(4096, new SecureRandom());
        rsaPublicKey= (RSAPublicKey) gen.genKeyPair().getPublic();
        rsaPrivateKey= (RSAPrivateKey) gen.genKeyPair().getPrivate();
    }
}

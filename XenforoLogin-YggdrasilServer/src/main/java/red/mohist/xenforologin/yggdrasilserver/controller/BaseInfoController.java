/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.yggdrasilserver.controller;

import com.google.gson.Gson;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import red.mohist.xenforologin.core.utils.Config;
import red.mohist.xenforologin.yggdrasilserver.YggdrasilServerCore;
import red.mohist.xenforologin.yggdrasilserver.modules.RequestConfig;

import java.util.Base64;

public class BaseInfoController extends Controller {
    @Override
    public FullHttpResponse hanlde(FullHttpRequest request) {
        RequestConfig requestConfig=new RequestConfig();
        requestConfig
                .addMeta("serverName", Config.getString("yggdrasil.core.serverName","XenforoLoginCore"))
                .addMeta("implementationName","XenforoLoginYggdrasil")
                .addMeta("implementationVersion","testTEST");
        Config.getConfig("yggdrasil.core.skinDomains").getAsJsonArray().forEach((skinDomain)->{
            requestConfig.addSkinDomains(skinDomain.getAsString());
        });
        requestConfig.setSignaturePublickey("-----BEGIN PUBLIC KEY-----\n" +
                Base64.getMimeEncoder(76, new byte[] { '\n' }).encodeToString(YggdrasilServerCore.instance.rsaPublicKey.getEncoded()) +
                "\n-----END PUBLIC KEY-----\n");
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer(new Gson().toJson(requestConfig), CharsetUtil.UTF_8));
        // 设置头信息
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
        return response;
    }
}

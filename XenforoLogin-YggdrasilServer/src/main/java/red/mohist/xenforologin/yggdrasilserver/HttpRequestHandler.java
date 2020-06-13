/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.yggdrasilserver;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import red.mohist.xenforologin.yggdrasilserver.controller.AuthenticateController;
import red.mohist.xenforologin.yggdrasilserver.controller.BaseInfoController;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        // 获取请求的uri
        String uri = req.uri();
        FullHttpResponse response;
        switch (uri){
            case "/":
                response=new BaseInfoController().hanlde(req);
                break;
            case "/authserver/authenticate":
                response=new AuthenticateController().hanlde(req);
                break;
            default:
                response=new BaseInfoController().hanlde(req);
        }
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
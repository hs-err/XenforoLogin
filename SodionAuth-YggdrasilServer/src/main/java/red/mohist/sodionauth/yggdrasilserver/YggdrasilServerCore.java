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

package red.mohist.sodionauth.yggdrasilserver;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.Helper;
import red.mohist.sodionauth.yggdrasilserver.controller.*;
import red.mohist.sodionauth.yggdrasilserver.modules.RequestConfig;
import red.mohist.sodionauth.yggdrasilserver.modules.TokenPair;
import red.mohist.sodionauth.yggdrasilserver.provider.UserProvider;

import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static io.netty.handler.codec.http.HttpUtil.is100ContinueExpected;

@SuppressWarnings("FieldCanBeLocal")
public class YggdrasilServerCore {
    public static YggdrasilServerCore instance;
    private final int port;
    public RSAPublicKey rsaPublicKey;
    public RSAPrivateKey rsaPrivateKey;
    public KeyPair rsaKeyPair;

    public YggdrasilServerCore() throws NoSuchAlgorithmException, SQLException {
        this.port = Config.yggdrasil.getServer().getPort();
        instance = this;
        generalKey();
        new UserProvider();
    }
    private void generalKey() throws NoSuchAlgorithmException {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(4096, new SecureRandom());
        rsaKeyPair = gen.genKeyPair();
        rsaPublicKey = (RSAPublicKey) rsaKeyPair.getPublic();
        rsaPrivateKey = (RSAPrivateKey) rsaKeyPair.getPrivate();
    }

    public void start() throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();
        bootstrap.group(boss,work)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .channel(NioServerSocketChannel.class)
                .childHandler(new HttpServerInitializer());

        ChannelFuture f = bootstrap.bind(new InetSocketAddress(port)).sync();
        System.out.println(" server start up on port : " + port);
        f.channel().closeFuture().sync();

    }
    public static class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel channel) throws Exception {
            ChannelPipeline pipeline = channel.pipeline();
            pipeline.addLast(new HttpServerCodec());// http 编解码
            pipeline.addLast("httpAggregator",new HttpObjectAggregator(512*1024)); // http 消息聚合器                                                                     512*1024为接收的最大contentlength
            pipeline.addLast(new HttpRequestHandler());// 请求处理器

        }
    }
    public static class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.flush();
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
            //100 Continue
            if (is100ContinueExpected(req)) {
                ctx.write(new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1,
                        HttpResponseStatus.CONTINUE));
            }
            Controller controller;
            String uri=req.uri();
            if(uri.startsWith("/")) {
                controller = new BaseConfigController();
            }else if(uri.startsWith("/authserver/authenticate")){
                controller=new LoginController();
            }else if(uri.startsWith("/authserver/refresh")){
                controller=new RefreshController();
            }else if(uri.startsWith("/authserver/validate")){
                controller=new ValidateController();
            }else if(uri.startsWith("/authserver/invalidate")){
                controller=new InvalidateController();
            }else if(uri.startsWith("/sessionserver/session/minecraft/join")){
                controller=new JoinController();
            }else if(uri.startsWith("/sessionserver/session/minecraft/hasJoined")){
                controller=new HasJoinedController();
            }else if(uri.startsWith("/sessionserver/session/minecraft/profile/")){
                controller=new ProfileController();
            }else if(uri.startsWith("/api/profiles/minecraft")) {
                controller = new ProfilesController();
            }else{
                controller=new NotFoundController();
            }
            Object msg=controller.handle(
                    new Gson().fromJson(req.content().toString(CharsetUtil.UTF_8), JsonElement.class),
                    req);
            FullHttpResponse response;
            if(msg == null){
                response = new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1,
                        HttpResponseStatus.NO_CONTENT);
            }else if(msg instanceof FullHttpResponse) {
                response = (FullHttpResponse) msg;
            }else {
                response = new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1,
                        HttpResponseStatus.OK,
                        Unpooled.copiedBuffer(new Gson().toJson(msg), CharsetUtil.UTF_8));
            }
            // 设置头信息
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=utf-s");
            response.headers().set(HttpHeaderNames.SERVER, "LogosNoFox");
            // 将html write到客户端
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }
}

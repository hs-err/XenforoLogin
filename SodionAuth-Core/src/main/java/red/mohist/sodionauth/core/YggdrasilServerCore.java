/*
 * Copyright 2021 Mohist-Community
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

package red.mohist.sodionauth.core;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
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
import red.mohist.sodionauth.core.controller.*;
import red.mohist.sodionauth.core.provider.UserProvider;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.Helper;

import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.netty.handler.codec.http.HttpUtil.is100ContinueExpected;

@SuppressWarnings("FieldCanBeLocal")
public class YggdrasilServerCore {
    public static YggdrasilServerCore instance;
    protected static Map<String, Controller> router = new ConcurrentHashMap<>();
    private final int port;
    public RSAPublicKey rsaPublicKey;
    public RSAPrivateKey rsaPrivateKey;
    public KeyPair rsaKeyPair;
    ChannelFuture f;

    public YggdrasilServerCore() throws Exception {
        this.port = Config.yggdrasil.getServer().getPort();
        instance = this;
        generalKey();
        new UserProvider();
        router.put("/", new BaseConfigController());
        router.put("/authserver/authenticate", new LoginController());
        router.put("/authserver/refresh", new RefreshController());
        router.put("/authserver/validate", new ValidateController());
        router.put("/authserver/invalidate", new InvalidateController());
        router.put("/sessionserver/session/minecraft/join", new JoinController());
        router.put("/sessionserver/session/minecraft/hasJoined", new HasJoinedController());
        router.put("/api/profiles/minecraft", new ProfileController());
        start();
    }

    private void generalKey() throws NoSuchAlgorithmException {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(4096, new SecureRandom());
        rsaKeyPair = gen.genKeyPair();
        rsaPublicKey = (RSAPublicKey) rsaKeyPair.getPublic();
        rsaPrivateKey = (RSAPrivateKey) rsaKeyPair.getPrivate();
    }

    public void start() throws Exception {
        new Thread(() -> {
            ServerBootstrap bootstrap = new ServerBootstrap();
            EventLoopGroup boss = new NioEventLoopGroup();
            EventLoopGroup work = new NioEventLoopGroup();
            bootstrap.group(boss, work)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new HttpServerInitializer());
            try {
                f = bootstrap.bind(new InetSocketAddress(port)).sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Helper.getLogger().info("server start up on port : " + port);
            try {
                f.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void stop() throws Exception {
        f.channel().close().sync();
    }

    public static class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel channel) throws Exception {
            ChannelPipeline pipeline = channel.pipeline();
            pipeline.addLast(new HttpServerCodec());// http 编解码
            pipeline.addLast("httpAggregator", new HttpObjectAggregator(512 * 1024)); // http 消息聚合器                                                                     512*1024为接收的最大contentlength
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
            QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
            if (router.containsKey(decoder.path())) {
                controller = router.get(decoder.path());
            } else if (decoder.path().startsWith("/sessionserver/session/minecraft/profile/")) {
                controller = new ProfileController();
            } else {
                controller = new NotFoundController();
            }
            Object msg = controller.handle(
                    new Gson().fromJson(req.content().toString(CharsetUtil.UTF_8), JsonElement.class),
                    req);
            FullHttpResponse response;
            if (msg == null) {
                response = new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1,
                        HttpResponseStatus.NO_CONTENT);
            } else if (msg instanceof FullHttpResponse) {
                response = (FullHttpResponse) msg;
            } else {
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

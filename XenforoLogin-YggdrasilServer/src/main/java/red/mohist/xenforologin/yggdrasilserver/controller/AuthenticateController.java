/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.yggdrasilserver.controller;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import red.mohist.xenforologin.core.forums.ForumSystems;
import red.mohist.xenforologin.yggdrasilserver.implementation.PlainPlayer;

public class AuthenticateController extends Controller {
    @Override
    public FullHttpResponse hanlde(FullHttpRequest request) {
        request.
        ForumSystems.getCurrentSystem().login(new PlainPlayer(req1));
        return null;
    }
}

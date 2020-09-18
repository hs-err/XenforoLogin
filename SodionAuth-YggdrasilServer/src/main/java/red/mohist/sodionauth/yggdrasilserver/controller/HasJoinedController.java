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

package red.mohist.sodionauth.yggdrasilserver.controller;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.handler.codec.http.*;
import red.mohist.sodionauth.yggdrasilserver.provider.SessionProvider;
import red.mohist.sodionauth.yggdrasilserver.provider.UserProvider;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class HasJoinedController implements Controller {
    @Override
    public Object handle(JsonElement content, FullHttpRequest request) throws SQLException {
        QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
        Map<String, List<String>> parameters=decoder.parameters();
        String username=parameters.get("username").get(0);
        String serverId=parameters.get("serverId").get(0);
        String ip=parameters.containsKey("ip")
                ? parameters.get("ip").get(0)
                : null;
        if(SessionProvider.instance.verify(username,serverId,ip)){
            return UserProvider.instance.getProfile("username");
        }
        return null;
    }
}

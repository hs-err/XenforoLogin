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

package red.mohist.sodionauth.yggdrasilserver.controller;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.handler.codec.http.FullHttpRequest;
import red.mohist.sodionauth.core.enums.ResultType;
import red.mohist.sodionauth.core.utils.Helper;
import red.mohist.sodionauth.yggdrasilserver.modules.RefreshRespone;
import red.mohist.sodionauth.yggdrasilserver.provider.UserProvider;

import java.sql.SQLException;

public class RefreshController implements Controller {
    @Override
    public Object handle(JsonElement content, FullHttpRequest requests) throws SQLException {
        JsonObject post = content.getAsJsonObject();
        String accessToken = post.get("accessToken").getAsString();
        String clientToken = post.get("clientToken") == null
                ? null
                : post.get("clientToken").getAsString();
        RefreshRespone respone = new RefreshRespone();
        ResultType result = UserProvider.instance.refreshToken(clientToken, accessToken);
        if (result != ResultType.OK) {
            Helper.getLogger().info("Login fail");
        }
        String username = (String) result.getInheritedObject("correct");
        accessToken = (String) result.getInheritedObject("accessToken");
        respone.setUser(UserProvider.instance.getUser(username))
                .setSelectedProfile(UserProvider.instance.getProfile(username))
                .setClientToken(clientToken)
                .setAccessToken(accessToken);
        return respone;
    }
}

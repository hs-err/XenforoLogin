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
import red.mohist.sodionauth.yggdrasilserver.modules.ErrorResponse;
import red.mohist.sodionauth.yggdrasilserver.modules.LoginRespone;
import red.mohist.sodionauth.yggdrasilserver.provider.UserProvider;

import java.sql.SQLException;
import java.util.UUID;

public class LoginController implements Controller {
    @Override
    public Object handle(JsonElement content, FullHttpRequest request) throws SQLException {
        JsonObject post = content.getAsJsonObject();
        String username = post.get("username").getAsString();
        String password = post.get("password").getAsString();
        String uuid = Helper.toStringUuid(username);
        String clientToken = post.get("clientToken") == null
                ? Helper.toStringUuid(UUID.randomUUID())
                : post.get("clientToken").getAsString();
        LoginRespone respone = new LoginRespone();
        ResultType result = UserProvider.instance.login(
                username,
                password,
                clientToken);
        if (result != ResultType.OK) {
            Helper.getLogger().info("Login fail");
            return null;
        }
        switch (result) {
            case NO_USER:
            case SERVER_ERROR:
            case PASSWORD_INCORRECT:
                return ErrorResponse.forbiddenOperation;
        }
        username = (String) result.getInheritedObject("correct");
        respone.setUser(UserProvider.instance.getUser(username))
                .addProfiles(UserProvider.instance.getProfile(username))
                .selectedProfile(UserProvider.instance.getProfile(username))
                .setClientToken(clientToken)
                .setAccessToken((String) result.getInheritedObject("accessToken"));
        return respone;
    }
}

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

package red.mohist.sodionauth.core.http.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import io.netty.handler.codec.http.FullHttpRequest;
import red.mohist.sodionauth.core.modules.Profile;
import red.mohist.sodionauth.core.provider.UserProvider;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProfilesController implements Controller {
    @Override
    public Object handle(JsonElement content, FullHttpRequest request) throws SQLException {
        JsonArray post = content.getAsJsonArray();
        if (post.size() > 10) {
            return null;
        }
        List<Profile> response = new ArrayList<>();
        for (JsonElement per : post) {
            response.add(UserProvider.instance.getProfile(per.getAsString()));
        }
        return response;
    }
}

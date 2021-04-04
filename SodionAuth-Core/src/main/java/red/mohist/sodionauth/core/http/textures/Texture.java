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

package red.mohist.sodionauth.core.http.textures;

import com.google.gson.Gson;
import red.mohist.sodionauth.core.modules.Property;
import red.mohist.sodionauth.core.modules.Textures;

import java.util.Base64;

public abstract class Texture {
    public Property getPropertie(String username) {
        return new Property("textures",
                Base64.getEncoder().encodeToString(
                        new Gson().toJson(getTextures(username)).getBytes()
                ));
    }

    public abstract Textures getTextures(String username);
}

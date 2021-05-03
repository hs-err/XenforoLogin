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

package red.mohist.sodionauth.core.authbackends;

import red.mohist.sodionauth.core.authbackends.implementations.SodionApi;
import red.mohist.sodionauth.core.authbackends.implementations.XenforoApi;
import red.mohist.sodionauth.core.utils.Config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuthBackends {
    public static Map<String, AuthBackend> authBackendMap = new ConcurrentHashMap<>();

    static {
        Config.api.xenforo.forEach((key, bean) -> {
            authBackendMap.put("xenforo:" + key, new XenforoApi(bean));
        });
        Config.api.web.forEach((key, bean) -> {
            authBackendMap.put("web:" + key, new SodionApi(bean));
        });
    }

    public static AuthBackend getByName(String typeName) {
        return authBackendMap.get(typeName);
    }
}

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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.yggdrasilserver.YggdrasilServerCore;
import red.mohist.sodionauth.yggdrasilserver.modules.RequestConfig;

import java.util.Base64;

public class BaseConfigController implements Controller {
    @Override
    public Object handle(JsonObject content) {
        RequestConfig requestConfig=new RequestConfig();
        requestConfig
                .addMeta("serverName", Config.yggdrasil.getCore().getServerName("SodionAuthYggdrasilServer"))
                .addMeta("implementationName","SodionAuthYggdrasilServer")
                .addMeta("implementationVersion","testTEST");
        Config.yggdrasil.getCore().getSkinDomains().forEach(requestConfig::addSkinDomains);
        requestConfig.setSignaturePublickey("-----BEGIN PUBLIC KEY-----\n" +
                Base64.getMimeEncoder(76, new byte[] { '\n' }).encodeToString(YggdrasilServerCore.instance.rsaPublicKey.getEncoded()) +
                "\n-----END PUBLIC KEY-----\n");
        return requestConfig;
    }
}

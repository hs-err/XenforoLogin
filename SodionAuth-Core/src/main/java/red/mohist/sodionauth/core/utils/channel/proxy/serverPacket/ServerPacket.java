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

package red.mohist.sodionauth.core.utils.channel.proxy.serverPacket;

import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.Helper;
import red.mohist.sodionauth.core.utils.channel.proxy.ProxyPacket;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public abstract class ServerPacket extends ProxyPacket {
    public byte[] pack(){
        byte[] n = encode();
        byte[] m = Helper.merge(n, Config.bungee.serverKey.getBytes(StandardCharsets.UTF_8));
        byte[] s = Helper.sha256(m);
        return Helper.merge(m,s);
    }
    protected boolean verify(byte[] r){
        byte[] n = encode();
        byte[] m = Helper.merge(n, Config.bungee.clientKey.getBytes(StandardCharsets.UTF_8));
        byte[] s = Helper.sha256(m);
        return Arrays.equals(Helper.merge(m,s), r);
    }
}

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

package red.mohist.sodionauth.core.utils.channel.proxy;

import com.google.common.collect.ImmutableMap;
import red.mohist.sodionauth.core.utils.channel.proxy.clientPacket.ClientPacket;
import red.mohist.sodionauth.core.utils.channel.proxy.clientPacket.HelloServerPacket;
import red.mohist.sodionauth.core.utils.channel.proxy.clientPacket.LoginSuccessPacket;
import red.mohist.sodionauth.core.utils.channel.proxy.serverPacket.ServerPacket;
import red.mohist.sodionauth.core.utils.channel.proxy.serverPacket.ShakeTokenPacket;

import java.nio.ByteBuffer;
import java.util.Map;

public class ProxyChannel {
    public static final String name = "sodionauth:proxy";
    public static final Map<Integer,Class<? extends ClientPacket>> clientPackets =
            new ImmutableMap.Builder<Integer,Class<? extends ClientPacket>>()
                    .put(0, HelloServerPacket.class)
                    .put(1, LoginSuccessPacket.class)
                    .build();
    public static final Map<Integer,Class<? extends ServerPacket>> serverPackets =
            new ImmutableMap.Builder<Integer,Class<? extends ServerPacket>>()
                    .put(0, ShakeTokenPacket.class)
                    .build();

    public static ClientPacket parserClient(byte[] data) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(data);
            Class<? extends ClientPacket> clazz = clientPackets.get(buffer.getInt());
            return clazz.getConstructor(byte[].class).newInstance((Object) data);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ServerPacket parserServer(byte[] data) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(data);
            Class<? extends ServerPacket> clazz = serverPackets.get(buffer.getInt());
            return clazz.getConstructor(byte[].class).newInstance((Object) data);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
}

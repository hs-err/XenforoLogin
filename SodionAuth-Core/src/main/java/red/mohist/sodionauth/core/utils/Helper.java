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

package red.mohist.sodionauth.core.utils;

import com.eloli.sodioncore.file.BaseFileService;
import com.eloli.sodioncore.logger.AbstractLogger;
import com.google.gson.JsonElement;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Helper {
    public static Helper instance;

    public final BaseFileService baseFileService;
    public final AbstractLogger log;
    public final Map<String, JsonElement> jsonMap;

    public Helper(BaseFileService baseFileService, AbstractLogger log, com.eloli.sodioncore.dependency.DependencyManager dependencyManager) throws Exception {
        instance = this;
        this.baseFileService = baseFileService;

        this.log = log;
        jsonMap = new HashMap<>();
        Config.init();
        Lang.init();
        Collection<String> dependencies = new ArrayList<>();
        dependencies.add("com.google.guava:guava:29.0-jre:com.google.common.eventbus.EventBus");
        dependencies.add("me.gosimple:nbvcxz:1.5.0:me.gosimple.nbvcxz.Nbvcxz");
        dependencies.add("com.maxmind.geoip2:geoip2:2.14.0:com.maxmind.geoip2.DatabaseReader");
        dependencies.add("org.mindrot:jbcrypt:0.4:org.mindrot.jbcrypt.BCrypt");
        dependencies.add("io.netty:netty-all:4.1.50.Final:io.netty.bootstrap.ServerBootstrap");
        dependencies.add("org.apache.httpcomponents:fluent-hc:4.5.11:org.apache.http.client.fluent.Request");
        for (String dependency : dependencies) {
            String[] spilt = dependency.split(":");
            dependencyManager.checkDependencyMaven(dependency);
        }
    }

    public static String getConfigPath(String filename) {
        return Helper.instance.baseFileService.getConfigPath(filename);
    }

    public static AbstractLogger getLogger() {
        return Helper.instance.log;
    }

    public static UUID getUuidFromName(String name) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
    }

    public static String toStringUuid(UUID uuid) {
        String str = uuid.toString();
        return str.substring(0, 8) + str.substring(9, 13) + str.substring(14, 18) + str.substring(19, 23) + str.substring(24);
    }

    public static UUID fromStringUuid(String data) {
        return UUID.fromString(data.substring(0, 8) + "-" + data.substring(8, 12) + "-" + data.substring(12, 16) + "-" + data.substring(16, 20) + "-" + data.substring(20));
    }

    public static String toStringUuid(String name) {
        return toStringUuid(getUuidFromName(name));
    }

    public static String readBuffer(ByteBuffer buffer, int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(buffer.getChar());
        }
        return builder.toString();
    }

    public static void putBuffer(ByteBuffer buffer, int length, String str) {
        for (int i = 0; i < length; i++) {
            buffer.putChar(str.charAt(i));
        }
    }

    public static byte[] merge(byte[] a, byte[] b) {
        byte[] c = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public static byte[] sha256(byte[] a) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(a);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveResource(String resourcePath, boolean replace) throws IOException {
        baseFileService.saveResource(resourcePath, replace);
    }
}

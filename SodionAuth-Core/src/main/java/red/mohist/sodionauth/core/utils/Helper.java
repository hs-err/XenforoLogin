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

import com.google.gson.JsonElement;
import red.mohist.sodionauth.core.modules.LogProvider;
import red.mohist.sodionauth.core.utils.dependency.DependencyManager;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Helper {
    public static Helper instance;

    public final String basePath;
    public final LogProvider log;
    public final Map<String, JsonElement> jsonMap;

    public Helper(String path, LogProvider log) throws IOException {
        instance = this;
        this.log = log;
        basePath = path;
        jsonMap = new HashMap<>();
        Config.init();
        Lang.init();
        Collection<String> dependencies = new ArrayList<>();
        dependencies.add("com.google.guava:guava:29.0-jre");
        dependencies.add("me.gosimple:nbvcxz:1.5.0");
        dependencies.add("org.reflections:reflections:0.9.12");
        dependencies.add("com.google.code.findbugs:jsr305:3.0.2");
        dependencies.add("com.maxmind.geoip2:geoip2:2.14.0");
        dependencies.add("org.mindrot:jbcrypt:0.4");
        dependencies.add("io.netty:netty-all:4.1.50.Final");
        dependencies.add("com.blinkfox:zealot:1.3.1");
        dependencies.add("org.apache.httpcomponents:fluent-hc:4.5.11");
        dependencies.add("org.xerial:sqlite-jdbc:3.34.0");
        dependencies.add("org.teasoft:bee:1.8.99");
        dependencies.add("org.teasoft:honey:1.8.99");
        dependencies.add("org.teasoft:bee-ext:1.8.99");
        switch (Config.database.type) {
            case "sqlite":
                dependencies.add("org.xerial:sqlite-jdbc:3.34.0");
                break;
            case "mysql":
                dependencies.add("mysql:mysql-connector-java:8.0.24");
                break;
            case "h2":
                dependencies.add("com.h2database:h2:1.4.200");
                break;
        }
        for (String dependency : dependencies) {
            String[] spilt = dependency.split(":");
            DependencyManager.checkDependencyMaven(spilt[0], spilt[1], spilt[2]);
        }
    }

    public static String getConfigPath(String filename) {
        return Paths.get(instance.basePath, filename).toString();
    }

    public static LogProvider getLogger() {
        return Helper.instance.log;
    }

    public static UUID getUuidFromName(String name) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
    }

    public static String toStringUuid(UUID uuid) {
        String str = uuid.toString();
        return str.substring(0, 8) + str.substring(9, 13) + str.substring(14, 18) + str.substring(19, 23) + str.substring(24);
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

    public void saveResource(String resourcePath, boolean replace) {
        if (resourcePath == null || resourcePath.equals("")) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResource(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found");
        }

        File outFile = new File(basePath, resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(basePath, resourcePath.substring(0, Math.max(lastIndex, 0)));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists() || replace) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            }
        } catch (IOException ex) {
            Helper.getLogger().warn("Could not save " + outFile.getName() + " to " + outFile, ex);
        }
    }

    public InputStream getResource(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }

        try {
            URL url = this.getClass().getClassLoader().getResource(filename);
            if (url == null) {
                return null;
            }
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }
}

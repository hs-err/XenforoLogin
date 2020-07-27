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

package red.mohist.sodionauth.core.dependency;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import red.mohist.sodionauth.core.SodionAuthCore;
import red.mohist.sodionauth.core.dependency.classloader.ReflectionClassLoader;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.Helper;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DependencyManager {

    private static final ReflectionClassLoader reflectionClassLoader = new ReflectionClassLoader();

    public static void checkForSQLite() {
        Helper.getLogger().info("Checking if SQLite library is present...");
        try {
            Class.forName("org.sqlite.JDBC");
            Helper.getLogger().info("SQLite library present");
            return;
        } catch (ClassNotFoundException e) {
            Helper.getLogger().warn("Cannot find sqlite library");
        } catch (Exception e) {
            Helper.getLogger().warn("Cannot load sqlite library");
        }

        File librariesPath = new File(Helper.getConfigPath("libraries"));
        librariesPath.mkdirs();

        File SQLiteLib = librariesPath.toPath().resolve("sqlite-jdbc-3.30.1.jar").toFile();

        if (!SQLiteLib.isFile())
            try {
                Helper.getLogger().info("Downloading SQLite library...");
                downloadFile(SQLiteLib, "org/xerial/sqlite-jdbc/3.30.1/sqlite-jdbc-3.30.1.jar");
            } catch (IOException e) {
                Helper.getLogger().warn("Unable to download SQLite library", e);
            }
        else
            Helper.getLogger().info("SQLite library file present");

        boolean matchChecksum = isMatchChecksum(SQLiteLib,
                "org/xerial/sqlite-jdbc/3.30.1/sqlite-jdbc-3.30.1.jar.sha1");

        if (matchChecksum) {
            Helper.getLogger().info("Checksum matched, loading it into memory...");
            reflectionClassLoader.addJarToClasspath(SQLiteLib.toPath());
        } else
            Helper.getLogger().warn("Checksum not matched");

        DependencyManager.checkForSQLite();

    }

    public static void checkForMySQL() {
        Helper.getLogger().info("Checking if MySQL library is present...");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Helper.getLogger().info("MySQL library present");
            return;
        } catch (ClassNotFoundException e) {
            Helper.getLogger().warn("Cannot find MySQL library");
        } catch (Exception e) {
            Helper.getLogger().warn("Cannot load MySQL library");
        }

        File librariesPath = new File(Helper.getConfigPath("libraries"));
        librariesPath.mkdirs();

        File MySQLib = librariesPath.toPath().resolve("mysql-connector-java-8.0.21.jar").toFile();

        if (!MySQLib.isFile())
            try {
                downloadFile(MySQLib, "mysql/mysql-connector-java/8.0.21/mysql-connector-java-8.0.21.jar");
            } catch (IOException e) {
                Helper.getLogger().warn("Unable to download MySQL library", e);
                throw new RuntimeException(e);
            }

        boolean matchChecksum = isMatchChecksum(MySQLib,
                "omysql/mysql-connector-java/8.0.21/mysql-connector-java-8.0.21.jar.sha1");

        if (matchChecksum) {
            Helper.getLogger().info("Checksum matched, loading it into memory...");
            reflectionClassLoader.addJarToClasspath(MySQLib.toPath());
        } else
            Helper.getLogger().warn("Checksum not matched");

        DependencyManager.checkForMySQL();

    }

    private static boolean isMatchChecksum(File file, String url) {
        Helper.getLogger().info("Checking file checksum...");
        boolean matchChecksum = false;
        try {
            String checksum = createSha1(file);
            HttpGet request = new HttpGet(Config.dependencies.getMavenRepository() +
                    url);
            CloseableHttpResponse response = SodionAuthCore.instance.getHttpClient().execute(request);
            final StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() < 200 || statusLine.getStatusCode() >= 300)
                throw new IOException(statusLine.getStatusCode() + " " + statusLine.getReasonPhrase());
            final InputStream content = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
            StringWriter writer = new StringWriter();
            char[] buffer = new char[40];
            if (reader.read(buffer) < 40) throw new IOException();
            writer.write(buffer);
            reader.close();
            content.close();
            String targetChecksum = writer.toString();
            writer.close();
            if (targetChecksum.equals(checksum))
                matchChecksum = true;
            else
                file.delete();
        } catch (IOException e) {
            Helper.getLogger().warn("Error while checking checksum, skipping...", e);
            matchChecksum = true;
        }
        return matchChecksum;
    }

    private static void downloadFile(File file, String url) throws IOException {
        Helper.getLogger().warn("Downloading...");
        HttpGet request = new HttpGet(Config.dependencies.getMavenRepository() +
                url);
        CloseableHttpResponse response = SodionAuthCore.instance.getHttpClient().execute(request);
        final StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() < 200 || statusLine.getStatusCode() >= 300)
            throw new IOException(statusLine.getStatusCode() + " " + statusLine.getReasonPhrase());
        OutputStream fileOut = new FileOutputStream(file);
        response.getEntity().writeTo(fileOut);
        fileOut.flush();
        fileOut.close();
        response.close();
        Helper.getLogger().info("Downloaded successfully");
    }

    private static String createSha1(File file) throws IOException {
        MessageDigest sha1 = null;
        try {
            sha1 = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        try (InputStream input = new FileInputStream(file)) {

            byte[] buffer = new byte[8192];
            int len = input.read(buffer);

            while (len != -1) {
                sha1.update(buffer, 0, len);
                len = input.read(buffer);
            }

            return Hex.encodeHexString(sha1.digest());
        }
    }

}
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

import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import red.mohist.sodionauth.core.dependency.classloader.ReflectionClassLoader;
import red.mohist.sodionauth.core.utils.Helper;

import java.io.File;
import java.io.IOException;

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
                Helper.getLogger().warn("Downloading...");
                final Response httpResponse = Request
                        .Get("https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.30.1/" +
                                "sqlite-jdbc-3.30.1.jar")
                        .execute();
                httpResponse.saveContent(SQLiteLib);
                Helper.getLogger().info("Downloaded successfully");
            } catch (IOException e) {
                Helper.getLogger().warn("Unable to download SQLite library", e);
                throw new RuntimeException(e);
            }
        Helper.getLogger().info("Loading it into memory...");

        reflectionClassLoader.addJarToClasspath(SQLiteLib.toPath());

        DependencyManager.checkForSQLite();

    }

}
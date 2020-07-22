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

package red.mohist.sodionauth.core.utils;

import com.google.gson.Gson;
import red.mohist.sodionauth.core.config.MainConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Config {
    public static MainConfiguration instance;
    public static String defaultLang;
    public static MainConfiguration.ApiConfiguration api;
    public static MainConfiguration.SessionConfiguration session;
    public static MainConfiguration.YggdrasilConfiguration yggdrasil;
    public static MainConfiguration.SpawnConfiguration spawn;
    public static MainConfiguration.TeleportConfiguration teleport;
    public static MainConfiguration.SecurityConfiguration security;
    public static MainConfiguration.ProtectionConfiguration protection;
    public Config() throws IOException {
        Helper.instance.saveResource("config.json", false);
        File configFile = new File(Helper.getConfigPath("config.json"));
        FileInputStream fileReader = new FileInputStream(configFile);
        InputStreamReader inputStreamReader = new InputStreamReader(fileReader, StandardCharsets.UTF_8);
        instance = new Gson().fromJson(inputStreamReader, MainConfiguration.class);
        inputStreamReader.close();
        fileReader.close();

        defaultLang=instance.defaultLang;
        api=instance.api;
        session=instance.session;
        yggdrasil=instance.yggdrasil;
        spawn=instance.spawn;
        teleport=instance.teleport;
        security=instance.security;
        protection=instance.protection;
    }
}

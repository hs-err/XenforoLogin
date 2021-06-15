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

import com.eloli.sodioncore.config.ConfigureService;
import red.mohist.sodionauth.core.config.MainConfiguration;

public class Config {
    public static String defaultLang;
    public static String serverId;
    public static MainConfiguration.DatabaseBean database;
    public static MainConfiguration.ApiBean api;
    public static MainConfiguration.PasswordBean password;
    public static MainConfiguration.BungeeBean bungee;
    public static MainConfiguration.DependenciesBean dependencies;
    public static MainConfiguration.SessionBean session;
    public static MainConfiguration.SpawnBean spawn;
    public static MainConfiguration.TeleportBean teleport;
    public static MainConfiguration.SecurityBean security;
    public static MainConfiguration.ProtectionBean protection;
    private static ConfigureService<MainConfiguration> configureService;

    public static void init() throws Exception {
        configureService = new ConfigureService<>(Helper.instance.baseFileService, "config.json");
        configureService.register(null, MainConfiguration.class);
        configureService.init();

        defaultLang = configureService.instance.defaultLang;
        serverId = configureService.instance.serverId;
        database = configureService.instance.database;
        api = configureService.instance.api;
        password = configureService.instance.password;
        bungee = configureService.instance.bungee;
        dependencies = configureService.instance.dependencies;
        session = configureService.instance.session;
        spawn = configureService.instance.spawn;
        teleport = configureService.instance.teleport;
        security = configureService.instance.security;
        protection = configureService.instance.protection;
    }
}

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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import red.mohist.sodionauth.core.SodionAuthCore;
import red.mohist.sodionauth.core.config.*;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("unused")
public class Config {
    public static final int CURRENT_VERSION = 2;
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
    protected static MainConfiguration instance;
    protected static Gson gson;

    public static void init() throws IOException {
        Helper.getLogger().info("load");
        GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation();
        builder.setFieldNamingStrategy(field -> {
            String name = field.getName();
            Name nameAnnotation = field.getAnnotation(Name.class);
            if (nameAnnotation != null) {
                if (nameAnnotation.plain()) {
                    return nameAnnotation.value();
                } else {
                    name = nameAnnotation.value();
                }
            }
            StringBuilder translation = new StringBuilder();
            int i = 0;
            for (int length = name.length(); i < length; ++i) {
                char character = name.charAt(i);
                if (Character.isUpperCase(character) && translation.length() != 0) {
                    translation.append(" ");
                }
                translation.append(character);
            }
            return translation.toString().toLowerCase();
        });
        builder.serializeNulls();
        builder.setPrettyPrinting();
        builder.disableHtmlEscaping();
        gson = builder.create();
        Helper.instance.saveResource("probeResource", true);
        try {
            File configFile = new File(Helper.getConfigPath("config.json"));
            FileInputStream fileReader = new FileInputStream(configFile);
            InputStreamReader inputStreamReader = new InputStreamReader(fileReader, StandardCharsets.UTF_8);
            JsonObject result = gson.fromJson(inputStreamReader, JsonObject.class);
            inputStreamReader.close();
            fileReader.close();
            int version = result.get("version").getAsInt();
            if (version < CURRENT_VERSION) {
                instance = (MainConfiguration) migrate(version, gson.fromJson(result, getConfigure(version)));
            } else {
                instance = gson.fromJson(result, MainConfiguration.class);
            }
        } catch (FileNotFoundException e) {
            instance = new MainConfiguration();
        } catch (Exception e) {
            Helper.getLogger().warn("Can't read Configure file", e);
            SodionAuthCore.instance.loadFail();
            return;
        }

        try {
            save();
        } catch (Exception e) {
            Helper.getLogger().warn("Can't save Configure file", e);
            SodionAuthCore.instance.loadFail();
            return;
        }

        defaultLang = instance.defaultLang;
        serverId = instance.serverId;
        database = instance.database;
        api = instance.api;
        password = instance.password;
        bungee = instance.bungee;
        dependencies = instance.dependencies;
        session = instance.session;
        spawn = instance.spawn;
        teleport = instance.teleport;
        security = instance.security;
        protection = instance.protection;
    }

    private static Configure migrate(int fromVersion, Configure configure) throws IOException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException, InstantiationException {
        if (fromVersion == CURRENT_VERSION) {
            return configure;
        }
        Migrater migrater = getMigrater(fromVersion).newInstance();
        Configure result = getConfigure(fromVersion + 1).newInstance();
        replace(result, configure);
        migrater.migrate(configure, result);
        return migrate(fromVersion + 1, result);
    }

    private static Configure replace(Configure configure, Configure getter) throws IllegalAccessException, NoSuchFieldException {
        for (Field field : configure.getClass().getFields()) {
            Class<?> type = field.getType();
            if (Configure.class.isAssignableFrom(type)) {
                field.set(configure, replace((Configure) field.get(configure), getter));
            } else {
                Migrate annotation = field.getAnnotation(Migrate.class);
                if (annotation != null) {
                    field.set(configure, getHistoryValue(getter, annotation.value()));
                }
            }
        }
        return configure;
    }

    private static Object getHistoryValue(Configure getter, String path) throws NoSuchFieldException, IllegalAccessException {
        String[] paths = path.split("\\.");
        if (paths.length == 1) {
            return getter.getClass().getField(paths[0]).get(getter);
        } else {
            StringBuilder pathBuilder = new StringBuilder();
            for (int i = 1; i < paths.length; i++) {
                pathBuilder.append(".").append(paths[i]);
            }
            return getHistoryValue(
                    (Configure) getter.getClass().getField(paths[0]).get(getter),
                    pathBuilder.substring(1)
            );
        }
    }

    protected static Class<? extends Migrater> getMigrater(int version) throws ClassNotFoundException {
        return (Class<? extends Migrater>)
                Class.forName("red.mohist.sodionauth.core.config.migrates.MigrateFrom" + version);
    }

    protected static Class<? extends Configure> getConfigure(int version) throws ClassNotFoundException {
        if (version == CURRENT_VERSION) {
            return MainConfiguration.class;
        } else {
            return (Class<? extends Configure>)
                    Class.forName("red.mohist.sodionauth.core.config.migrates.MainConfiguration" + version);
        }
    }

    protected static void save() throws IOException, IllegalAccessException {
        File configFile = new File(Helper.getConfigPath("config.json"));
        FileOutputStream fileWriter = new FileOutputStream(configFile);

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileWriter, StandardCharsets.UTF_8);
        outputStreamWriter.write(gson.toJson(saveValue(instance)));
        outputStreamWriter.flush();
        outputStreamWriter.close();
        fileWriter.flush();
        fileWriter.close();
    }

    protected static JsonObject saveValue(Configure configure) throws IllegalAccessException {
        JsonObject result = new JsonObject();
        for (Field field : configure.getClass().getFields()) {
            String translatedName = gson.fieldNamingStrategy().translateName(field);
            Lores lores = field.getAnnotation(Lores.class);
            if (lores != null) {
                for (int i = 0; i < lores.value().length; i++) {
                    result.addProperty(
                            "_" + translatedName + "_" + i,
                            lores.value()[i].value());
                }
            } else {
                Lore lore = field.getAnnotation(Lore.class);
                if (lore != null) {
                    result.addProperty("_" + translatedName, lore.value());
                }
            }
            if (Configure.class.isAssignableFrom(field.getType())) {
                result.add(field.getName(), saveValue((Configure) field.get(configure)));
            } else if (field.getType().isArray()) {
                JsonArray array = new JsonArray();
                if (Configure.class.isAssignableFrom(field.getType().getComponentType())) {
                    for (Configure inner : (Configure[]) field.get(configure)) {
                        array.add(saveValue(inner));
                    }
                } else {
                    for (Object object : (Object[]) field.get(configure)) {
                        array.add(gson.toJsonTree(object));
                    }
                }
            } else {
                result.add(translatedName, gson.toJsonTree(field.get(configure)));
            }
        }
        return result;
    }
}

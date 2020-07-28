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

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import red.mohist.sodionauth.core.config.LangConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Lang {
    public static final ConcurrentHashMap<String, LangConfiguration> languages = new ConcurrentHashMap<>();
    public static LangConfiguration def;
    public static LangConfiguration all;

    public static LangConfiguration get(String name) {
        LangConfiguration lang = languages.getOrDefault(name, null);
        if (lang == null) {
            lang = def;
        }
        return lang;
    }

    public static void init() throws IOException {
        ArrayList<String> languageList = new ArrayList<>(ImmutableList.of(
                "en","zh-CN"
        ));
        for (String language : languageList) {
            Helper.instance.saveResource("lang/" + language + ".json", false);
            File configFile = new File(Helper.getConfigPath("lang/" + language + ".json"));
            FileInputStream fileReader = new FileInputStream(configFile);
            InputStreamReader inputStreamReader = new InputStreamReader(fileReader, StandardCharsets.UTF_8);
            languages.put(language, new Gson().fromJson(inputStreamReader, LangConfiguration.class));
            inputStreamReader.close();
            fileReader.close();
        }
        def = languages.get(Config.defaultLang);
        all = languages.get("en");
    }
}

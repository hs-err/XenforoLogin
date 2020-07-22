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

import com.google.gson.JsonElement;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

public class Config {
    public static Config instance;
    public Map<String, JsonElement> jsonMap;

    public Config(Map<String, JsonElement> jsonMap) throws IOException {
        instance = this;
        this.jsonMap = jsonMap;
    }

    public static JsonElement getConfig() {
        return Config.getConfig(".");
    }

    public static String getString(String key) {
        return getConfig(key).getAsString();
    }

    private static JsonElement getConfig(String key) {
        return Config.instance.jsonMap.get("." + key);
    }

    private static boolean containsKey(String key) {
        return getConfig(key) != null;
    }

    public static String getString(String key, String def) {
        try {
            return containsKey(key)
                    ? getConfig(key).getAsString()
                    : def;
        } catch (Exception e) {
            return def;
        }
    }

    public static Integer getInteger(String key) {
        return getConfig(key).getAsInt();
    }

    public static Integer getInteger(String key, Integer def) {
        try {
            return containsKey(key)
                    ? getConfig(key).getAsInt()
                    : def;
        } catch (Exception e) {
            return def;
        }
    }

    public static BigInteger getBigInteger(String key) {
        return getConfig(key).getAsBigInteger();
    }

    public static BigInteger getInteger(String key, BigInteger def) {
        try {
            return containsKey(key)
                    ? getConfig(key).getAsBigInteger()
                    : def;
        } catch (Exception e) {
            return def;
        }
    }

    public static Double getDouble(String key) {
        return getConfig(key).getAsDouble();
    }

    public static Double getDouble(String key, Double def) {
        try {
            return containsKey(key)
                    ? getConfig(key).getAsDouble()
                    : def;
        } catch (Exception e) {
            return def;
        }
    }

    public static BigDecimal getBigDecimal(String key) {
        return getConfig(key).getAsBigDecimal();
    }

    public static BigDecimal getBigDecimal(String key, BigDecimal def) {
        try {
            return containsKey(key)
                    ? getConfig(key).getAsBigDecimal()
                    : def;
        } catch (Exception e) {
            return def;
        }
    }

    public static Float getFloat(String key) {
        return getConfig(key).getAsFloat();
    }

    public static Float getFloat(String key, Float def) {
        try {
            return containsKey(key)
                    ? getConfig(key).getAsFloat()
                    : def;
        } catch (Exception e) {
            return def;
        }
    }

    public static Boolean getBoolean(String key) {
        return getConfig(key).getAsBoolean();
    }

    public static Boolean getBoolean(String key, Boolean def) {
        try {
            return containsKey(key)
                    ? getConfig(key).getAsBoolean()
                    : def;
        } catch (Exception e) {
            return def;
        }
    }

    public static Byte getByte(String key) {
        return getConfig(key).getAsByte();
    }

    public static Byte getByte(String key, Byte def) {
        try {
            return containsKey(key)
                    ? getConfig(key).getAsByte()
                    : def;
        } catch (Exception e) {
            return def;
        }
    }

    public static Long getLong(String key) {
        return getConfig(key).getAsLong();
    }

    public static Long getLong(String key, Long def) {
        try {
            return containsKey(key)
                    ? getConfig(key).getAsLong()
                    : def;
        } catch (Exception e) {
            return def;
        }
    }

    public static Number getNumber(String key) {
        return getConfig(key).getAsNumber();
    }

    public static Number getNumber(String key, Number def) {
        try {
            return containsKey(key)
                    ? getConfig(key).getAsNumber()
                    : def;
        } catch (Exception e) {
            return def;
        }
    }
}

/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.core.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.codec.Charsets;
import red.mohist.xenforologin.core.interfaces.LogProvider;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Config {
    public static Config instance;
    public Map<String, JsonElement> jsonMap;
    public Config(Map<String, JsonElement> jsonMap) throws IOException {
        instance=this;
        this.jsonMap=jsonMap;
    }

    public static JsonElement getConfig(){
        return Config.getConfig(".");
    }
    public static JsonElement getConfig(String key){
        return Config.instance.jsonMap.get("."+key);
    }

    public static String getString(String key){
        return Config.instance.jsonMap.get("." + key).getAsString();
    }
    public static String getString(String key,String def){
        return Config.instance.jsonMap.containsKey("." + key)
                ? Config.instance.jsonMap.get("." + key).getAsString()
                :def;
    }

    public static Integer getInteger(String key){
        return Config.instance.jsonMap.get("." + key).getAsInt();
    }
    public static Integer getInteger(String key,Integer def){
        return Config.instance.jsonMap.containsKey("." + key)
                ? Config.instance.jsonMap.get("." + key).getAsInt()
                :def;
    }

    public static BigInteger getBigInteger(String key){
        return Config.instance.jsonMap.get("." + key).getAsBigInteger();
    }
    public static BigInteger getInteger(String key,BigInteger def){
        return Config.instance.jsonMap.containsKey("." + key)
                ? Config.instance.jsonMap.get("." + key).getAsBigInteger()
                :def;
    }

    public static Double getDouble(String key){
        return Config.instance.jsonMap.get("." + key).getAsDouble();
    }
    public static Double getDouble(String key,Double def){
        return Config.instance.jsonMap.containsKey("." + key)
                ? Config.instance.jsonMap.get("." + key).getAsDouble()
                :def;
    }

    public static BigDecimal getBigDecimal(String key){
        return Config.instance.jsonMap.get("." + key).getAsBigDecimal();
    }
    public static BigDecimal getBigDecimal(String key, BigDecimal def){
        return Config.instance.jsonMap.containsKey("." + key)
                ? Config.instance.jsonMap.get("." + key).getAsBigDecimal()
                :def;
    }

    public static Float getFloat(String key){
        return Config.instance.jsonMap.get("." + key).getAsFloat();
    }
    public static Float getFloat(String key, Float def){
        return Config.instance.jsonMap.containsKey("." + key)
                ? Config.instance.jsonMap.get("." + key).getAsFloat()
                :def;
    }

    public static Boolean getBoolean(String key){
        return Config.instance.jsonMap.get("." + key).getAsBoolean();
    }
    public static Boolean getBoolean(String key, Boolean def){
        return Config.instance.jsonMap.containsKey("." + key)
                ? Config.instance.jsonMap.get("." + key).getAsBoolean()
                :def;
    }

    public static Byte getByte(String key){
        return Config.instance.jsonMap.get("." + key).getAsByte();
    }
    public static Byte getByte(String key, Byte def){
        return Config.instance.jsonMap.containsKey("." + key)
                ? Config.instance.jsonMap.get("." + key).getAsByte()
                :def;
    }

    public static Long getLong(String key){
        return Config.instance.jsonMap.get("." + key).getAsLong();
    }
    public static Long getLong(String key, Long def){
        return Config.instance.jsonMap.containsKey("." + key)
                ? Config.instance.jsonMap.get("." + key).getAsLong()
                :def;
    }

    public static Number getNumber(String key){
        return Config.instance.jsonMap.get("." + key).getAsNumber();
    }
    public static Number getNumber(String key, Number def){
        return Config.instance.jsonMap.containsKey("." + key)
                ? Config.instance.jsonMap.get("." + key).getAsLong()
                :def;
    }
}

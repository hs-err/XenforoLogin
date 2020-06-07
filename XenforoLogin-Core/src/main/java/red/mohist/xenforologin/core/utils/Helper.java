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

public class Helper {
    public static Helper instance;

    public String basePath;
    public LogProvider log;
    public Map<String, JsonElement> jsonMap;
    public Helper(String path, LogProvider log) throws IOException {
        instance=this;
        this.log=log;
        basePath=path;
        jsonMap= new HashMap<>();
        saveResource("config.json",false);

        File configFile=new File(basePath+"/config.json");
        Reader reader=new FileReader(configFile);
        JsonElement json=new Gson().fromJson(reader,JsonElement.class);
        generalConfigMap("",json);

        Reader readerDefault=getTextResource("config.json");
        JsonElement jsonDefault=new Gson().fromJson(readerDefault,JsonElement.class);
        generalConfigMap("",jsonDefault);

        new Config(jsonMap);
    }

    protected final Reader getTextResource(String file) {
        final InputStream in = getResource(file);

        return in == null ? null : new InputStreamReader(in, Charsets.UTF_8);
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
            } else {
                Helper.getLogger().warn( "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
            Helper.getLogger().warn( "Could not save " + outFile.getName() + " to " + outFile, ex);
        }
    }

    public InputStream getResource(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }

        try {
            URL url = this.getClass().getClassLoader().getResource(filename);
            Helper.getLogger().info(url.toString());
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

    private void generalConfigMap(String key, JsonElement data){
        if(data.isJsonNull()){
            if(!jsonMap.containsKey(key.equals("")?".":key)){
                jsonMap.put(key.equals("")?".":key,null);
            }
        }
        if(data.isJsonArray()){
            if(!jsonMap.containsKey(key.equals("")?".":key)) {
                JsonArray jsonArray = data.getAsJsonArray();
                jsonMap.put(key.equals("") ? "." : key, data.getAsJsonArray());
                for (int i = 0; i < jsonArray.size(); i++) {
                    generalConfigMap((key.equals("") ? "." : key) + "[" + i + "]", jsonArray.get(i));
                }
            }
        }
        if (data.isJsonObject())
        {
            JsonObject jsonBase = new JsonObject();
            Set<Map.Entry<String, JsonElement>> jsonObject = data.getAsJsonObject().entrySet();
            for (Map.Entry<String, JsonElement> jsonData : jsonObject){
                generalConfigMap(key + "." + jsonData.getKey(), jsonData.getValue());
                jsonBase.add(jsonData.getKey(),jsonData.getValue());
            }
            jsonMap.put(key.equals("")?".":key,jsonBase.getAsJsonObject());
        }
        if (data.isJsonPrimitive())
        {
            if(!jsonMap.containsKey(key.equals("")?".":key)){
                jsonMap.put(key.equals("")?".":key,data.getAsJsonPrimitive());
            }
        }
    }
    public static LogProvider getLogger(){
        return Helper.instance.log;
    }

    public static String langFile(String key) {
        String result = Config.getString("lang." + key);
        if (result == null) {
            return key;
        }
        return result;
    }

    public static String langFile(String key, Map<String, String> data) {
        String result = Config.getString("lang." + key);
        if (result == null) {
            StringBuilder resultBuilder = new StringBuilder(key);
            resultBuilder.append("\n");
            for (Map.Entry<String, String> entry : data.entrySet()) {
                resultBuilder.append(entry.getKey()).append(":").append(entry.getValue());
            }
            result = resultBuilder.toString();
            return result;
        }
        for (Map.Entry<String, String> entry : data.entrySet()) {
            result = result.replace("[" + entry.getKey() + "]", entry.getValue());
        }
        return result;
    }
}

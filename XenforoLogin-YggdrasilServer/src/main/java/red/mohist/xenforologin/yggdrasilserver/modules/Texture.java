/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.yggdrasilserver.modules;

import java.util.HashMap;

public class Texture {
    public String url;
    public HashMap<String,String> metadata;
    public Texture setUrl(String url){
        this.url=url;
        return this;
    }
    public Texture addMetadata(String key,String value){
        metadata.put(key,value);
        return this;
    }
    public Texture removeMetadata(String key){
        metadata.remove(key);
        return this;
    }
}

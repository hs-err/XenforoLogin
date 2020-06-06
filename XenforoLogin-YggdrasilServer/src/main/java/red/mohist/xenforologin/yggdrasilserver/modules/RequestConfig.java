/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.yggdrasilserver.modules;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RequestConfig {
    public HashMap<String,String> meta;
    public ArrayList<String> skinDomains;
    public String signaturePublickey;
    public RequestConfig(){
        meta=new HashMap<String,String>();
        skinDomains=new ArrayList<String>();
    }
    public RequestConfig addMeta(String key,String value){
        meta.put(key,value);
        return this;
    }
    public RequestConfig addSkinDomains(String value){
        skinDomains.add(value);
        return this;
    }
    public RequestConfig setSignaturePublickey(String value){
        signaturePublickey=value;
        return this;
    }
}

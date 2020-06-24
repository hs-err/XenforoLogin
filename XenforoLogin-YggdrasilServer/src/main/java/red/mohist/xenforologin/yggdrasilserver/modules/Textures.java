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

public class Textures {
    public int timestamp;
    public String profileId;
    public String profileName;
    public HashMap<String,Texture> textures;
    public Textures setProfileId(String profileId){
        this.profileId=profileId;
        return this;
    }
    public Textures setTimestamp(int timestamp){
        this.timestamp=timestamp;
        return this;
    }
    public Textures setProfileName(String profileName){
        this.profileName=profileName;
        return this;
    }
    public Textures addProperties(String key,Texture value){
        textures.put(key,value);
        return this;
    }
    public Textures removeProperties(String key){
        textures.remove(key);
        return this;
    }
}

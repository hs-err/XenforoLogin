/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.yggdrasilserver.modules;

import java.util.ArrayList;

public class Profile {
    public String id;
    public String name;
    public ArrayList<Propertie> properties;
    public Profile setId(String id){
        this.id=id;
        return this;
    }
    public Profile setName(String name){
        this.name=name;
        return this;
    }
    public Profile addProperties(String name,String value){
        properties.add(new Propertie(name,value));
        return this;
    }
    public Profile removeProperties(String name){
        for (int i = 0; i < properties.size(); i++) {
            if(properties.get(i).name.equals(name)){
                properties.remove(i);
                break;
            }
        }
        return this;
    }
}

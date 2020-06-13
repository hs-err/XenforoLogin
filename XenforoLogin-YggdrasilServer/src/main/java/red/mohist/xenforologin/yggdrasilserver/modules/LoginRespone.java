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

public class LoginRespone {
    public String accessToken;
    public String clientToken;
    public ArrayList<Profile> availableProfiles;
    public Profile selectedProfile;
    public User user;
    public LoginRespone(){
        availableProfiles= new ArrayList<>();
    }
    public LoginRespone addProfiles(Profile profile){
        availableProfiles.add(profile);
        return this;
    }
    public LoginRespone selectedProfile(Profile profile){
        selectedProfile=profile;
        return this;
    }
    public LoginRespone setUser(User user){
        this.user=user;
        return this;
    }
    public LoginRespone setAccessToken(String accessToken){
        this.accessToken=accessToken;
        return this;
    }
    public LoginRespone setClientToken(String  clientToken){
        this.clientToken=clientToken;
        return this;
    }
}

/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.yggdrasilserver.modules;

public class RefreshRespone {
    public String accessToken;
    public String clientToken;
    public Profile selectedProfile;
    public User user;
    public RefreshRespone setSelectedProfile(Profile profile){
        selectedProfile=profile;
        return this;
    }
    public RefreshRespone setUser(User user){
        this.user=user;
        return this;
    }
    public RefreshRespone setAccessToken(String accessToken){
        this.accessToken=accessToken;
        return this;
    }
    public RefreshRespone setClientToken(String  clientToken){
        this.clientToken=clientToken;
        return this;
    }
}

/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.yggdrasilserver.modules;

public class TokenPair {
    public String accessToken;
    public String clientToken;
    public TokenPair setAccessToken(String accessToken){
        this.accessToken=accessToken;
        return this;
    }
    public TokenPair setClientToken(String  clientToken){
        this.clientToken=clientToken;
        return this;
    }
}

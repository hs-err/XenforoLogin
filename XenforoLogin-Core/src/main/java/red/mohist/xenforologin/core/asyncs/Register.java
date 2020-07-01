/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.core.asyncs;

import red.mohist.xenforologin.core.enums.ResultType;
import red.mohist.xenforologin.core.modules.AbstractPlayer;

public abstract class Register {
    public AbstractPlayer player;
    public String email;
    public String password;

    public Register(AbstractPlayer player, String email,String password){
        this.player=player;
        this.email=email;
        this.password=password;
    }
    public abstract void run(ResultType result);
}

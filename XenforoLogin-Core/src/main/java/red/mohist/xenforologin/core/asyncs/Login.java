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

public abstract class Login {
    public AbstractPlayer player;
    public String message;

    public Login(AbstractPlayer player,String message){
        this.player=player;
        this.message=message;
    }
    public abstract void run(ResultType result);
}

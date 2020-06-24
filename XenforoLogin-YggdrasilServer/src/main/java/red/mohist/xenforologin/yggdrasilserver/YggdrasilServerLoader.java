/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.yggdrasilserver;

import red.mohist.xenforologin.core.interfaces.PlatformAdapter;
import red.mohist.xenforologin.core.modules.AbstractPlayer;
import red.mohist.xenforologin.core.modules.LocationInfo;
import red.mohist.xenforologin.core.utils.Helper;

import java.util.logging.Logger;

public class YggdrasilServerLoader implements PlatformAdapter {
    @Override
    public Logger getLogger() {
        return null;
    }

    @Override
    public String getConfigPath(String filename) {
        return Helper.instance.basePath+"/"+filename;
    }

    @Override
    public LocationInfo getSpawn(String world) {
        return null;
    }

    @Override
    public void login(AbstractPlayer player) {

    }

    @Override
    public void sendBlankInventoryPacket(AbstractPlayer player) {

    }
}

/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.core.interfaces;

import red.mohist.xenforologin.core.modules.AbstractPlayer;
import red.mohist.xenforologin.core.modules.LocationInfo;

import java.util.logging.Logger;

public interface PlatformAdapter {

    Logger getLogger();

    String getConfigPath(String filename);

    LocationInfo getSpawn(String world);

    void login(AbstractPlayer player);

    void sendBlankInventoryPacket(AbstractPlayer player);
}

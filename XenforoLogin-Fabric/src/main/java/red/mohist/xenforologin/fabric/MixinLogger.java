/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.fabric;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MixinLogger {

    public static final Logger logger = LogManager.getLogger("XenforoLogin|FabricMixins");

}

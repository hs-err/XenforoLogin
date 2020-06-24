/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.yggdrasilserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import red.mohist.xenforologin.core.forums.ForumSystems;
import red.mohist.xenforologin.core.interfaces.LogProvider;
import red.mohist.xenforologin.core.utils.Helper;

public class YggdrasilServerEntry {
    private static final Logger logger = LogManager
            .getLogger("XenforoLogin-Yggdrasil-Launcher");


    public static void main(String[] args) throws Exception {
        logger.info("Hello world!");
        new Helper(".", new LogProvider() {
            @Override
            public void info(String info) {
                logger.info(info);
            }

            @Override
            public void info(String info, Exception exception) {
                logger.info(info,exception);
            }

            @Override
            public void warn(String info) {
                logger.warn(info);
            }

            @Override
            public void warn(String info, Exception exception) {
                logger.warn(info,exception);
            }
        });
        ForumSystems.reloadConfig();
        YggdrasilServerCore server = new YggdrasilServerCore();
        server.start();
    }

}

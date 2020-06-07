/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.core.interfaces;

public interface LogProvider {
    void info(String info);
    void info(String info,Exception exception);
    void warn(String info);
    void warn(String info,Exception exception);
}

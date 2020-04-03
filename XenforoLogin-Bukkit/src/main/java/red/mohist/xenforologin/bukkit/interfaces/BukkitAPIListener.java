/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.bukkit.interfaces;

import org.bukkit.event.Listener;

public interface BukkitAPIListener extends Listener {

    void eventClass();

    default boolean isAvailable() {
        try {
            eventClass();
        } catch (NoClassDefFoundError e) {
            return false;
        }
        return true;
    }

}

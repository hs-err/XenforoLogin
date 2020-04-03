/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.fabric;

import net.fabricmc.api.DedicatedServerModInitializer;

@SuppressWarnings("unused")
public class FabricLoader implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        System.out.println("Hello world!");
    }
}

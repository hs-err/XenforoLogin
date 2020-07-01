/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.core.proxys;

import org.reflections.Reflections;
import red.mohist.xenforologin.core.utils.Config;
import red.mohist.xenforologin.core.utils.Helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class ProxySystems {
    private static ArrayList<ProxySystem> currentSystem = new ArrayList<>();
    public ProxySystems(){
        {
            int unavailableCount = 0;
            Set<Class<? extends ProxySystem>> classes = new Reflections("red.mohist.xenforologin.core.proxys.implementations")
                    .getSubTypesOf(ProxySystem.class);
            for (Class<? extends ProxySystem> clazz : classes) {
                ProxySystem proxySystem;
                try {
                    proxySystem = clazz.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    Helper.getLogger().warn(clazz.getName() + " is not available.");
                    unavailableCount++;
                    continue;
                }
                currentSystem.add(proxySystem);
            }
            if (unavailableCount > 0) {
                Helper.getLogger().warn("Warning: Some features in this plugin is not available on this version of bukkit");
                Helper.getLogger().warn("If your encountered errors, do NOT report to XenforoLogin.");
                Helper.getLogger().warn("Error count: " + unavailableCount);
            }
        }
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                for (ProxySystem proxySystem : currentSystem) {
                    try {
                        proxySystem.refreshProxys();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 0, Config.getInteger("secure.proxy.update_time", 60)*1000);
    }
    public static boolean isProxy(String ip){
        for (ProxySystem proxySystem : currentSystem) {
            if(proxySystem.isProxy(ip)){
                Helper.getLogger().warn("find proxy by "+proxySystem.getClass().getName());
                return true;
            }
        }
        return false;
    }
}

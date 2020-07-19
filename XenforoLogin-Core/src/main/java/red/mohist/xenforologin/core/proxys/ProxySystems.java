/*
 * Copyright 2020 Mohist-Community
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

    public ProxySystems() {
        {
            int unavailableCount = 0;
            Set<Class<? extends ProxySystem>> classes = new Reflections("red.mohist.xenforologin.core.proxys.implementations")
                    .getSubTypesOf(ProxySystem.class);
            for (Class<? extends ProxySystem> clazz : classes) {
                try {
                    if (Config.getBoolean("secure.proxy.proxys." + clazz.getSimpleName())) {
                        ProxySystem proxySystem = clazz.getDeclaredConstructor().newInstance();
                        currentSystem.add(proxySystem);
                    }
                } catch (Exception e) {
                    Helper.getLogger().warn(clazz.getName() + " is not available.");
                    unavailableCount++;
                }
            }
            if (unavailableCount > 0) {
                Helper.getLogger().warn("Can't pass proxy provider count: " + unavailableCount);
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
        }, 0, Config.getInteger("secure.proxy.update_time", 60) * 1000);
    }

    public static boolean isProxy(String ip) {
        for (ProxySystem proxySystem : currentSystem) {
            if (proxySystem.isProxy(ip)) {
                Helper.getLogger().warn("find proxy by " + proxySystem.getClass().getName());
                return true;
            }
        }
        return false;
    }
}

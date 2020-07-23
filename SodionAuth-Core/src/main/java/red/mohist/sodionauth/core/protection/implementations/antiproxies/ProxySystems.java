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

package red.mohist.sodionauth.core.protection.implementations.antiproxies;

import org.reflections.Reflections;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.protection.SecureSystem;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.Helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class ProxySystems implements SecureSystem {
    private static ArrayList<ProxySystem> currentSystem = new ArrayList<>();

    public ProxySystems() {
        {
            int unavailableCount = 0;
            Set<Class<? extends ProxySystem>> classes = new Reflections("red.mohist.sodionauth.core.protection.implementations.antiproxies.implementations")
                    .getSubTypesOf(ProxySystem.class);
            for (Class<? extends ProxySystem> clazz : classes) {
                try {
                    if (Config.protection.getProxySystems().getProxiesProvider().getOrDefault(
                            clazz.getSimpleName(),true
                    )) {
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
        }, 0, Config.protection.getProxySystems().getUpdateTime(60) * 1000);
    }

    @Override
    public String canJoin(AbstractPlayer player) {
        String ip = player.getAddress().getHostAddress();
        if(ip.equals("127.0.0.1")){
            if(Config.protection.getProxySystems().getEnableLocal()){
                return null;
            }else{
                Helper.getLogger().warn("find proxy by EnableLocal");
                return player.getLang().getErrors().getProxy();
            }
        }
        for (ProxySystem proxySystem : currentSystem) {
            if (proxySystem.isProxy(ip)) {
                Helper.getLogger().warn("find proxy by " + proxySystem.getClass().getName());
                return player.getLang().getErrors().getProxy();
            }
        }
        return null;
    }

    @Override
    public String canLogin(AbstractPlayer player) {
        return null;
    }

    @Override
    public String canRegister(AbstractPlayer player) {
        return null;
    }
}

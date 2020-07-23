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

package red.mohist.sodionauth.core.protection;

import org.reflections.Reflections;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.Helper;

import java.util.ArrayList;
import java.util.Set;

public class SecureSystems {
    private static final ArrayList<SecureSystem> currentSystem = new ArrayList<>();
    public static void reloadConfig() {
        {
            int unavailableCount = 0;
            Set<Class<? extends SecureSystem>> classes = new Reflections("red.mohist.sodionauth.core.protects.implementations")
                    .getSubTypesOf(SecureSystem.class);
            for (Class<? extends SecureSystem> clazz : classes) {
                try {
                    if (Config.getBoolean("protects."+clazz.getSimpleName()+".enable")) {
                        SecureSystem secureSystem = clazz.getDeclaredConstructor().newInstance();
                        currentSystem.add(secureSystem);
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
    }

    public static String canJoin(AbstractPlayer player) {
        for (SecureSystem secureSystem : currentSystem) {
            String canJoin=secureSystem.canJoin(player);
            if(canJoin!=null){
                Helper.getLogger().warn("deny by secure " + secureSystem.getClass().getName());
                return canJoin;
            }
        }
        return null;
    }
    public static String canLogin(AbstractPlayer player) {
        for (SecureSystem secureSystem : currentSystem) {
            String canLogin=secureSystem.canLogin(player);
            if(canLogin!=null){
                Helper.getLogger().warn("deny by secure " + secureSystem.getClass().getName());
                return canLogin;
            }
        }
        return null;
    }
    public static String canRegister(AbstractPlayer player) {
        for (SecureSystem secureSystem : currentSystem) {
            String canRegister=secureSystem.canRegister(player);
            if(canRegister!=null){
                Helper.getLogger().warn("deny by secure " + secureSystem.getClass().getName());
                return canRegister;
            }
        }
        return null;
    }
}

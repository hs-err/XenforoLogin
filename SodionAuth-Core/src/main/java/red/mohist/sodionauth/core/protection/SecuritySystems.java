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


import red.mohist.sodionauth.core.config.MainConfiguration;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.Helper;
import red.mohist.sodionauth.libs.reflections.Reflections;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Set;

public class SecuritySystems {
    private static final ArrayList<SecuritySystem> currentSystem = new ArrayList<>();

    public static void reloadConfig() {
        {
            int unavailableCount = 0;
            Set<Class<? extends SecuritySystem>> classes = new Reflections("red.mohist.sodionauth.core.protection.implementations")
                    .getSubTypesOf(SecuritySystem.class);
            for (Class<? extends SecuritySystem> clazz : classes) {
                try {
                    final Object systemBean = MainConfiguration.ProtectionBean.class
                            .getDeclaredMethod("get" + clazz.getSimpleName())
                            .invoke(Config.protection);
                    Field enabledField = systemBean.getClass().getDeclaredField("enable");
                    enabledField.setAccessible(true);
                    if ((Boolean) enabledField.get(systemBean)) {
                        SecuritySystem securitySystem = clazz.getDeclaredConstructor().newInstance();
                        currentSystem.add(securitySystem);
                    }
                } catch (Exception e) {
                    Helper.getLogger().warn(clazz.getName() + " is not available.", e);
                    unavailableCount++;
                }
            }
            if (unavailableCount > 0) {
                Helper.getLogger().warn("Can't pass proxy provider count: " + unavailableCount);
            }
        }
    }

    public static String canJoin(AbstractPlayer player) {
        for (SecuritySystem securitySystem : currentSystem) {
            String canJoin = securitySystem.canJoin(player);
            if (canJoin != null) {
                Helper.getLogger().warn("deny by secure " + securitySystem.getClass().getName());
                return canJoin;
            }
        }
        return null;
    }

    public static String canLogin(AbstractPlayer player) {
        for (SecuritySystem securitySystem : currentSystem) {
            String canLogin = securitySystem.canLogin(player);
            if (canLogin != null) {
                Helper.getLogger().warn("deny by secure " + securitySystem.getClass().getName());
                return canLogin;
            }
        }
        return null;
    }

    public static String canRegister(AbstractPlayer player) {
        for (SecuritySystem securitySystem : currentSystem) {
            String canRegister = securitySystem.canRegister(player);
            if (canRegister != null) {
                Helper.getLogger().warn("deny by secure " + securitySystem.getClass().getName());
                return canRegister;
            }
        }
        return null;
    }
}

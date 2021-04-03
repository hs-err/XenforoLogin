/*
 * Copyright 2021 Mohist-Community
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

package red.mohist.sodionauth.core;

import com.google.common.eventbus.Subscribe;
import red.mohist.sodionauth.core.dependency.DependencyManager;
import red.mohist.sodionauth.core.events.DownEvent;
import red.mohist.sodionauth.core.interfaces.PlatformAdapter;
import red.mohist.sodionauth.core.services.Service;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.Helper;

import java.util.concurrent.atomic.AtomicBoolean;

public final class SodionAuthCore {

    public static SodionAuthCore instance;
    private final AtomicBoolean isEnabled = new AtomicBoolean(false);
    public PlatformAdapter api;

    public SodionAuthCore(PlatformAdapter platformAdapter) {
        try {
            instance=this;
            api=platformAdapter;

            Helper.getLogger().info("Initializing basic services...");
            /**
            DependencyManager.checkDependencyMaven("org.mindrot", "jbcrypt", "0.4", () -> {
                try {
                    Class.forName("org.mindrot.jbcrypt.BCrypt");
                    return true;
                } catch (Exception e) {
                    return false;
                }
            });
            DependencyManager.checkDependencyMaven("org.reflections", "reflections", "0.9.12", () -> {
                try {
                    Class.forName("org.reflections.Reflections");
                    return true;
                } catch (Exception e) {
                    return false;
                }
            });
            DependencyManager.checkDependencyMaven("org.apache.httpcomponents", "fluent-hc", "4.5.11", () -> {
                try {
                    Class.forName("org.reflections.Reflections");
                    return true;
                } catch (Exception e) {
                    return false;
                }
            });
            DependencyManager.checkDependencyMaven("com.maxmind.geoip2", "geoip2", "2.14.0", () -> {
                try {
                    Class.forName("com.maxmind.geoip2.DatabaseReader");
                    return true;
                } catch (Exception e) {
                    return false;
                }
            });
            DependencyManager.checkDependencyMaven("com.google.code.findbugs", "jsr305", "3.0.2", () -> {
                try {
                    Class.forName("javax.annotation.Nullable");
                    return true;
                } catch (Exception e) {
                    return false;
                }
            });
            DependencyManager.checkDependencyMaven("io.netty", "netty-all", "4.1.50.Final", () -> {
                try {
                    Class.forName("io.netty.util.NettyRuntime");
                    return true;
                } catch (Exception e) {
                    return false;
                }
            });
            DependencyManager.checkDependencyMaven("com.blinkfox", "zealot", "1.3.1", () -> {
                try {
                    Class.forName("com.blinkfox.zealot.core.Zealot");
                    return true;
                } catch (Exception e) {
                    return false;
                }
            });
            DependencyManager.checkDependencyMaven("org.apache.logging.log4j", "log4j-slf4j-impl", "2.8.1", () -> {
                try {
                    Class.forName("org.apache.logging.slf4j.Log4jLoggerFactory");
                    return true;
                } catch (Exception e) {
                    return false;
                }
            });
             **/
            isEnabled.set(true);
            DependencyManager.checkForSQLite();

            new Service();

            if (Config.yggdrasil.getEnable()) {
                new YggdrasilServerCore();
            }
        } catch (Throwable throwable) {
            isEnabled.set(false);
            throwable.printStackTrace();
        }
    }

    public void loadFail() {
        api.shutdown();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isEnabled() {
        return isEnabled.get();
    }

    @Subscribe
    public void onDown(DownEvent event) {
        isEnabled.set(false);
    }
}
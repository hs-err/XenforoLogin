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

package red.mohist.sodionauth.yggdrasilserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import red.mohist.sodionauth.core.SodionAuthCore;
import red.mohist.sodionauth.core.dependency.DependencyManager;
import red.mohist.sodionauth.core.interfaces.LogProvider;
import red.mohist.sodionauth.core.utils.Helper;

public class YggdrasilServerEntry {
    private static final Logger logger = LogManager.getLogger("SodionAuth|YggdrasilServer");


    public static void main(String[] args) throws Exception {
        logger.info("Hello world!");
        new Helper(".", new LogProvider() {
            @Override
            public void info(String info) {
                logger.info(info);
            }

            @Override
            public void info(String info, Exception exception) {
                logger.info(info, exception);
            }

            @Override
            public void warn(String info) {
                logger.warn(info);
            }

            @Override
            public void warn(String info, Exception exception) {
                logger.warn(info, exception);
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
        /*
        DependencyManager.checkDependencyMaven("com.google.code.gson", "gson", "2.8.6", () -> {
            try {
                Class.forName("com.google.gson.Gson");
                return true;
            } catch (Exception e) {
                return false;
            }
        });
        DependencyManager.checkDependencyMaven("com.google.guava", "guava", "29.0-jre", () -> {
            try {
                Class.forName("com.google.common.base.Preconditions");
                return true;
            } catch (Exception e) {
                return false;
            }
        });
        */
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
        new SodionAuthCore(new YggdrasilServerLoader());
    }

}

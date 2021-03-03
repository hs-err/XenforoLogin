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

package red.mohist.sodionauth.fabric;

import net.fabricmc.loader.launch.knot.Knot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import red.mohist.sodionauth.core.probe.Probe;
import red.mohist.sodionauth.fabric.probe.ReflectionClassLoader;

import java.nio.file.FileSystems;

public class FabricDevEnvBootstrap {

    public static final Logger logger = LogManager.getLogger("SodionAuth|FabricDevBootstrap");
    private static final ReflectionClassLoader classLoader = new ReflectionClassLoader();

    public static void wrapClassloaderIfNecessary() {
        if (!Knot.getLauncher().isDevelopment()) {
            logger.info("Fabric development environment not detected");
            return;
        } else
            logger.info("Fabric development environment detected");
        final String coreJarPath = Probe.getJarPath();
        logger.info("Loading SodionAuth-Core[" + coreJarPath + "] into fabric classpath...");
        classLoader.addJarToClasspath(FileSystems.getDefault().getPath(coreJarPath));
        logger.info("Done");
    }

}

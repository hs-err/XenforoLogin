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

package red.mohist.sodionauth.yggdrasilserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import red.mohist.sodionauth.core.probe.Probe;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;

public class YggdrasilServerLaunchWrapper {

    public static final Logger logger = LogManager.getLogger("SodionAuth|YggdrasilServerLaunchWrapper");

    public static void main(String[] args) throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String mainPath = Probe.getJarPath();
        String corePath = red.mohist.sodionauth.core.probe.Probe.getJarPath();
        logger.info("Wrapping JAR [" + mainPath + ", " + corePath + "] into URLClassLoader");
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{FileSystems.getDefault().getPath(mainPath).toUri().toURL(), FileSystems.getDefault().getPath(corePath).toUri().toURL()}, YggdrasilServerLaunchWrapper.class.getClassLoader().getParent());
        logger.info("Done, invoking main...");
        Class.forName("red.mohist.sodionauth.yggdrasilserver.YggdrasilServerEntry", true, urlClassLoader)
                .getMethod("main", String[].class)
                .invoke(null, (Object) args);
    }

    public static class Probe {

        public static String getJarPath() {
            final URL probeResource = Probe.class.getClassLoader().getResource("red/mohist/sodionauth/yggdrasilserver/YggdrasilServerLaunchWrapper.class");
            if (probeResource == null) throw new IllegalStateException("Could not find probeResource");
            String s = probeResource.getPath();
            int start=0;
            int end=s.lastIndexOf("!");
            if(s.startsWith("file:")){
                start+=5;
            }
            if(File.separator.equals("\\")){
                start+=1;
            }
            return s.substring(start,end==-1?s.length():end)
                    .replaceAll("red/mohist/sodionauth/yggdrasilserver/YggdrasilServerLaunchWrapper.class", "");
        }

    }
}

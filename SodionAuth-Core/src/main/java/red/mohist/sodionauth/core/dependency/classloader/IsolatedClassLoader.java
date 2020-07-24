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

/*
 * This file is part of LuckPerms, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 */

package red.mohist.sodionauth.core.dependency.classloader;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * A classloader "isolated" from the rest of the Minecraft server.
 *
 * <p>Used to load specific LuckPerms dependencies without causing conflicts
 * with other plugins, or libraries provided by the server implementation.</p>
 */
public class IsolatedClassLoader extends URLClassLoader {
    static {
        ClassLoader.registerAsParallelCapable();
    }

    public IsolatedClassLoader(URL... urls) {
        /*
         * ClassLoader#getSystemClassLoader returns the AppClassLoader
         *
         * Calling #getParent on this returns the ExtClassLoader (Java 8) or
         * the PlatformClassLoader (Java 9). Since we want this classloader to
         * be isolated from the Minecraft server (the app), we set the parent
         * to be the platform class loader.
         */
        super(urls, ClassLoader.getSystemClassLoader().getParent());
    }

}

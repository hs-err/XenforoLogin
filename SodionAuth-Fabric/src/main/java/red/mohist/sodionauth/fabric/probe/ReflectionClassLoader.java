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

package red.mohist.sodionauth.fabric.probe;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

public class ReflectionClassLoader {

    public static final Logger logger = LogManager.getLogger("SodionAuth|FabricDevBootstrapClassLoader");

    public URLClassLoader getClassLoader() {
        return classLoader;
    }

    private final URLClassLoader classLoader;

    @SuppressWarnings("Guava") // we can't use java.util.Function because old Guava versions are used at runtime
    private final Supplier<Method> addUrlMethod;

    public ReflectionClassLoader() throws IllegalStateException {
        ClassLoader classLoader = getClass().getClassLoader().getParent();
        if (classLoader instanceof URLClassLoader) {
            this.classLoader = (URLClassLoader) classLoader;
        } else {
            throw new IllegalStateException("ClassLoader is not instance of URLClassLoader");
        }

        this.addUrlMethod = Suppliers.memoize(() -> {
            if (isJava9OrNewer()) {
                logger.info("It is safe to ignore any warning printed following this message " +
                        "starting with 'WARNING: An illegal reflective access operation has occurred, Illegal reflective " +
                        "access by " + getClass().getName() + "'. This is intended, and will not have any impact on the " +
                        "operation of SodionAuth.");
            }

            try {
                Method addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                addUrlMethod.setAccessible(true);
                return addUrlMethod;
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void addJarToClasspath(Path file) {
        try {
            this.addUrlMethod.get().invoke(this.classLoader, file.toUri().toURL());
        } catch (IllegalAccessException | InvocationTargetException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isJava9OrNewer() {
        try {
            // method was added in the Java 9 release
            Runtime.class.getMethod("version");
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}

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

import red.mohist.sodionauth.core.authbackends.AuthBackendSystems;
import red.mohist.sodionauth.core.exception.AuthenticatedException;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.modules.PlainPlayer;
import red.mohist.sodionauth.core.services.Service;

import java.net.InetAddress;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public final class SodionAuthApi {
    public static void login(AbstractPlayer player) throws AuthenticatedException {
        try {
            login(player);
        } catch (Throwable e) {
            throw new AuthenticatedException();
        }
    }

    public static boolean isLogin(AbstractPlayer player) {
        return !Service.auth.needCancelled(player);
    }

    public static boolean register(AbstractPlayer player, String email, String password) throws ExecutionException, InterruptedException {
        return Service.auth.registerAsync(player, email, password).get();
    }

    public static boolean register(AbstractPlayer player, String password) throws ExecutionException, InterruptedException {
        return Service.auth.registerAsync(player, null, password).get();
    }

    public static boolean isRegistered(AbstractPlayer player) {
        switch (Service.auth.logged_in.getOrDefault(player.getUniqueId(), null)) {
            case LOGGED_IN:
            case NEED_LOGIN:
            case NEED_CHECK:
                return true;
            case NEED_REGISTER_EMAIL:
            case NEED_REGISTER_PASSWORD:
            case NEED_REGISTER_CONFIRM:
                return false;
        }
        switch (AuthBackendSystems.getCurrentSystem()
                .join(player)
                .shouldLogin(false)) {
            case OK:
            case ERROR_NAME:
                return true;
            default:
                return false;
        }
    }

    public static boolean isRegistered(String playerName) {
        switch (AuthBackendSystems.getCurrentSystem()
                .join(new PlainPlayer(playerName, UUID.randomUUID(), InetAddress.getLoopbackAddress()))
                .shouldLogin(false)) {
            case OK:
            case ERROR_NAME:
                return true;
            default:
                return false;
        }
    }
}
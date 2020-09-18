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

package red.mohist.sodionauth.core;

import org.knownspace.minitask.ITask;
import red.mohist.sodionauth.core.authbackends.AuthBackendSystems;
import red.mohist.sodionauth.core.exception.AuthenticatedException;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.modules.PlainPlayer;

import java.net.InetAddress;
import java.util.UUID;

public final class SodionAuthApi {
    @Deprecated
    public static void login(AbstractPlayer player) throws AuthenticatedException {
        SodionAuthCore.instance.login(player);
    }

    public static ITask<Void> loginAsync(AbstractPlayer player) {
        return SodionAuthCore.instance.loginAsync(player);
    }

    public static boolean isLogin(AbstractPlayer player) {
        return !SodionAuthCore.instance.needCancelled(player);
    }

    public static boolean register(AbstractPlayer player, String email, String password) {
        return SodionAuthCore.instance.register(player, email, password);
    }

    public static boolean register(AbstractPlayer player, String password) {
        return register(player, null, password);
    }

    public static boolean isRegistered(AbstractPlayer player) {
        switch (SodionAuthCore.instance.logged_in.getOrDefault(player.getUniqueId(), null)) {
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
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

import red.mohist.sodionauth.core.exception.AuthenticatedException;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.services.Service;

public final class SodionAuthApi {
    public static void login(AbstractPlayer player) throws AuthenticatedException {
        try {
            Service.auth.login(player);
        } catch (Throwable e) {
            throw new AuthenticatedException();
        }
    }

    public static boolean isLogin(AbstractPlayer player) {
        return !Service.auth.needCancelled(player);
    }

    public static boolean register(AbstractPlayer player, String email, String password) {
        throw new UnsupportedOperationException();
    }

    public static boolean register(AbstractPlayer player, String password) {
        return Service.register.registerSync(player, null, password);
    }

    public static boolean isRegistered(AbstractPlayer player) {
        return false;
    }

    public static boolean isRegistered(String playerName) {
        return false;
    }
}
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

package red.mohist.sodionauth.core.protection.implementations.ratelimit;

import com.google.common.util.concurrent.RateLimiter;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.protection.SecuritySystem;
import red.mohist.sodionauth.core.utils.Config;

@SuppressWarnings("UnstableApiUsage")
public class RateLimit implements SecuritySystem {
    private final RateLimiter rateLimiter;

    public RateLimit() {
        rateLimiter = RateLimiter.create(Config.protection.getRateLimit().getPermitsPerSecond(5));
    }

    @Override
    public String canJoin(AbstractPlayer player) {
        if (Config.protection.getRateLimit().getJoin().getEnable(true)) {
            if (!rateLimiter.tryAcquire(
                    Config.protection.getRateLimit().getJoin().getPermits(1))) {
                return player.getLang().getErrors().getRateLimit();
            }
        }
        return null;
    }

    @Override
    public String canLogin(AbstractPlayer player) {
        if (Config.protection.getRateLimit().getLogin().getEnable(true)) {
            if (!rateLimiter.tryAcquire(
                    Config.protection.getRateLimit().getLogin().getPermits(5))) {
                return player.getLang().getErrors().getRateLimit();
            }
        }
        return null;
    }

    @Override
    public String canRegister(AbstractPlayer player) {
        if (Config.protection.getRateLimit().getRegister().getEnable()) {
            if (!rateLimiter.tryAcquire(
                    Config.protection.getRateLimit().getRegister().getPermits(10)
            )) {
                return player.getLang().getErrors().getRateLimit();
            }
        }
        return null;
    }
}

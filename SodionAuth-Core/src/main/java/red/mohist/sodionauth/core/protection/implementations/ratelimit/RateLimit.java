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
        rateLimiter = RateLimiter.create(Config.protection.RateLimit.permitsPerSecond);
    }

    @Override
    public String canJoin(AbstractPlayer player) {
        if (Config.protection.RateLimit.join>0) {
            if (!rateLimiter.tryAcquire(
                    Config.protection.RateLimit.join)) {
                return player.getLang().errors.rateLimit;
            }
        }
        return null;
    }

    @Override
    public String canLogin(AbstractPlayer player) {
        if (Config.protection.RateLimit.login>0) {
            if (!rateLimiter.tryAcquire(
                    Config.protection.RateLimit.login)) {
                return player.getLang().errors.rateLimit;
            }
        }
        return null;
    }

    @Override
    public String canRegister(AbstractPlayer player) {
        if (Config.protection.RateLimit.register>0) {
            if (!rateLimiter.tryAcquire(
                    Config.protection.RateLimit.register
            )) {
                return player.getLang().errors.rateLimit;
            }
        }
        return null;
    }
}

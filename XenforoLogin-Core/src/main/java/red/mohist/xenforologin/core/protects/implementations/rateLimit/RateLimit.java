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

package red.mohist.xenforologin.core.protects.implementations.rateLimit;

import com.google.common.util.concurrent.RateLimiter;
import red.mohist.xenforologin.core.modules.AbstractPlayer;
import red.mohist.xenforologin.core.protects.SecureSystem;
import red.mohist.xenforologin.core.utils.Config;
import red.mohist.xenforologin.core.utils.Helper;

public class RateLimit implements SecureSystem {
    private RateLimiter rateLimiter;
    public RateLimit(){
        rateLimiter=RateLimiter.create(Config.getDouble("protects.RateLimit.permitsPerSecond"));
    }
    @Override
    public String canJoin(AbstractPlayer player) {
        if (Config.getBoolean("protects.RateLimit.join.enable")) {
            if (!rateLimiter.tryAcquire(Config.getInteger("protects.RateLimit.join.permits"))) {
                return Helper.langFile("errors.rate_limit");
            }
        }
        return null;
    }

    @Override
    public String canLogin(AbstractPlayer player) {
        if (Config.getBoolean("protects.RateLimit.login.enable")) {
            if (!rateLimiter.tryAcquire(Config.getInteger("protects.RateLimit.login.permits"))) {
                return Helper.langFile("errors.rate_limit");
            }
        }
        return null;
    }

    @Override
    public String canRegister(AbstractPlayer player) {
        if (Config.getBoolean("protects.RateLimit.register.enable")) {
            if (!rateLimiter.tryAcquire(Config.getInteger("protects.RateLimit.register.permits"))) {
                return Helper.langFile("errors.rate_limit");
            }
        }
        return null;
    }
}

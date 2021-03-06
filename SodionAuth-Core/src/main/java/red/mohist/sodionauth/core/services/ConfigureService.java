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

package red.mohist.sodionauth.core.services;

import com.google.common.eventbus.Subscribe;
import red.mohist.sodionauth.core.events.BootEvent;
import red.mohist.sodionauth.core.utils.Helper;

import java.io.IOException;

public class ConfigureService {
    @Subscribe
    public void onBoot(BootEvent event) throws IOException {
        Helper.getLogger().info("Initializing configure service...");
    }
}

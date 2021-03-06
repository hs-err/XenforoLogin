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

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import red.mohist.sodionauth.core.events.Event;
import red.mohist.sodionauth.core.utils.Helper;

public class EventBusService {
    public EventBus eventBus = new EventBus();
    public AsyncEventBus asyncEventBus = new AsyncEventBus(Service.threadPool.executor);

    public EventBusService() {
        Helper.getLogger().info("Initializing eventBus service...");

    }

    public Event post(Event event) {
        event.setAsync(false);
        eventBus.post(event);
        return event;
    }

    public EventBusService register(Object listener) {
        eventBus.register(listener);
        asyncEventBus.register(listener);
        return this;
    }
}

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

package red.mohist.sodionauth.fabric.mixinhelper;

import net.minecraft.server.network.ServerPlayerEntity;
import red.mohist.sodionauth.core.events.player.QuitEvent;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.fabric.MixinLogger;
import red.mohist.sodionauth.fabric.implementation.FabricPlayer;

public class MixinClientConnectionHelper {
    public static void onBeforeDisconnect(ServerPlayerEntity player) {
        AbstractPlayer abstractPlayer = new FabricPlayer(player);
        MixinLogger.logger.info("Calling onRemovePlayer for " + player.getName().asString());
        new QuitEvent(abstractPlayer).post();
    }
}

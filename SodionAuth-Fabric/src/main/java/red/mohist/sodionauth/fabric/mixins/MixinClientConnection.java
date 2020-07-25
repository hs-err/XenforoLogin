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

package red.mohist.sodionauth.fabric.mixins;

import javassist.bytecode.Opcode;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import red.mohist.sodionauth.fabric.mixinhelper.MixinClientConnectionHelper;

@Mixin(ClientConnection.class)
public abstract class MixinClientConnection {

    @Shadow
    private PacketListener packetListener;

    @Shadow
    public abstract PacketListener getPacketListener();

    @Inject(
            method = "handleDisconnection",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/network/ClientConnection;disconnected:Z",
                    opcode = Opcode.PUTFIELD,
                    by = -1
            )
    )
    public void beforeDisconnect(CallbackInfo ci) {
        MixinClientConnectionHelper.onBeforeDisconnect(
                ((ServerPlayNetworkHandler) getPacketListener()).player
        );
    }

}

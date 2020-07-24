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

package red.mohist.sodionauth.sponge.implementation;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import red.mohist.sodionauth.core.modules.*;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.Helper;
import red.mohist.sodionauth.sponge.SpongeLoader;

import java.net.InetAddress;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SpongePlayer extends AbstractPlayer {

    private Player handle;

    public SpongePlayer(Player handle) {
        super(handle.getName(), handle.getUniqueId(), handle.getConnection().getAddress().getAddress());
        this.handle = handle;
    }
    public SpongePlayer(String name, UUID uuid, InetAddress address) {
        super(name, uuid, address);
    }
    public void checkHandle(){
        if(handle==null){
            Optional<Player> optionalPlayer = Sponge.getServer().getPlayer(getUniqueId());
            if(!optionalPlayer.isPresent()){
                Helper.getLogger().warn("No player can be call.");
            }else{
                handle=optionalPlayer.get();
            }
        }
    }
    @Override
    public void sendMessage(String message) {
        checkHandle();
        handle.sendMessage(Text.of(message));
    }

    @Override
    public CompletableFuture<Boolean> teleport(LocationInfo location) {
        checkHandle();
        CompletableFuture<Boolean> booleanCompletableFuture = new CompletableFuture<>();
        if(Sponge.getServer().isMainThread()){
            booleanCompletableFuture.complete(handle.setLocation(Sponge.getServer().getWorld(location.world)
                    .get().getLocation(location.x, location.y, location.z)));
        }else{
            Sponge.getScheduler().createTaskBuilder().execute(()->{
                try {
                    booleanCompletableFuture.complete(teleport(location).get());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }).submit(SpongeLoader.instance);
        }
        return booleanCompletableFuture;
    }

    @Override
    public void kick(String message) {
        checkHandle();
        handle.kick(Text.of(message));
    }

    @Override
    public PlayerInfo getPlayerInfo() {
        checkHandle();
        PlayerInfo playerInfo = new PlayerInfo();
        playerInfo.locationInfo=getLocation();
        playerInfo.gameMode=getGameMode();
        playerInfo.health=handle.getHealthData().health().get();
        playerInfo.maxHealth=handle.getHealthData().maxHealth().get();
        handle.getValue(Keys.FALL_DISTANCE).ifPresent(value -> playerInfo.fallDistance = value.get());
        Vector3d v3d = handle.getVelocity();
        playerInfo.velocityInfo= VelocityInfo.create(v3d.getX(),v3d.getY(),v3d.getZ());
        playerInfo.foodInfo= FoodInfo.create(
                handle.getFoodData().foodLevel().get(),
                handle.getFoodData().exhaustion().get(),
                handle.getFoodData().saturation().get()
        );
        handle.getValue(Keys.REMAINING_AIR).ifPresent(value -> playerInfo.remainingAir = value.get());
        return null;
    }

    @Override
    public void setPlayerInfo(PlayerInfo playerInfo) {

    }

    public LocationInfo getLocation() {
        Location<World> location = handle.getLocation();

        return new LocationInfo(
                location.getExtent().getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                0, 0
        );
    }

    public int getGameMode() {
        GameMode gameMode = handle.gameMode().get();
        if (GameModes.SURVIVAL.equals(gameMode)) {
            return 0;
        } else if (GameModes.CREATIVE.equals(gameMode)) {
            return 1;
        } else if (GameModes.ADVENTURE.equals(gameMode)) {
            return 2;
        } else if (GameModes.SPECTATOR.equals(gameMode)) {
            return 3;
        } else {
            return Config.security.getDefaultGamemode(0);
        }
    }

    @Override
    public void setGameMode(int gameMode) {
        checkHandle();
        if(Sponge.getServer().isMainThread()){
            switch (gameMode) {
                case 0:
                    handle.offer(Keys.GAME_MODE,GameModes.SURVIVAL);
                    return;
                case 1:
                    handle.offer(Keys.GAME_MODE,GameModes.CREATIVE);
                    return;
                case 2:
                    handle.offer(Keys.GAME_MODE,GameModes.ADVENTURE);
                    return;
                case 3:
                    handle.offer(Keys.GAME_MODE,GameModes.SPECTATOR);
            }
        }else{
            Sponge.getScheduler().createTaskBuilder().execute(()->{
                setGameMode(gameMode);
            }).submit(SpongeLoader.instance);
        }
    }

    @Override
    public boolean isOnline() {
        if(handle==null){
            Optional<Player> optionalPlayer = Sponge.getServer().getPlayer(getUniqueId());
            return optionalPlayer.map(User::isOnline).orElse(false);
        }else {
            return handle.isOnline();
        }
    }
}

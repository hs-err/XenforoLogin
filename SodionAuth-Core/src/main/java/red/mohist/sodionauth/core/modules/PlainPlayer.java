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

package red.mohist.sodionauth.core.modules;

import java.net.InetAddress;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlainPlayer extends AbstractPlayer {

    public PlainPlayer(String name, UUID uuid, InetAddress address) {
        super(name, uuid, address);
    }

    @Override
    public void sendMessage(String message) {

    }

    @Override
    public CompletableFuture<Boolean> teleport(LocationInfo location) {
        return null;
    }

    @Override
    public void kick(String message) {

    }

    @Override
    public LocationInfo getLocation() {
        return null;
    }

    @Override
    public void setLocation(LocationInfo location) {

    }

    @Override
    public int getGameMode() {
        return 0;
    }

    @Override
    public void setGameMode(int gameMode) {

    }

    @Override
    public double getHealth() {
        return 0;
    }

    @Override
    public void setHealth(double health) {

    }

    @Override
    public double getMaxHealth() {
        return 0;
    }

    @Override
    public void setMaxHealth(double maxHealth) {

    }

    @Override
    public float getFallDistance() {
        return 0;
    }

    @Override
    public void setFallDistance(float fallDistance) {

    }

    @Override
    public VelocityInfo getVelocity() {
        return null;
    }

    @Override
    public void setVelocity(VelocityInfo velocity) {

    }

    @Override
    public FoodInfo getFood() {
        return null;
    }

    @Override
    public void setFood(FoodInfo food) {

    }

    @Override
    public int getRemainingAir() {
        return 0;
    }

    @Override
    public void setRemainingAir(int remainingAir) {

    }

    @Override
    public Collection<EffectInfo> getEffects() {
        return null;
    }

    @Override
    public void setEffects(Collection<EffectInfo> effects) {

    }

    @Override
    public boolean isOnline() {
        return false;
    }
}

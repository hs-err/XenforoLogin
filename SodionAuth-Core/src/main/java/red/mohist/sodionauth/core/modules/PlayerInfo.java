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

import red.mohist.sodionauth.core.SodionAuthCore;
import red.mohist.sodionauth.core.utils.Config;

import java.util.Collection;
import java.util.LinkedList;

public class PlayerInfo {
    public LocationInfo location = SodionAuthCore.instance.spawn_location;
    public Integer gameMode = Config.security.getDefaultGamemode();
    public double health=20;
    public double maxHealth=20;
    public float fallDistance=0;
    public VelocityInfo velocity=new VelocityInfo();
    public FoodInfo food=new FoodInfo();
    public int remainingAir=0;
    public Collection<EffectInfo> effects=new LinkedList<>();
}

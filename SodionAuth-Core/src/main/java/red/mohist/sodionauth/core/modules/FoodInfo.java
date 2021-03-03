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

package red.mohist.sodionauth.core.modules;

public class FoodInfo {
    public int foodLevel = 20;
    public double exhaustion = 0;
    public double saturation = 5;

    public static FoodInfo create(int foodLevel, double exhaustion, double saturation) {
        FoodInfo foodInfo = new FoodInfo();
        foodInfo.foodLevel = foodLevel;
        foodInfo.exhaustion = exhaustion;
        foodInfo.saturation = saturation;
        return foodInfo;
    }
}
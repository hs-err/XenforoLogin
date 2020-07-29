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

public class VelocityInfo {
    public double x = 0;
    public double y = 0;
    public double z = 0;

    public static VelocityInfo create(double x, double y, double z) {
        VelocityInfo velocityInfo = new VelocityInfo();
        velocityInfo.x = x;
        velocityInfo.y = y;
        velocityInfo.z = z;
        return velocityInfo;
    }
}

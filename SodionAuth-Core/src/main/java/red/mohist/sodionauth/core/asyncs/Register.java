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

package red.mohist.sodionauth.core.asyncs;

import red.mohist.sodionauth.core.enums.ResultType;
import red.mohist.sodionauth.core.modules.AbstractPlayer;

public abstract class Register {
    public final AbstractPlayer player;
    public final String email;
    public final String password;

    public Register(AbstractPlayer player, String email, String password) {
        this.player = player;
        this.email = email;
        this.password = password;
    }

    public abstract void run(ResultType result);
}
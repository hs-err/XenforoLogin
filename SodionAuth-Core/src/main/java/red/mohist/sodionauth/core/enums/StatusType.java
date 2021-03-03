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

package red.mohist.sodionauth.core.enums;

public enum StatusType {
    NEED_CHECK, NEED_LOGIN, NEED_REGISTER_EMAIL, NEED_REGISTER_PASSWORD, NEED_REGISTER_CONFIRM, LOGGED_IN, HANDLE;
    public String email;
    public String password;

    public StatusType setEmail(String t) {
        email = t;
        return this;
    }

    public StatusType setPassword(String t) {
        password = t;
        return this;
    }
}

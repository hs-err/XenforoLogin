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

public class PlayerStatus {
    public StatusType type;

    public String email;
    public String password;

    public PlayerStatus(StatusType type) {
        this.type = type;
    }

    public static PlayerStatus needCheck() {
        return new PlayerStatus(StatusType.NEED_CHECK);
    }

    public static PlayerStatus needLogin() {
        return new PlayerStatus(StatusType.NEED_LOGIN);
    }

    public static PlayerStatus needRegisterEmail() {
        return new PlayerStatus(StatusType.NEED_REGISTER_EMAIL);
    }

    public static PlayerStatus needRegisterPassword() {
        return new PlayerStatus(StatusType.NEED_REGISTER_PASSWORD);
    }

    public static PlayerStatus needRegisterConfirm() {
        return new PlayerStatus(StatusType.NEED_REGISTER_CONFIRM);
    }

    public static PlayerStatus loggedIn() {
        return new PlayerStatus(StatusType.LOGGED_IN);
    }

    public static PlayerStatus handle() {
        return new PlayerStatus(StatusType.HANDLE);
    }

    public static PlayerStatus proxyHandle() {
        return new PlayerStatus(StatusType.PROXY_HANDLE);
    }

    public PlayerStatus setEmail(String t) {
        email = t;
        return this;
    }

    public PlayerStatus setPassword(String t) {
        password = t;
        return this;
    }

    public enum StatusType {
        NEED_CHECK,
        NEED_LOGIN,
        NEED_REGISTER_EMAIL,
        NEED_REGISTER_PASSWORD,
        NEED_REGISTER_CONFIRM,
        LOGGED_IN,
        HANDLE,
        PROXY_HANDLE;
    }
}

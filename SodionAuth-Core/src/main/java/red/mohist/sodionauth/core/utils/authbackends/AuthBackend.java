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

package red.mohist.sodionauth.core.utils.authbackends;

import red.mohist.sodionauth.core.config.MainConfiguration;
import red.mohist.sodionauth.core.database.entities.AuthInfo;
import red.mohist.sodionauth.core.database.entities.User;

public abstract class AuthBackend {
    public String friendlyName;
    public boolean allowLogin;
    public boolean allowRegister;


    public AuthBackend(MainConfiguration.ApiBean.ApiConfigBean config) {
        this.friendlyName = config.friendlyName;
        this.allowLogin = config.allowLogin;
        this.allowRegister = config.allowRegister;
    }

    public abstract LoginResult login(User user, AuthInfo authInfo, String password);

    public abstract RegisterResult register(User user, String password);

    public abstract GetResult get(User user);

    public enum LoginResultType {
        SUCCESS,
        ERROR_NAME,
        ERROR_PASSWORD,
        ERROR_SERVER,
        NO_USER;
    }

    public enum RegisterResult {
        SUCCESS,
        NAME_EXIST,
        EMAIL_EXIST,
        ERROR_NAME,
        ERROR_EMAIL,
        ERROR_SERVER
    }

    public enum GetResultType {
        SUCCESS,
        NO_SUCH_USER,
        ERROR_SERVER;
    }

    public static class LoginResult {
        public LoginResultType type;
        public String correct;

        public LoginResult(LoginResultType type) {
            this.type = type;
        }

        public static LoginResult success() {
            return new LoginResult(LoginResultType.SUCCESS);
        }

        public static LoginResult errorName() {
            return new LoginResult(LoginResultType.ERROR_NAME);
        }

        public static LoginResult errorPassword() {
            return new LoginResult(LoginResultType.ERROR_PASSWORD);
        }

        public static LoginResult errorServer() {
            return new LoginResult(LoginResultType.ERROR_SERVER);
        }

        public static LoginResult noUser() {
            return new LoginResult(LoginResultType.NO_USER);
        }

        public LoginResult setCorrect(String ss) {
            return this;
        }
    }

    public static class GetResult {
        public GetResultType type;
        public String name;
        public String email;

        public GetResult(GetResultType type) {
            this.type = type;
        }

        public static GetResult success() {
            return new GetResult(GetResultType.SUCCESS);
        }

        public static GetResult noSuchUser() {
            return new GetResult(GetResultType.NO_SUCH_USER);
        }

        public static GetResult errorServer() {
            return new GetResult(GetResultType.ERROR_SERVER);
        }

        public GetResult setName(String name) {
            this.name = name;
            return this;
        }

        public GetResult setEmail(String email) {
            this.email = email;
            return this;
        }
    }
}

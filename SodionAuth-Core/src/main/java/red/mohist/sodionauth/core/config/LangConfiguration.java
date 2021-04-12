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

package red.mohist.sodionauth.core.config;

import red.mohist.sodionauth.core.utils.Lang;

import java.util.Map;

public class LangConfiguration {

    public String prefix;
    public String registerEmail;
    public String registerPassword;
    public String registerPasswordConfirm;
    public String loggedIn;
    public String loginSuccess;
    public String unRegisterSuccess;
    public String session;
    public String needLogin;
    public ErrorsBean errors;

    public static class ErrorsBean {
        public String prefix;
        public String proxy;
        public String rateLimit;
        public String countryLimit;
        public String email;
        public String server;
        public String confirm;
        public String handle;
        public String password;
        public String key;
        public String url;
        public String noUser;
        public String timeOut;
        public String nameIncorrect;
        public String unknown;
        public String mailExist;
        public String usernameExist;
        public String loginExist;

        public String getNameIncorrect(Map<String, Object> data) {
            String result = nameIncorrect;
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", (String) entry.getValue());
            }
            return result;
        }
    }
}

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

import java.util.Map;

public class LangConfiguration {
    public String registerEmail;
    public String registerPassword;
    public String registerPasswordConfirm;
    public String loggedIn;
    public String loginSuccess;
    public String registerSuccess;
    public String unRegisterSuccess;
    public String session;
    public String needLogin;
    public String passwordStrength;
    public String passwordTipPrefix;
    public String passwordWarnPrefix;

    public String thisServer;

    public String getPasswordStrength(Map<String, Object> data) {
        String result = passwordStrength;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            result = result.replace("[" + entry.getKey() + "]", (String) entry.getValue());
        }
        return result;
    }

    public String getRegisterSuccess(Map<String, Object> data) {
        String result = registerSuccess;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            result = result.replace("[" + entry.getKey() + "]", (String) entry.getValue());
        }
        return result;
    }

    public ErrorsBean errors;

    public static class ErrorsBean {
        public String registerFailed;
        public String proxy;
        public String rateLimit;
        public String countryLimit;
        public String email;
        public String server;
        public String confirm;
        public String handle;
        public String password;
        public String key;
        public String noUser;
        public String timeOut;
        public String nameIncorrect;
        public String unknown;
        public String usernameExist;
        public String loginExist;

        public String getRegisterFailed(Map<String, Object> data) {
            String result = registerFailed;
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", (String) entry.getValue());
            }
            return result;
        }

        public String getKey(Map<String, Object> data) {
            String result = key;
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", (String) entry.getValue());
            }
            return result;
        }

        public String getNameIncorrect(Map<String, Object> data) {
            String result = nameIncorrect;
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", (String) entry.getValue());
            }
            return result;
        }
    }
}

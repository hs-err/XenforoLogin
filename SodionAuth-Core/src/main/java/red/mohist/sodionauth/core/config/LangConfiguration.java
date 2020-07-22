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

package red.mohist.sodionauth.core.config;

import com.google.gson.Gson;

public class LangConfiguration {


    public String register_email;
    public String register_password;
    public String register_password_confirm;
    public String logged_in;
    public String success;
    public String session;
    public String last_login;
    public String last_login_unknown;
    public String need_login;
    public ErrorsConfiguration errors;

    public static LangConfiguration objectFromData(String str) {

        return new Gson().fromJson(str, LangConfiguration.class);
    }

    public static class ErrorsConfiguration {
        public String proxy;
        public String rate_limit;
        public String country_limit;
        public String email;
        public String server;
        public String confirm;
        public String handle;
        public String password;
        public String key;
        public String url;
        public String no_user;
        public String time_out;
        public String name_incorrect;
        public String unknown;
        public String mail_exist;
        public String user_exist;

        public static ErrorsConfiguration objectFromData(String str) {

            return new Gson().fromJson(str, ErrorsConfiguration.class);
        }
    }
}

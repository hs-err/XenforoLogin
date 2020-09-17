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

import red.mohist.sodionauth.core.utils.Lang;

import java.util.Map;

public class LangConfiguration {

    private String prefix;
    private String registerEmail;
    private String registerPassword;
    private String registerPasswordConfirm;
    private String loggedIn;
    private String success;
    private String session;
    private String needLogin;
    private ErrorsBean errors;

    public String getPrefix() {
        return prefix != null ? prefix : Lang.all.prefix;
    }

    public String getRegisterEmail(Map<String, Object> data) {
        String result = getRegisterEmail();
        if (result == null) {
            StringBuilder resultBuilder = new StringBuilder("registerEmail");
            resultBuilder.append("\n");
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                resultBuilder.append(entry.getKey()).append(":").append((String) entry.getValue());
            }
            result = resultBuilder.toString();
            return result;
        }
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            result = result.replace("[" + entry.getKey() + "]", (String) entry.getValue());
        }
        return result;
    }

    public String getRegisterEmail() {
        return getPrefix() +
                (registerEmail != null ? registerEmail : Lang.all.registerEmail);
    }

    public String getRegisterPassword(Map<String, Object> data) {
        String result = getRegisterPassword();
        if (result == null) {
            StringBuilder resultBuilder = new StringBuilder("registerPassword");
            resultBuilder.append("\n");
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                resultBuilder.append(entry.getKey()).append(":").append((String) entry.getValue());
            }
            result = resultBuilder.toString();
            return result;
        }
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            result = result.replace("[" + entry.getKey() + "]", (String) entry.getValue());
        }
        return result;
    }

    public String getRegisterPassword() {
        return getPrefix() +
                (registerPassword != null ? registerPassword : Lang.all.registerPassword);
    }

    public String getRegisterPasswordConfirm(Map<String, Object> data) {
        String result = getRegisterPasswordConfirm();
        if (result == null) {
            StringBuilder resultBuilder = new StringBuilder("registerPasswordConfirm");
            resultBuilder.append("\n");
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                resultBuilder.append(entry.getKey()).append(":").append((String) entry.getValue());
            }
            result = resultBuilder.toString();
            return result;
        }
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            result = result.replace("[" + entry.getKey() + "]", (String) entry.getValue());
        }
        return result;
    }

    public String getRegisterPasswordConfirm() {
        return getPrefix() +
                (registerPasswordConfirm != null ? registerPasswordConfirm : Lang.all.registerPasswordConfirm);
    }

    public String getLoggedIn(Map<String, Object> data) {
        String result = getLoggedIn();
        if (result == null) {
            StringBuilder resultBuilder = new StringBuilder("loggedIn");
            resultBuilder.append("\n");
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                resultBuilder.append(entry.getKey()).append(":").append((String) entry.getValue());
            }
            result = resultBuilder.toString();
            return result;
        }
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            result = result.replace("[" + entry.getKey() + "]", (String) entry.getValue());
        }
        return result;
    }

    public String getLoggedIn() {
        return getPrefix() +
                (loggedIn != null ? loggedIn : Lang.all.loggedIn);
    }

    public String getSuccess(Map<String, Object> data) {
        String result = getSuccess();
        if (result == null) {
            StringBuilder resultBuilder = new StringBuilder("success");
            resultBuilder.append("\n");
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                resultBuilder.append(entry.getKey()).append(":").append((String) entry.getValue());
            }
            result = resultBuilder.toString();
            return result;
        }
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            result = result.replace("[" + entry.getKey() + "]", (String) entry.getValue());
        }
        return result;
    }

    public String getSuccess() {
        return getPrefix() +
                (success != null ? success : Lang.all.success);
    }

    public String getSession(Map<String, Object> data) {
        String result = getSession();
        if (result == null) {
            StringBuilder resultBuilder = new StringBuilder("session");
            resultBuilder.append("\n");
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                resultBuilder.append(entry.getKey()).append(":").append((String) entry.getValue());
            }
            result = resultBuilder.toString();
            return result;
        }
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            result = result.replace("[" + entry.getKey() + "]", (String) entry.getValue());
        }
        return result;
    }

    public String getSession() {
        return getPrefix() +
                (session != null ? session : Lang.all.session);
    }

    public String getNeedLogin(Map<String, Object> data) {
        String result = getNeedLogin();
        if (result == null) {
            StringBuilder resultBuilder = new StringBuilder("needLogin");
            resultBuilder.append("\n");
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                resultBuilder.append(entry.getKey()).append(":").append((String) entry.getValue());
            }
            result = resultBuilder.toString();
            return result;
        }
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            result = result.replace("[" + entry.getKey() + "]", (String) entry.getValue());
        }
        return result;
    }

    public String getNeedLogin() {
        return getPrefix() +
                (needLogin != null ? needLogin : Lang.all.needLogin);
    }

    public ErrorsBean getErrors() {
        return errors;
    }

    public static class ErrorsBean {
        private String prefix;
        private String proxy;
        private String rateLimit;
        private String countryLimit;
        private String email;
        private String server;
        private String confirm;
        private String handle;
        private String password;
        private String key;
        private String url;
        private String noUser;
        private String timeOut;
        private String nameIncorrect;
        private String unknown;
        private String mailExist;
        private String userExist;
        private String loginExist;

        public String getPrefix() {
            return prefix != null ? prefix : Lang.all.errors.prefix;
        }

        public String getProxy(Map<String, Object> data) {
            String result = getProxy();
            if (result == null) {
                StringBuilder resultBuilder = new StringBuilder("proxy");
                resultBuilder.append("\n");
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    resultBuilder.append(entry.getKey()).append(":").append((String) entry.getValue());
                }
                result = resultBuilder.toString();
                return result;
            }
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", (String) entry.getValue());
            }
            return result;
        }

        public String getProxy() {
            return getPrefix() +
                    (proxy != null ? proxy : Lang.all.errors.proxy);
        }

        public String getRateLimit(Map<String, Object> data) {
            String result = getRateLimit();
            if (result == null) {
                StringBuilder resultBuilder = new StringBuilder("rateLimit");
                resultBuilder.append("\n");
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    resultBuilder.append(entry.getKey()).append(":").append((String) entry.getValue());
                }
                result = resultBuilder.toString();
                return result;
            }
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", (String) entry.getValue());
            }
            return result;
        }

        public String getRateLimit() {
            return getPrefix() +
                    (rateLimit != null ? rateLimit : Lang.all.errors.rateLimit);
        }

        public String getCountryLimit(Map<String, Object> data) {
            String result = getCountryLimit();
            if (result == null) {
                StringBuilder resultBuilder = new StringBuilder("countryLimit");
                resultBuilder.append("\n");
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    resultBuilder.append(entry.getKey()).append(":").append((String) entry.getValue());
                }
                result = resultBuilder.toString();
                return result;
            }
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", (String) entry.getValue());
            }
            return result;
        }

        public String getCountryLimit() {
            return getPrefix() +
                    (countryLimit != null ? countryLimit : Lang.all.errors.countryLimit);
        }

        public String getEmail(Map<String, Object> data) {
            String result = getEmail();
            if (result == null) {
                StringBuilder resultBuilder = new StringBuilder("email");
                resultBuilder.append("\n");
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    resultBuilder.append(entry.getKey()).append(":").append((String) entry.getValue());
                }
                result = resultBuilder.toString();
                return result;
            }
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", (String) entry.getValue());
            }
            return result;
        }

        public String getEmail() {
            return getPrefix() +
                    (email != null ? email : Lang.all.errors.email);
        }

        public String getServer(Map<String, Object> data) {
            String result = getServer();
            if (result == null) {
                StringBuilder resultBuilder = new StringBuilder("server");
                resultBuilder.append("\n");
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    resultBuilder.append(entry.getKey()).append(":").append((String) entry.getValue());
                }
                result = resultBuilder.toString();
                return result;
            }
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", (String) entry.getValue());
            }
            return result;
        }

        public String getServer() {
            return getPrefix() +
                    (server != null ? server : Lang.all.errors.server);
        }

        public String getConfirm(Map<String, Object> data) {
            String result = getConfirm();
            if (result == null) {
                StringBuilder resultBuilder = new StringBuilder("confirm");
                resultBuilder.append("\n");
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    resultBuilder.append(entry.getKey()).append(":").append((String) entry.getValue());
                }
                result = resultBuilder.toString();
                return result;
            }
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", (String) entry.getValue());
            }
            return result;
        }

        public String getConfirm() {
            return getPrefix() +
                    (confirm != null ? confirm : Lang.all.errors.confirm);
        }

        public String getHandle(Map<String, Object> data) {
            String result = getHandle();
            if (result == null) {
                StringBuilder resultBuilder = new StringBuilder("handle");
                resultBuilder.append("\n");
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    resultBuilder.append(entry.getKey()).append(":").append((String) entry.getValue());
                }
                result = resultBuilder.toString();
                return result;
            }
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", (String) entry.getValue());
            }
            return result;
        }

        public String getHandle() {
            return getPrefix() +
                    (handle != null ? handle : Lang.all.errors.handle);
        }

        public String getPassword(Map<String, Object> data) {
            String result = getPassword();
            if (result == null) {
                StringBuilder resultBuilder = new StringBuilder("password");
                resultBuilder.append("\n");
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    resultBuilder.append(entry.getKey()).append(":").append((String) entry.getValue());
                }
                result = resultBuilder.toString();
                return result;
            }
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", (String) entry.getValue());
            }
            return result;
        }

        public String getPassword() {
            return getPrefix() +
                    (password != null ? password : Lang.all.errors.password);
        }

        public String getKey(Map<String, Object> data) {
            String result = getKey();
            if (result == null) {
                StringBuilder resultBuilder = new StringBuilder("key");
                resultBuilder.append("\n");
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    resultBuilder.append(entry.getKey()).append(":").append((String) entry.getValue());
                }
                result = resultBuilder.toString();
                return result;
            }
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", (String) entry.getValue());
            }
            return result;
        }

        public String getKey() {
            return getPrefix() +
                    (key != null ? key : Lang.all.errors.key);
        }

        public String getUrl(Map<String, Object> data) {
            String result = getUrl();
            if (result == null) {
                StringBuilder resultBuilder = new StringBuilder("url");
                resultBuilder.append("\n");
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    resultBuilder.append(entry.getKey()).append(":").append((String) entry.getValue());
                }
                result = resultBuilder.toString();
                return result;
            }
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", (String) entry.getValue());
            }
            return result;
        }

        public String getUrl() {
            return getPrefix() +
                    (url != null ? url : Lang.all.errors.url);
        }

        public String getNoUser(Map<String, Object> data) {
            String result = getNoUser();
            if (result == null) {
                StringBuilder resultBuilder = new StringBuilder("noUser");
                resultBuilder.append("\n");
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    resultBuilder.append(entry.getKey()).append(":").append((String) entry.getValue());
                }
                result = resultBuilder.toString();
                return result;
            }
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", (String) entry.getValue());
            }
            return result;
        }

        public String getNoUser() {
            return getPrefix() +
                    (noUser != null ? noUser : Lang.all.errors.noUser);
        }

        public String getTimeOut(Map<String, Object> data) {
            String result = getTimeOut();
            if (result == null) {
                StringBuilder resultBuilder = new StringBuilder("timeOut");
                resultBuilder.append("\n");
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    resultBuilder.append(entry.getKey()).append(":").append((String) entry.getValue());
                }
                result = resultBuilder.toString();
                return result;
            }
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", (String) entry.getValue());
            }
            return result;
        }

        public String getTimeOut() {
            return getPrefix() +
                    (timeOut != null ? timeOut : Lang.all.errors.timeOut);
        }

        public String getNameIncorrect(Map<String, Object> data) {
            String result = getNameIncorrect();
            if (result == null) {
                StringBuilder resultBuilder = new StringBuilder("nameIncorrect");
                resultBuilder.append("\n");
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    resultBuilder.append(entry.getKey()).append(":").append((String) entry.getValue());
                }
                result = resultBuilder.toString();
                return result;
            }
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", (String) entry.getValue());
            }
            return result;
        }

        public String getNameIncorrect() {
            return getPrefix() +
                    (nameIncorrect != null ? nameIncorrect : Lang.all.errors.nameIncorrect);
        }

        public String getUnknown(Map<String, Object> data) {
            String result = getUnknown();
            if (result == null) {
                StringBuilder resultBuilder = new StringBuilder("unknown");
                resultBuilder.append("\n");
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    resultBuilder.append(entry.getKey()).append(":").append((String) entry.getValue());
                }
                result = resultBuilder.toString();
                return result;
            }
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", (String) entry.getValue());
            }
            return result;
        }

        public String getUnknown() {
            return getPrefix() +
                    (unknown != null ? unknown : Lang.all.errors.unknown);
        }

        public String getMailExist(Map<String, Object> data) {
            String result = getMailExist();
            if (result == null) {
                StringBuilder resultBuilder = new StringBuilder("mailExist");
                resultBuilder.append("\n");
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    resultBuilder.append(entry.getKey()).append(":").append((String) entry.getValue());
                }
                result = resultBuilder.toString();
                return result;
            }
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", (String) entry.getValue());
            }
            return result;
        }

        public String getMailExist() {
            return getPrefix() +
                    (mailExist != null ? mailExist : Lang.all.errors.mailExist);
        }

        public String getUserExist(Map<String, Object> data) {
            String result = getUserExist();
            if (result == null) {
                StringBuilder resultBuilder = new StringBuilder("userExist");
                resultBuilder.append("\n");
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    resultBuilder.append(entry.getKey()).append(":").append((String) entry.getValue());
                }
                result = resultBuilder.toString();
                return result;
            }
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", (String) entry.getValue());
            }
            return result;
        }

        public String getUserExist() {
            return getPrefix() +
                    (userExist != null ? userExist : Lang.all.errors.userExist);
        }

        public String getLoginExist(Map<String, Object> data) {
            String result = getLoginExist();
            if (result == null) {
                StringBuilder resultBuilder = new StringBuilder("loginExist");
                resultBuilder.append("\n");
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    resultBuilder.append(entry.getKey()).append(":").append((String) entry.getValue());
                }
                result = resultBuilder.toString();
                return result;
            }
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", (String) entry.getValue());
            }
            return result;
        }

        public String getLoginExist() {
            return getPrefix() +
                    (loginExist != null ? loginExist : Lang.all.errors.loginExist);
        }
    }
}

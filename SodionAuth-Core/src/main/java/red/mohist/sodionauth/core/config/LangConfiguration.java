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

    private String registerEmail;
    private String registerPassword;
    private String registerPasswordConfirm;
    private String loggedIn;
    private String success;
    private String session;
    private String lastLogin;
    private String lastLoginUnknown;
    private String needLogin;
    private ErrorsBean errors;

    public String getRegisterEmail(Map<String, String> data) {
        String result = getRegisterEmail();
        if (result == null) {
            StringBuilder resultBuilder = new StringBuilder("registerEmail");
            resultBuilder.append("\n");
            for (Map.Entry<String, String> entry : data.entrySet()) {
                resultBuilder.append(entry.getKey()).append(":").append(entry.getValue());
            }
            result = resultBuilder.toString();
            return result;
        }
        for (Map.Entry<String, String> entry : data.entrySet()) {
            result = result.replace("[" + entry.getKey() + "]", entry.getValue());
        }
        return result;
    }

    public String getRegisterEmail() {
        return registerEmail != null ? registerEmail : Lang.all.registerEmail;
    }

    public String getRegisterPassword(Map<String, String> data) {
        String result = getRegisterPassword();
        if (result == null) {
            StringBuilder resultBuilder = new StringBuilder("registerPassword");
            resultBuilder.append("\n");
            for (Map.Entry<String, String> entry : data.entrySet()) {
                resultBuilder.append(entry.getKey()).append(":").append(entry.getValue());
            }
            result = resultBuilder.toString();
            return result;
        }
        for (Map.Entry<String, String> entry : data.entrySet()) {
            result = result.replace("[" + entry.getKey() + "]", entry.getValue());
        }
        return result;
    }

    public String getRegisterPassword() {
        return registerPassword != null ? registerPassword : Lang.all.registerPassword;
    }

    public String getRegisterPasswordConfirm(Map<String, String> data) {
        String result = getRegisterPasswordConfirm();
        if (result == null) {
            StringBuilder resultBuilder = new StringBuilder("registerPasswordConfirm");
            resultBuilder.append("\n");
            for (Map.Entry<String, String> entry : data.entrySet()) {
                resultBuilder.append(entry.getKey()).append(":").append(entry.getValue());
            }
            result = resultBuilder.toString();
            return result;
        }
        for (Map.Entry<String, String> entry : data.entrySet()) {
            result = result.replace("[" + entry.getKey() + "]", entry.getValue());
        }
        return result;
    }

    public String getRegisterPasswordConfirm() {
        return registerPasswordConfirm != null ? registerPasswordConfirm : Lang.all.registerPasswordConfirm;
    }

    public String getLoggedIn(Map<String, String> data) {
        String result = getLoggedIn();
        if (result == null) {
            StringBuilder resultBuilder = new StringBuilder("loggedIn");
            resultBuilder.append("\n");
            for (Map.Entry<String, String> entry : data.entrySet()) {
                resultBuilder.append(entry.getKey()).append(":").append(entry.getValue());
            }
            result = resultBuilder.toString();
            return result;
        }
        for (Map.Entry<String, String> entry : data.entrySet()) {
            result = result.replace("[" + entry.getKey() + "]", entry.getValue());
        }
        return result;
    }

    public String getLoggedIn() {
        return loggedIn != null ? loggedIn : Lang.all.loggedIn;
    }

    public String getSuccess(Map<String, String> data) {
        String result = getSuccess();
        if (result == null) {
            StringBuilder resultBuilder = new StringBuilder("success");
            resultBuilder.append("\n");
            for (Map.Entry<String, String> entry : data.entrySet()) {
                resultBuilder.append(entry.getKey()).append(":").append(entry.getValue());
            }
            result = resultBuilder.toString();
            return result;
        }
        for (Map.Entry<String, String> entry : data.entrySet()) {
            result = result.replace("[" + entry.getKey() + "]", entry.getValue());
        }
        return result;
    }

    public String getSuccess() {
        return success != null ? success : Lang.all.success;
    }

    public String getSession(Map<String, String> data) {
        String result = getSession();
        if (result == null) {
            StringBuilder resultBuilder = new StringBuilder("session");
            resultBuilder.append("\n");
            for (Map.Entry<String, String> entry : data.entrySet()) {
                resultBuilder.append(entry.getKey()).append(":").append(entry.getValue());
            }
            result = resultBuilder.toString();
            return result;
        }
        for (Map.Entry<String, String> entry : data.entrySet()) {
            result = result.replace("[" + entry.getKey() + "]", entry.getValue());
        }
        return result;
    }

    public String getSession() {
        return session != null ? session : Lang.all.session;
    }

    public String getLastLogin(Map<String, String> data) {
        String result = getLastLogin();
        if (result == null) {
            StringBuilder resultBuilder = new StringBuilder("lastLogin");
            resultBuilder.append("\n");
            for (Map.Entry<String, String> entry : data.entrySet()) {
                resultBuilder.append(entry.getKey()).append(":").append(entry.getValue());
            }
            result = resultBuilder.toString();
            return result;
        }
        for (Map.Entry<String, String> entry : data.entrySet()) {
            result = result.replace("[" + entry.getKey() + "]", entry.getValue());
        }
        return result;
    }

    public String getLastLogin() {
        return lastLogin != null ? lastLogin : Lang.all.lastLogin;
    }

    public String getLastLoginUnknown(Map<String, String> data) {
        String result = getLastLoginUnknown();
        if (result == null) {
            StringBuilder resultBuilder = new StringBuilder("lastLoginUnknown");
            resultBuilder.append("\n");
            for (Map.Entry<String, String> entry : data.entrySet()) {
                resultBuilder.append(entry.getKey()).append(":").append(entry.getValue());
            }
            result = resultBuilder.toString();
            return result;
        }
        for (Map.Entry<String, String> entry : data.entrySet()) {
            result = result.replace("[" + entry.getKey() + "]", entry.getValue());
        }
        return result;
    }

    public String getLastLoginUnknown() {
        return lastLoginUnknown != null ? lastLoginUnknown : Lang.all.lastLoginUnknown;
    }

    public String getNeedLogin(Map<String, String> data) {
        String result = getNeedLogin();
        if (result == null) {
            StringBuilder resultBuilder = new StringBuilder("needLogin");
            resultBuilder.append("\n");
            for (Map.Entry<String, String> entry : data.entrySet()) {
                resultBuilder.append(entry.getKey()).append(":").append(entry.getValue());
            }
            result = resultBuilder.toString();
            return result;
        }
        for (Map.Entry<String, String> entry : data.entrySet()) {
            result = result.replace("[" + entry.getKey() + "]", entry.getValue());
        }
        return result;
    }

    public String getNeedLogin() {
        return needLogin != null ? needLogin : Lang.all.needLogin;
    }

    public ErrorsBean getErrors() {
        return errors != null ? errors : Lang.all.errors;
    }

    public static class ErrorsBean {
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

        public String getProxy(Map<String, String> data) {
            String result = getProxy();
            if (result == null) {
                StringBuilder resultBuilder = new StringBuilder("proxy");
                resultBuilder.append("\n");
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    resultBuilder.append(entry.getKey()).append(":").append(entry.getValue());
                }
                result = resultBuilder.toString();
                return result;
            }
            for (Map.Entry<String, String> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", entry.getValue());
            }
            return result;
        }

        public String getProxy() {
            return proxy != null ? proxy : Lang.all.errors.proxy;
        }

        public String getRateLimit(Map<String, String> data) {
            String result = getRateLimit();
            if (result == null) {
                StringBuilder resultBuilder = new StringBuilder("rateLimit");
                resultBuilder.append("\n");
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    resultBuilder.append(entry.getKey()).append(":").append(entry.getValue());
                }
                result = resultBuilder.toString();
                return result;
            }
            for (Map.Entry<String, String> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", entry.getValue());
            }
            return result;
        }

        public String getRateLimit() {
            return rateLimit != null ? rateLimit : Lang.all.errors.rateLimit;
        }

        public String getCountryLimit(Map<String, String> data) {
            String result = getCountryLimit();
            if (result == null) {
                StringBuilder resultBuilder = new StringBuilder("countryLimit");
                resultBuilder.append("\n");
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    resultBuilder.append(entry.getKey()).append(":").append(entry.getValue());
                }
                result = resultBuilder.toString();
                return result;
            }
            for (Map.Entry<String, String> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", entry.getValue());
            }
            return result;
        }

        public String getCountryLimit() {
            return countryLimit != null ? countryLimit : Lang.all.errors.countryLimit;
        }

        public String getEmail(Map<String, String> data) {
            String result = getEmail();
            if (result == null) {
                StringBuilder resultBuilder = new StringBuilder("email");
                resultBuilder.append("\n");
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    resultBuilder.append(entry.getKey()).append(":").append(entry.getValue());
                }
                result = resultBuilder.toString();
                return result;
            }
            for (Map.Entry<String, String> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", entry.getValue());
            }
            return result;
        }

        public String getEmail() {
            return email != null ? email : Lang.all.errors.email;
        }

        public String getServer(Map<String, String> data) {
            String result = getServer();
            if (result == null) {
                StringBuilder resultBuilder = new StringBuilder("server");
                resultBuilder.append("\n");
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    resultBuilder.append(entry.getKey()).append(":").append(entry.getValue());
                }
                result = resultBuilder.toString();
                return result;
            }
            for (Map.Entry<String, String> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", entry.getValue());
            }
            return result;
        }

        public String getServer() {
            return server != null ? server : Lang.all.errors.server;
        }

        public String getConfirm(Map<String, String> data) {
            String result = getConfirm();
            if (result == null) {
                StringBuilder resultBuilder = new StringBuilder("confirm");
                resultBuilder.append("\n");
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    resultBuilder.append(entry.getKey()).append(":").append(entry.getValue());
                }
                result = resultBuilder.toString();
                return result;
            }
            for (Map.Entry<String, String> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", entry.getValue());
            }
            return result;
        }

        public String getConfirm() {
            return confirm != null ? confirm : Lang.all.errors.confirm;
        }

        public String getHandle(Map<String, String> data) {
            String result = getHandle();
            if (result == null) {
                StringBuilder resultBuilder = new StringBuilder("handle");
                resultBuilder.append("\n");
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    resultBuilder.append(entry.getKey()).append(":").append(entry.getValue());
                }
                result = resultBuilder.toString();
                return result;
            }
            for (Map.Entry<String, String> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", entry.getValue());
            }
            return result;
        }

        public String getHandle() {
            return handle != null ? handle : Lang.all.errors.handle;
        }

        public String getPassword(Map<String, String> data) {
            String result = getPassword();
            if (result == null) {
                StringBuilder resultBuilder = new StringBuilder("password");
                resultBuilder.append("\n");
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    resultBuilder.append(entry.getKey()).append(":").append(entry.getValue());
                }
                result = resultBuilder.toString();
                return result;
            }
            for (Map.Entry<String, String> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", entry.getValue());
            }
            return result;
        }

        public String getPassword() {
            return password != null ? password : Lang.all.errors.password;
        }

        public String getKey(Map<String, String> data) {
            String result = getKey();
            if (result == null) {
                StringBuilder resultBuilder = new StringBuilder("key");
                resultBuilder.append("\n");
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    resultBuilder.append(entry.getKey()).append(":").append(entry.getValue());
                }
                result = resultBuilder.toString();
                return result;
            }
            for (Map.Entry<String, String> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", entry.getValue());
            }
            return result;
        }

        public String getKey() {
            return key != null ? key : Lang.all.errors.key;
        }

        public String getUrl(Map<String, String> data) {
            String result = getUrl();
            if (result == null) {
                StringBuilder resultBuilder = new StringBuilder("url");
                resultBuilder.append("\n");
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    resultBuilder.append(entry.getKey()).append(":").append(entry.getValue());
                }
                result = resultBuilder.toString();
                return result;
            }
            for (Map.Entry<String, String> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", entry.getValue());
            }
            return result;
        }

        public String getUrl() {
            return url != null ? url : Lang.all.errors.url;
        }

        public String getNoUser(Map<String, String> data) {
            String result = getNoUser();
            if (result == null) {
                StringBuilder resultBuilder = new StringBuilder("noUser");
                resultBuilder.append("\n");
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    resultBuilder.append(entry.getKey()).append(":").append(entry.getValue());
                }
                result = resultBuilder.toString();
                return result;
            }
            for (Map.Entry<String, String> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", entry.getValue());
            }
            return result;
        }

        public String getNoUser() {
            return noUser != null ? noUser : Lang.all.errors.noUser;
        }

        public String getTimeOut(Map<String, String> data) {
            String result = getTimeOut();
            if (result == null) {
                StringBuilder resultBuilder = new StringBuilder("timeOut");
                resultBuilder.append("\n");
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    resultBuilder.append(entry.getKey()).append(":").append(entry.getValue());
                }
                result = resultBuilder.toString();
                return result;
            }
            for (Map.Entry<String, String> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", entry.getValue());
            }
            return result;
        }

        public String getTimeOut() {
            return timeOut != null ? timeOut : Lang.all.errors.timeOut;
        }

        public String getNameIncorrect(Map<String, String> data) {
            String result = getNameIncorrect();
            if (result == null) {
                StringBuilder resultBuilder = new StringBuilder("nameIncorrect");
                resultBuilder.append("\n");
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    resultBuilder.append(entry.getKey()).append(":").append(entry.getValue());
                }
                result = resultBuilder.toString();
                return result;
            }
            for (Map.Entry<String, String> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", entry.getValue());
            }
            return result;
        }

        public String getNameIncorrect() {
            return nameIncorrect != null ? nameIncorrect : Lang.all.errors.nameIncorrect;
        }

        public String getUnknown(Map<String, String> data) {
            String result = getUnknown();
            if (result == null) {
                StringBuilder resultBuilder = new StringBuilder("unknown");
                resultBuilder.append("\n");
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    resultBuilder.append(entry.getKey()).append(":").append(entry.getValue());
                }
                result = resultBuilder.toString();
                return result;
            }
            for (Map.Entry<String, String> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", entry.getValue());
            }
            return result;
        }

        public String getUnknown() {
            return unknown != null ? unknown : Lang.all.errors.unknown;
        }

        public String getMailExist(Map<String, String> data) {
            String result = getMailExist();
            if (result == null) {
                StringBuilder resultBuilder = new StringBuilder("mailExist");
                resultBuilder.append("\n");
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    resultBuilder.append(entry.getKey()).append(":").append(entry.getValue());
                }
                result = resultBuilder.toString();
                return result;
            }
            for (Map.Entry<String, String> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", entry.getValue());
            }
            return result;
        }

        public String getMailExist() {
            return mailExist != null ? mailExist : Lang.all.errors.mailExist;
        }

        public String getUserExist(Map<String, String> data) {
            String result = getUserExist();
            if (result == null) {
                StringBuilder resultBuilder = new StringBuilder("userExist");
                resultBuilder.append("\n");
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    resultBuilder.append(entry.getKey()).append(":").append(entry.getValue());
                }
                result = resultBuilder.toString();
                return result;
            }
            for (Map.Entry<String, String> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", entry.getValue());
            }
            return result;
        }

        public String getUserExist() {
            return userExist != null ? userExist : Lang.all.errors.userExist;
        }

        public String getLoginExist(Map<String, String> data) {
            String result = getLoginExist();
            if (result == null) {
                StringBuilder resultBuilder = new StringBuilder("userExist");
                resultBuilder.append("\n");
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    resultBuilder.append(entry.getKey()).append(":").append(entry.getValue());
                }
                result = resultBuilder.toString();
                return result;
            }
            for (Map.Entry<String, String> entry : data.entrySet()) {
                result = result.replace("[" + entry.getKey() + "]", entry.getValue());
            }
            return result;
        }

        public String getLoginExist() {
            return loginExist != null ? loginExist : Lang.all.errors.loginExist;
        }
    }
}

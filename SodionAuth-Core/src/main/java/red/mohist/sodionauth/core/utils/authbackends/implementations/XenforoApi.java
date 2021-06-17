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

package red.mohist.sodionauth.core.utils.authbackends.implementations;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import red.mohist.sodionauth.core.utils.authbackends.AuthBackend;
import red.mohist.sodionauth.core.config.MainConfiguration;
import red.mohist.sodionauth.core.database.entities.AuthInfo;
import red.mohist.sodionauth.core.database.entities.User;
import red.mohist.sodionauth.core.services.Service;
import red.mohist.sodionauth.core.utils.Helper;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

public class XenforoApi extends AuthBackend {
    private final String url;
    private final String key;

    public XenforoApi(MainConfiguration.ApiBean.XenforoBean config) {
        super(config);
        this.url = config.url;
        this.key = config.key;
    }

    @Override
    public LoginResult login(User user, AuthInfo authInfo, String password) {
        try {
            JsonObject response = request("auth", ImmutableMap.of(
                    "login", user.getName(),
                    "password", password
            ));
            if (response.get("success") != null && response.get("success").getAsBoolean()) {
                response.get("user").getAsJsonObject().get("username").getAsString();
                if (response.get("user").getAsJsonObject()
                        .get("username").getAsString().equals(user.getName())) {
                    return LoginResult.success();
                } else {
                    return LoginResult.errorName().setCorrect(
                            response.get("user").getAsJsonObject().get("username").getAsString());
                }
            } else {
                JsonArray errors = response.get("errors").getAsJsonArray();
                if (errors.size() > 0) {
                    switch (errors.get(0).getAsJsonObject().get("code").getAsString()) {
                        case "incorrect_password":
                            return LoginResult.errorPassword();
                        case "requested_user_x_not_found":
                            return LoginResult.noUser();
                        default:
                            throw new Exception("Unknown Xenforo error: " +
                                    errors.get(0).getAsJsonObject().get("code").getAsString());
                    }
                } else {
                    return LoginResult.errorServer();
                }
            }
        } catch (Exception e) {
            Helper.getLogger().warn(
                    "Error while login player " + user.getName() + " in xenforo " + url, e);
            return LoginResult.errorServer();
        }
    }

    @Override
    public RegisterResult register(User user, String password) {
        try {
            JsonObject response = request("users", ImmutableMap.of(
                    "username", user.getName(),
                    "email", user.getEmail(),
                    "password", password
            ));
            if (response.get("success") != null && response.get("success").getAsBoolean()) {
                return RegisterResult.SUCCESS;
            } else {
                JsonArray errors = response.get("errors").getAsJsonArray();
                if (errors.size() > 0) {
                    switch (errors.get(0).getAsJsonObject().get("code").getAsString()) {
                        case "usernames_must_be_unique":
                            return RegisterResult.NAME_EXIST;
                        case "please_enter_valid_email":
                            return RegisterResult.ERROR_EMAIL;
                        case "email_addresses_must_be_unique":
                            return RegisterResult.EMAIL_EXIST;
                        default:
                            throw new Exception("Unknown Xenforo error: " +
                                    errors.get(0).getAsJsonObject().get("code").getAsString());
                    }
                } else {
                    return RegisterResult.ERROR_SERVER;
                }
            }
        } catch (Exception e) {
            Helper.getLogger().warn(
                    "Error while register player " + user.getName() + " to xenforo " + url, e);
            return RegisterResult.ERROR_SERVER;
        }
    }

    @Override
    public GetResult get(User user) {
        try {
            JsonObject response = request("users/find-name",
                    ImmutableMap.of("username", user.getName()), true);
            if (response.get("exact").isJsonNull()) {
                return GetResult.noSuchUser();
            }
            return GetResult.success()
                    .setEmail(response.getAsJsonObject("exact").get("email").getAsString())
                    .setName(response.getAsJsonObject("exact").get("username").getAsString());
        } catch (Exception e) {
            Helper.getLogger().warn(
                    "Error while get player " + user.getName() + " to xenforo " + url, e);
            return GetResult.errorServer();
        }
    }

    protected JsonObject request(String path, Map<String, String> data) throws IOException {
        return request(path, data, false);
    }

    protected JsonObject request(String path) throws IOException {
        return request(path, null, true);
    }

    protected JsonObject request(String path, Map<String, String> data, boolean isGet) throws IOException {
        HttpUriRequest request;
        if (isGet) {
            StringBuilder requestUrl = new StringBuilder(url + "/" + path);
            if (data != null) {
                requestUrl.append(url.contains("?") ? "&" : "?");
                String[] keys = data.keySet().toArray(new String[]{});
                for (int i = 0; i < keys.length; i++) {
                    if (i != 0) {
                        requestUrl.append("&");
                    }
                    requestUrl.append(URLEncoder.encode(keys[i], "UTF-8"));
                    requestUrl.append("=");
                    requestUrl.append(URLEncoder.encode(data.get(keys[i]), "UTF-8"));
                }
            }
            request = new HttpGet(requestUrl.toString());
        } else {
            HttpPost postRequest = new HttpPost(url + "/" + path);
            Form form = Form.form();
            data.forEach(form::add);
            postRequest.setEntity(new UrlEncodedFormEntity(form.build()));
            request = postRequest;
        }
        request.addHeader("XF-Api-Key", key);
        request.addHeader("XF-Api-User", "1");
        return Service.httpClient.executeAsJson(request);
    }
}

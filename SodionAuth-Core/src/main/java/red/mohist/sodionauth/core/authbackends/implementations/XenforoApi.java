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

package red.mohist.sodionauth.core.authbackends.implementations;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import red.mohist.sodionauth.core.authbackends.AuthBackend;
import red.mohist.sodionauth.core.config.MainConfiguration;
import red.mohist.sodionauth.core.database.entities.AuthInfo;
import red.mohist.sodionauth.core.database.entities.User;
import red.mohist.sodionauth.core.services.Service;
import red.mohist.sodionauth.core.utils.Helper;
import red.mohist.sodionauth.core.utils.Lang;
import red.mohist.sodionauth.libs.http.HttpEntity;
import red.mohist.sodionauth.libs.http.client.entity.UrlEncodedFormEntity;
import red.mohist.sodionauth.libs.http.client.fluent.Form;
import red.mohist.sodionauth.libs.http.client.methods.*;
import red.mohist.sodionauth.libs.http.util.EntityUtils;

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
            JsonObject response = request("auth",ImmutableMap.of(
                    "login", user.getName(),
                    "password",password
            ));
            if (response.get("success") != null && response.get("success").getAsBoolean()) {
                response.get("user").getAsJsonObject().get("username").getAsString();
                if (response.get("user").getAsJsonObject()
                        .get("username").getAsString().equals(user.getName())) {
                    return LoginResult.SUCCESS;
                } else {
                    return LoginResult.ERROR_NAME.setCorrect("ss");
                }
            } else {
                JsonArray errors = response.get("errors").getAsJsonArray();
                if (errors.size() > 0) {
                    switch (errors.get(0).getAsJsonObject().get("code").getAsString()) {
                        case "incorrect_password":
                            return LoginResult.ERROR_PASSWORD;
                        case "requested_user_x_not_found":
                            return LoginResult.NO_USER;
                        default:
                            throw new Exception("Unknown Xenforo error: " +
                                    errors.get(0).getAsJsonObject().get("code").getAsString());
                    }
                } else {
                    return LoginResult.ERROR_SERVER;
                }
            }
        } catch (Exception e) {
            Helper.getLogger().warn(
                    "Error while login player " + user.getName() + " in xenforo "+url, e);
            return LoginResult.ERROR_SERVER;
        }
    }

    @Override
    public RegisterResult register(User user, String password) {
        try {
            JsonObject response = request("user",ImmutableMap.of(
                    "username", user.getName(),
                    "email", user.getEmail(),
                    "password",password
            ));
            if (response.get("success") != null && response.get("success").getAsBoolean()) {
                return RegisterResult.SUCCESS;
            }else{
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
                    "Error while register player " + user.getName() + " to xenforo "+url, e);
            return RegisterResult.ERROR_SERVER;
        }
    }

    @Override
    public GetResult get(User user) {
        try {
            JsonObject response = request("users/find-name?username=" +
                    URLEncoder.encode(user.getName(), "UTF-8"));
            if (response.get("exact").isJsonNull()) {
                return GetResult.NO_SUCH_USER;
            }
            if (!response.getAsJsonObject("exact").get("username").getAsString().equals(user.getName())) {
                return GetResult.ERROR_NAME.setCorrect(
                        response.getAsJsonObject("exact").get("username").getAsString());
            }
            return GetResult.SUCCESS;
        } catch (Exception e) {
            Helper.getLogger().warn(
                    "Error while get player " + user.getName() + " to xenforo "+url, e);
            return GetResult.ERROR_SERVER;
        }
    }

    protected JsonObject request(String path, Map<String,String> data) throws IOException {
        return request(path,data,false);
    }

    protected JsonObject request(String path) throws IOException {
        return request(path,null,false);
    }

    protected JsonObject request(String path, Map<String,String> data , boolean isGet) throws IOException {
        HttpUriRequest request;
        if(isGet){
            request = new HttpGet(url + "/" + path);
        }else {
            HttpPost postRequest = new HttpPost(url + "/" + path);
            Form form = Form.form();
            data.forEach(form::add);
            postRequest.setEntity(new UrlEncodedFormEntity(form.build()));
            request = postRequest;
        }
        request.addHeader("XF-Api-Key", key);
        request.addHeader("XF-Api-User", "1");
        CloseableHttpResponse response = Service.httpClient.execute(request);
        if(response == null){
            throw new IOException("No response");
        }
        switch (response.getStatusLine().getStatusCode()){
            case 200:
            case 400:
                break;
            case 401:
            case 403:
                Helper.getLogger().warn(
                        Lang.def.errors.getKey(ImmutableMap.of(
                                "key", key)));
                throw new IOException("Server returns status code "+response.getStatusLine().getStatusCode());
            case 404:
                Helper.getLogger().warn(
                        Lang.def.errors.getUrl(ImmutableMap.of(
                                "url", url)));
                throw new IOException("Server returns status code "+response.getStatusLine().getStatusCode());
            default:
                throw new IOException("Server returns status code "+response.getStatusLine().getStatusCode());
        }
        HttpEntity entity = response.getEntity();
        if(entity == null){
            throw new IOException("Server returns no entity");
        }
        String content = EntityUtils.toString(entity);
        if(content == null || content.equals("")){
            throw new IOException("Server returns empty entity");
        }
        response.close();
        JsonObject result;
        result = new Gson().fromJson(content,JsonObject.class);
        if(result == null){
            throw new JsonSyntaxException("Server returned: "+content);
        }
        return result;
    }
}

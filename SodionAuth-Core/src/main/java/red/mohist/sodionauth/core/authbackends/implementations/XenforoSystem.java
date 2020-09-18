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

package red.mohist.sodionauth.core.authbackends.implementations;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import red.mohist.sodionauth.core.SodionAuthCore;
import red.mohist.sodionauth.core.authbackends.AuthBackendSystem;
import red.mohist.sodionauth.core.enums.ResultType;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.utils.Helper;
import red.mohist.sodionauth.core.utils.Lang;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URLEncoder;

@SuppressWarnings("unused")
public class XenforoSystem implements AuthBackendSystem {

    private final String url;
    private final String key;

    public XenforoSystem(String url, String key) {
        this.url = url;
        this.key = key;
    }

    @Nonnull
    @Override
    public ResultType register(AbstractPlayer player, String password, String email) {
        try {
            ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                if (status == 200 || status == 400) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else if (status == 401) {
                    Helper.getLogger().warn(
                            Lang.def.getErrors().getKey(ImmutableMap.of(
                                    "key", key)));
                } else if (status == 404) {
                    Helper.getLogger().warn(
                            Lang.def.getErrors().getUrl(ImmutableMap.of(
                                    "url", url)));
                }
                return null;
            };

            HttpPost request = new HttpPost(url + "/users");
            Form form = Form.form();
            form.add("username", player.getName());
            form.add("password", password);
            form.add("email", email);
            request.setEntity(new UrlEncodedFormEntity(form.build()));
            request.addHeader("XF-Api-Key", key);
            request.addHeader("XF-Api-User", "1");
            CloseableHttpResponse response = SodionAuthCore.instance.getHttpClient().execute(request);
            String result = responseHandler.handleResponse(response);
            response.close();

            if (result == null) {
                return ResultType.SERVER_ERROR;
            }
            // FIXME Stop using JsonParser
            Helper.getLogger().warn("XenforoSystem is still using deprecated method to parse json");
            JsonParser parse = new JsonParser();
            JsonObject json;
            try {
                json = parse.parse(result).getAsJsonObject();
            } catch (JsonSyntaxException e) {
                Helper.getLogger().warn(result);
                e.printStackTrace();
                return ResultType.SERVER_ERROR;
            }
            if (json == null) {
                return ResultType.SERVER_ERROR;
            }
            if (json.get("success") != null && json.get("success").getAsBoolean()) {
                return ResultType.OK;
            } else {
                JsonArray errors = json.get("errors").getAsJsonArray();
                if (errors.size() > 0) {
                    switch (errors.get(0).getAsJsonObject().get("code").getAsString()) {
                        case "usernames_must_be_unique":
                            return ResultType.USER_EXIST;
                        case "please_enter_valid_email":
                            return ResultType.EMAIL_WRONG;
                        case "email_addresses_must_be_unique":
                            return ResultType.EMAIL_EXIST;
                        default:
                            return ResultType.UNKNOWN
                                    .inheritedObject(
                                            "code",
                                            errors.get(0).getAsJsonObject().get("code").getAsString())
                                    .inheritedObject(
                                            "message",
                                            errors.get(0).getAsJsonObject().get("message").getAsString());
                    }
                } else {
                    return ResultType.SERVER_ERROR;
                }
            }
        } catch (Exception e) {
            Helper.getLogger().warn(
                    "Error while register player " + player.getName() + " data", e);
            return ResultType.SERVER_ERROR;
        }
    }

    @Nonnull
    @Override
    public ResultType login(AbstractPlayer player, String password) {
        try {
            ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                if (status == 200 || status == 400) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else if (status == 403) {
                    Helper.getLogger().warn(
                            Lang.def.getErrors().getKey(ImmutableMap.of(
                                    "key", key)));
                } else if (status == 404) {
                    Helper.getLogger().warn(
                            Lang.def.getErrors().getUrl(ImmutableMap.of(
                                    "url", url)));
                }
                return null;
            };

            HttpPost request = new HttpPost(url + "/auth");
            Form form = Form.form();
            form.add("login", player.getName());
            form.add("password", password);
            request.addHeader("XF-Api-Key", key);
            CloseableHttpResponse response = SodionAuthCore.instance.getHttpClient().execute(request);
            String result = responseHandler.handleResponse(response);

            if (result == null) {
                return ResultType.SERVER_ERROR;
            }
            // FIXME Stop using JsonParser
            Helper.getLogger().warn("XenforoSystem is still using deprecated method to parse json");
            JsonParser parse = new JsonParser();
            JsonObject json;
            try {
                json = parse.parse(result).getAsJsonObject();
            } catch (JsonSyntaxException e) {
                Helper.getLogger().warn(result);
                e.printStackTrace();
                return ResultType.SERVER_ERROR;
            }
            if (json == null) {
                return ResultType.SERVER_ERROR;
            }
            if (json.get("success") != null && json.get("success").getAsBoolean()) {
                json.get("user").getAsJsonObject().get("username").getAsString();
                if (json.get("user").getAsJsonObject().get("username").getAsString().equals(player.getName())) {
                    return ResultType.OK;
                } else {
                    return ResultType.ERROR_NAME
                            .inheritedObject(
                                    "correct",
                                    json.getAsJsonObject("exact").get("username").getAsString());
                }
            } else {
                JsonArray errors = json.get("errors").getAsJsonArray();
                if (errors.size() > 0) {
                    switch (errors.get(0).getAsJsonObject().get("code").getAsString()) {
                        case "incorrect_password":
                            return ResultType.PASSWORD_INCORRECT;
                        case "requested_user_x_not_found":
                            return ResultType.NO_USER;
                        default:
                            return ResultType.UNKNOWN
                                    .inheritedObject(
                                            "code",
                                            errors.get(0).getAsJsonObject().get("code").getAsString())
                                    .inheritedObject(
                                            "message",
                                            errors.get(0).getAsJsonObject().get("message").getAsString());
                    }
                } else {
                    return ResultType.SERVER_ERROR;
                }
            }
        } catch (Exception e) {
            Helper.getLogger().warn("Error while checking player " + player.getName() + " data", e);
            return ResultType.SERVER_ERROR;
        }
    }

    @Nonnull
    @Override
    public ResultType join(AbstractPlayer player) {
        ResponseHandler<String> responseHandler = response -> {
            int status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            } else if (status == 401) {
                Helper.getLogger().warn(
                        Lang.def.getErrors().getKey(ImmutableMap.of(
                                "key", key)));
            } else if (status == 404) {
                Helper.getLogger().warn(
                        Lang.def.getErrors().getUrl(ImmutableMap.of(
                                "url", url)));
            }
            return null;
        };
        String result;
        try {
            HttpGet request = new HttpGet(url + "/users/find-name?username=" +
                    URLEncoder.encode(player.getName(), "UTF-8"));
            request.addHeader("XF-Api-Key", key);
            CloseableHttpResponse response = SodionAuthCore.instance.getHttpClient().execute(request);
            result = responseHandler.handleResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultType.SERVER_ERROR;
        }
        if (result == null) {
            new ClientProtocolException("Unexpected response: null").printStackTrace();
            return ResultType.SERVER_ERROR;
        }
        // FIXME Stop using JsonParser
        Helper.getLogger().warn("XenforoSystem is still using deprecated method to parse json");
        JsonParser parse = new JsonParser();
        JsonObject json;
        try {
            json = parse.parse(result).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            Helper.getLogger().warn(result);
            e.printStackTrace();
            return ResultType.SERVER_ERROR;
        }
        if (json == null) {
            new ClientProtocolException("Unexpected json: null").printStackTrace();
            return ResultType.SERVER_ERROR;
        }
        if (json.get("exact").isJsonNull()) {
            return ResultType.NO_USER;
        }
        if (!json.getAsJsonObject("exact").get("username").getAsString().equals(player.getName())) {
            return ResultType.ERROR_NAME
                    .inheritedObject(
                            "correct",
                            json.getAsJsonObject("exact").get("username").getAsString());
        }
        return ResultType.OK;
    }

    @Nonnull
    @Override
    public ResultType loginEmail(String email, String password) {
        try {
            ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                if (status == 200 || status == 400) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else if (status == 403) {
                    Helper.getLogger().warn(
                            Lang.def.getErrors().getKey(ImmutableMap.of(
                                    "key", key)));
                } else if (status == 404) {
                    Helper.getLogger().warn(
                            Lang.def.getErrors().getUrl(ImmutableMap.of(
                                    "url", url)));
                }
                return null;
            };

            HttpPost request = new HttpPost(url + "/auth");
            Form form = Form.form();
            form.add("login", email);
            form.add("password", password);
            request.addHeader("XF-Api-Key", key);
            CloseableHttpResponse response = SodionAuthCore.instance.getHttpClient().execute(request);
            String result = responseHandler.handleResponse(response);

            if (result == null) {
                return ResultType.SERVER_ERROR;
            }
            JsonObject json;
            try {
                json = new Gson().fromJson(result, JsonObject.class);
            } catch (JsonSyntaxException e) {
                Helper.getLogger().warn(result);
                e.printStackTrace();
                return ResultType.SERVER_ERROR;
            }
            if (json == null) {
                return ResultType.SERVER_ERROR;
            }
            if (json.get("success") != null && json.get("success").getAsBoolean()) {
                json.get("user").getAsJsonObject().get("username").getAsString();
                return ResultType.OK
                        .inheritedObject(
                                "correct",
                                json.getAsJsonObject("exact").get("username").getAsString());
            } else {
                JsonArray errors = json.get("errors").getAsJsonArray();
                if (errors.size() > 0) {
                    switch (errors.get(0).getAsJsonObject().get("code").getAsString()) {
                        case "incorrect_password":
                            return ResultType.PASSWORD_INCORRECT;
                        case "requested_user_x_not_found":
                            return ResultType.NO_USER;
                        default:
                            return ResultType.UNKNOWN
                                    .inheritedObject(
                                            "code",
                                            errors.get(0).getAsJsonObject().get("code").getAsString())
                                    .inheritedObject(
                                            "message",
                                            errors.get(0).getAsJsonObject().get("message").getAsString());
                    }
                } else {
                    return ResultType.SERVER_ERROR;
                }
            }
        } catch (Exception e) {
            Helper.getLogger().warn("Error while checking player " + email + " data", e);
            return ResultType.SERVER_ERROR;
        }
    }

}

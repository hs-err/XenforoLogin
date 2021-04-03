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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import red.mohist.sodionauth.core.authbackends.AuthBackendSystem;
import red.mohist.sodionauth.core.enums.ResultType;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.services.Service;
import red.mohist.sodionauth.core.utils.Helper;
import red.mohist.sodionauth.core.utils.Lang;
import red.mohist.sodionauth.libs.http.HttpEntity;
import red.mohist.sodionauth.libs.http.client.ClientProtocolException;
import red.mohist.sodionauth.libs.http.client.ResponseHandler;
import red.mohist.sodionauth.libs.http.client.entity.UrlEncodedFormEntity;
import red.mohist.sodionauth.libs.http.client.methods.CloseableHttpResponse;
import red.mohist.sodionauth.libs.http.client.methods.HttpPost;
import red.mohist.sodionauth.libs.http.util.EntityUtils;
import red.mohist.sodionauth.libs.http.client.fluent.Form;

import javax.annotation.Nonnull;
import java.io.IOException;

public class WebSystem implements AuthBackendSystem {
    private final String url;
    private final String key;

    public WebSystem(String url, String key) {
        this.url = url;
        this.key = key;
    }

    @Nonnull
    @Override
    public ResultType register(AbstractPlayer player, String password, String email) {
        ResponseHandler<String> responseHandler = response -> {
            int status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            } else if (status == 404) {
                Helper.getLogger().warn(
                        Lang.def.getErrors().getUrl(ImmutableMap.of(
                                "url", url)));
            }
            return null;
        };
        String result;
        try {
            HttpPost request = new HttpPost(url);
            Form form = Form.form();
            form.add("action", "register");
            form.add("email", email);
            form.add("username", player.getName());
            form.add("password", password);
            request.setEntity(new UrlEncodedFormEntity(form.build()));
            request.addHeader("SodionAuth-Key", key);
            CloseableHttpResponse response = Service.httpClient.execute(request);
            result = responseHandler.handleResponse(response);
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
            return ResultType.SERVER_ERROR;
        }
        if (result == null) {
            new ClientProtocolException("Unexpected response: null").printStackTrace();
            return ResultType.SERVER_ERROR;
        }
        JsonParser parse = new JsonParser();
        JsonObject json;
        try {
            json = parse.parse(result).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            Helper.getLogger().warn("Json parse null.");
            Helper.getLogger().warn(result);
            return ResultType.SERVER_ERROR;
        }
        if (json == null) {
            Helper.getLogger().warn("Json parse null.");
            Helper.getLogger().warn(result);
            return ResultType.SERVER_ERROR;
        }
        if (json.get("result") == null) {
            Helper.getLogger().warn(".result must be required.");
            Helper.getLogger().warn(result);
            return ResultType.SERVER_ERROR;
        }
        if (json.get("result").getAsString() == null) {
            Helper.getLogger().warn(".result must can be cast to string.");
            Helper.getLogger().warn(result);
            return ResultType.SERVER_ERROR;
        }
        switch (json.get("result").getAsString()) {
            case "ok":
                return ResultType.OK;
            case "user_exist":
                return ResultType.USER_EXIST;
            case "email_wrong":
                return ResultType.EMAIL_WRONG;
            case "email_exist":
                return ResultType.EMAIL_EXIST;
            case "unknown":
                if (json.get("code") == null) {
                    Helper.getLogger().warn("if .result is unknown,.message must be required.");
                    Helper.getLogger().warn(result);
                    return ResultType.SERVER_ERROR;
                }
                if (json.get("code").getAsString() == null) {
                    Helper.getLogger().warn(".message must can be cast to string.");
                    Helper.getLogger().warn(result);
                    return ResultType.SERVER_ERROR;
                }
                if (json.get("message") == null) {
                    Helper.getLogger().warn("if .result is unknown,.message must be required.");
                    Helper.getLogger().warn(result);
                    return ResultType.SERVER_ERROR;
                }
                if (json.get("message").getAsString() == null) {
                    Helper.getLogger().warn(".message must can be cast to string.");
                    Helper.getLogger().warn(result);
                    return ResultType.SERVER_ERROR;
                }
                return ResultType.UNKNOWN
                        .inheritedObject("code", json.get("code").getAsString())
                        .inheritedObject("message", json.get("message").getAsString());
            default:
                Helper.getLogger().warn(".result only can choose between ok,name_incorrect,no_user.");
                Helper.getLogger().warn(result);
                return ResultType.SERVER_ERROR;
        }
    }

    @Nonnull
    @Override
    public ResultType login(AbstractPlayer player, String password) {
        ResponseHandler<String> responseHandler = response -> {
            int status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            } else if (status == 404) {
                Helper.getLogger().warn(
                        Lang.def.getErrors().getUrl(ImmutableMap.of(
                                "url", url)));
            }
            return null;
        };
        String result;
        try {
            HttpPost request = new HttpPost(url);
            Form form = Form.form();
            form.add("action", "login");
            form.add("username", player.getName());
            form.add("password", password);
            request.setEntity(new UrlEncodedFormEntity(form.build()));
            request.addHeader("SodionAuth-Key", key);
            CloseableHttpResponse response = Service.httpClient.execute(request);
            result = responseHandler.handleResponse(response);
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
            return ResultType.SERVER_ERROR;
        }
        if (result == null) {
            new ClientProtocolException("Unexpected response: null").printStackTrace();
            return ResultType.SERVER_ERROR;
        }
        JsonParser parse = new JsonParser();
        JsonObject json;
        try {
            json = parse.parse(result).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            Helper.getLogger().warn("Json parse null.");
            Helper.getLogger().warn(result);
            return ResultType.SERVER_ERROR;
        }
        if (json == null) {
            Helper.getLogger().warn("Json parse null.");
            Helper.getLogger().warn(result);
            return ResultType.SERVER_ERROR;
        }
        if (json.get("result") == null) {
            Helper.getLogger().warn(".result must be required.");
            Helper.getLogger().warn(result);
            return ResultType.SERVER_ERROR;
        }
        if (json.get("result").getAsString() == null) {
            Helper.getLogger().warn(".result must can be cast to string.");
            Helper.getLogger().warn(result);
            return ResultType.SERVER_ERROR;
        }
        switch (json.get("result").getAsString()) {
            case "ok":
                return ResultType.OK;
            case "name_incorrect":
                if (json.get("correct") == null) {
                    Helper.getLogger().warn("if .result is name_incorrect,.correct must be required.");
                    Helper.getLogger().warn(result);
                    return ResultType.SERVER_ERROR;
                }
                if (json.get("correct").getAsString() == null) {
                    Helper.getLogger().warn(".correct must can be cast to string.");
                    Helper.getLogger().warn(result);
                    return ResultType.SERVER_ERROR;
                }
                return ResultType.ERROR_NAME.inheritedObject(
                        "correct", json.get("correct").getAsString());
            case "no_user":
                return ResultType.NO_USER;
            case "password_incorrect":
                return ResultType.PASSWORD_INCORRECT;
            case "unknown":
                if (json.get("code") == null) {
                    Helper.getLogger().warn("if .result is unknown,.message must be required.");
                    Helper.getLogger().warn(result);
                    return ResultType.SERVER_ERROR;
                }
                if (json.get("code").getAsString() == null) {
                    Helper.getLogger().warn(".message must can be cast to string.");
                    Helper.getLogger().warn(result);
                    return ResultType.SERVER_ERROR;
                }
                if (json.get("message") == null) {
                    Helper.getLogger().warn("if .result is unknown,.message must be required.");
                    Helper.getLogger().warn(result);
                    return ResultType.SERVER_ERROR;
                }
                if (json.get("message").getAsString() == null) {
                    Helper.getLogger().warn(".message must can be cast to string.");
                    Helper.getLogger().warn(result);
                    return ResultType.SERVER_ERROR;
                }
                return ResultType.UNKNOWN.inheritedObject("code", json.get("code").getAsString())
                        .inheritedObject("message", json.get("message").getAsString());
            default:
                Helper.getLogger().warn(".result only can choose between ok,name_incorrect,no_user.");
                Helper.getLogger().warn(result);
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
            } else if (status == 404) {
                Helper.getLogger().warn(
                        Lang.def.getErrors().getUrl(ImmutableMap.of(
                                "url", url)));
            }
            return null;
        };
        String result;
        try {
            HttpPost request = new HttpPost(url);
            Form form = Form.form();
            form.add("action", "join");
            form.add("username", player.getName());
            request.setEntity(new UrlEncodedFormEntity(form.build()));
            request.addHeader("SodionAuth-Key", key);
            CloseableHttpResponse response = Service.httpClient.execute(request);
            result = responseHandler.handleResponse(response);
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
            return ResultType.SERVER_ERROR;
        }
        if (result == null) {
            new ClientProtocolException("Unexpected response: null").printStackTrace();
            return ResultType.SERVER_ERROR;
        }
        JsonParser parse = new JsonParser();
        JsonObject json;
        try {
            json = parse.parse(result).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            Helper.getLogger().warn("Json parse null.");
            Helper.getLogger().warn(result);
            return ResultType.SERVER_ERROR;
        }
        if (json == null) {
            Helper.getLogger().warn("Json parse null.");
            Helper.getLogger().warn(result);
            return ResultType.SERVER_ERROR;
        }
        if (json.get("result") == null) {
            Helper.getLogger().warn(".result must be required.");
            Helper.getLogger().warn(result);
            return ResultType.SERVER_ERROR;
        }
        if (json.get("result").getAsString() == null) {
            Helper.getLogger().warn(".result must can be cast to string.");
            Helper.getLogger().warn(result);
            return ResultType.SERVER_ERROR;
        }
        switch (json.get("result").getAsString()) {
            case "ok":
                return ResultType.OK;
            case "name_incorrect":
                if (json.get("correct") == null) {
                    Helper.getLogger().warn("if .result is name_incorrect,.correct must be required.");
                    Helper.getLogger().warn(result);
                    return ResultType.SERVER_ERROR;
                }
                if (json.get("correct").getAsString() == null) {
                    Helper.getLogger().warn(".correct must can be cast to string.");
                    Helper.getLogger().warn(result);
                    return ResultType.SERVER_ERROR;
                }
                return ResultType.ERROR_NAME.inheritedObject(
                        "correct", json.get("correct").getAsString());
            case "no_user":
                return ResultType.NO_USER;
            default:
                Helper.getLogger().warn(".result only can choose between ok,name_incorrect,no_user.");
                Helper.getLogger().warn(result);
                return ResultType.SERVER_ERROR;
        }
    }

    @Nonnull
    @Override
    public ResultType loginEmail(String email, String password) {
        ResponseHandler<String> responseHandler = response -> {
            int status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            } else if (status == 404) {
                Helper.getLogger().warn(
                        Lang.def.getErrors().getUrl(ImmutableMap.of(
                                "url", url)));
            }
            return null;
        };
        String result;
        try {
            HttpPost request = new HttpPost(url);
            Form form = Form.form();
            form.add("action", "loginEmail");
            form.add("email", email);
            form.add("password", password);
            request.setEntity(new UrlEncodedFormEntity(form.build()));
            request.addHeader("SodionAuth-Key", key);
            CloseableHttpResponse response = Service.httpClient.execute(request);
            result = responseHandler.handleResponse(response);
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
            return ResultType.SERVER_ERROR;
        }
        if (result == null) {
            new ClientProtocolException("Unexpected response: null").printStackTrace();
            return ResultType.SERVER_ERROR;
        }
        JsonParser parse = new JsonParser();
        JsonObject json;
        try {
            json = parse.parse(result).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            Helper.getLogger().warn("Json parse null.");
            Helper.getLogger().warn(result);
            return ResultType.SERVER_ERROR;
        }
        if (json == null) {
            Helper.getLogger().warn("Json parse null.");
            Helper.getLogger().warn(result);
            return ResultType.SERVER_ERROR;
        }
        if (json.get("result") == null) {
            Helper.getLogger().warn(".result must be required.");
            Helper.getLogger().warn(result);
            return ResultType.SERVER_ERROR;
        }
        if (json.get("result").getAsString() == null) {
            Helper.getLogger().warn(".result must can be cast to string.");
            Helper.getLogger().warn(result);
            return ResultType.SERVER_ERROR;
        }
        switch (json.get("result").getAsString()) {
            case "ok":
                if (json.get("correct") == null) {
                    Helper.getLogger().warn("if .result = ok, .correct must be required.");
                    Helper.getLogger().warn(result);
                    return ResultType.SERVER_ERROR;
                }
                if (json.get("correct").getAsString() == null) {
                    Helper.getLogger().warn(".correct must can be cast to string.");
                    Helper.getLogger().warn(result);
                    return ResultType.SERVER_ERROR;
                }
                return ResultType.OK
                        .inheritedObject("correct", json.get("correct").getAsString());
            case "no_user":
                return ResultType.NO_USER;
            case "password_incorrect":
                return ResultType.PASSWORD_INCORRECT;
            case "unknown":
                if (json.get("code") == null) {
                    Helper.getLogger().warn("if .result is unknown,.message must be required.");
                    Helper.getLogger().warn(result);
                    return ResultType.SERVER_ERROR;
                }
                if (json.get("code").getAsString() == null) {
                    Helper.getLogger().warn(".message must can be cast to string.");
                    Helper.getLogger().warn(result);
                    return ResultType.SERVER_ERROR;
                }
                if (json.get("message") == null) {
                    Helper.getLogger().warn("if .result is unknown,.message must be required.");
                    Helper.getLogger().warn(result);
                    return ResultType.SERVER_ERROR;
                }
                if (json.get("message").getAsString() == null) {
                    Helper.getLogger().warn(".message must can be cast to string.");
                    Helper.getLogger().warn(result);
                    return ResultType.SERVER_ERROR;
                }
                return ResultType.UNKNOWN.inheritedObject("code", json.get("code").getAsString())
                        .inheritedObject("message", json.get("message").getAsString());
            default:
                Helper.getLogger().warn(".result only can choose between ok,name_incorrect,no_user.");
                Helper.getLogger().warn(result);
                return ResultType.SERVER_ERROR;
        }
    }
}

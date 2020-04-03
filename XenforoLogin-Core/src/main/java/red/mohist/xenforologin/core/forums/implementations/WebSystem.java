/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.core.forums.implementations;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;
import red.mohist.xenforologin.core.XenforoLoginCore;
import red.mohist.xenforologin.core.enums.ResultType;
import red.mohist.xenforologin.core.forums.ForumSystem;
import red.mohist.xenforologin.core.modules.AbstractPlayer;

import javax.annotation.Nonnull;
import java.io.IOException;

public class WebSystem implements ForumSystem {
    private String url;
    private String key;

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
                XenforoLoginCore.instance.api.getLogger().warning(
                        XenforoLoginCore.instance.langFile("errors.url", ImmutableMap.of(
                                "url", url)));
            }
            return null;
        };
        String result;
        try {
            result = Request.Post(url)
                    .bodyForm(Form.form().add("action", "register")
                            .add("email", email)
                            .add("username", player.getName())
                            .add("password", password).build())
                    .addHeader("XenforoLogin-Key", key)
                    .execute().handleResponse(responseHandler);
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
            XenforoLoginCore.instance.api.getLogger().warning("Json parse null.");
            XenforoLoginCore.instance.api.getLogger().warning(result);
            return ResultType.SERVER_ERROR;
        }
        if (json == null) {
            XenforoLoginCore.instance.api.getLogger().warning("Json parse null.");
            XenforoLoginCore.instance.api.getLogger().warning(result);
            return ResultType.SERVER_ERROR;
        }
        if (json.get("result") == null) {
            XenforoLoginCore.instance.api.getLogger().warning(".result must be required.");
            XenforoLoginCore.instance.api.getLogger().warning(result);
            return ResultType.SERVER_ERROR;
        }
        if (json.get("result").getAsString() == null) {
            XenforoLoginCore.instance.api.getLogger().warning(".result must can be cast to string.");
            XenforoLoginCore.instance.api.getLogger().warning(result);
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
                    XenforoLoginCore.instance.api.getLogger().warning("if .result is unknown,.message must be required.");
                    XenforoLoginCore.instance.api.getLogger().warning(result);
                    return ResultType.SERVER_ERROR;
                }
                if (json.get("code").getAsString() == null) {
                    XenforoLoginCore.instance.api.getLogger().warning(".message must can be cast to string.");
                    XenforoLoginCore.instance.api.getLogger().warning(result);
                    return ResultType.SERVER_ERROR;
                }
                if (json.get("message") == null) {
                    XenforoLoginCore.instance.api.getLogger().warning("if .result is unknown,.message must be required.");
                    XenforoLoginCore.instance.api.getLogger().warning(result);
                    return ResultType.SERVER_ERROR;
                }
                if (json.get("message").getAsString() == null) {
                    XenforoLoginCore.instance.api.getLogger().warning(".message must can be cast to string.");
                    XenforoLoginCore.instance.api.getLogger().warning(result);
                    return ResultType.SERVER_ERROR;
                }
                return ResultType.UNKNOWN.inheritedObject(ImmutableMap.of(
                        "code", json.get("code").getAsString(),
                        "message", json.get("message").getAsString()));
            default:
                XenforoLoginCore.instance.api.getLogger().warning(".result only can choose between ok,name_incorrect,no_user.");
                XenforoLoginCore.instance.api.getLogger().warning(result);
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
                XenforoLoginCore.instance.api.getLogger().warning(
                        XenforoLoginCore.instance.langFile("errors.url", ImmutableMap.of(
                                "url", url)));
            }
            return null;
        };
        String result;
        try {
            result = Request.Post(url)
                    .bodyForm(Form.form().add("action", "login")
                            .add("username", player.getName())
                            .add("password", password).build())
                    .addHeader("XenforoLogin-Key", key)
                    .execute().handleResponse(responseHandler);
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
            XenforoLoginCore.instance.api.getLogger().warning("Json parse null.");
            XenforoLoginCore.instance.api.getLogger().warning(result);
            return ResultType.SERVER_ERROR;
        }
        if (json == null) {
            XenforoLoginCore.instance.api.getLogger().warning("Json parse null.");
            XenforoLoginCore.instance.api.getLogger().warning(result);
            return ResultType.SERVER_ERROR;
        }
        if (json.get("result") == null) {
            XenforoLoginCore.instance.api.getLogger().warning(".result must be required.");
            XenforoLoginCore.instance.api.getLogger().warning(result);
            return ResultType.SERVER_ERROR;
        }
        if (json.get("result").getAsString() == null) {
            XenforoLoginCore.instance.api.getLogger().warning(".result must can be cast to string.");
            XenforoLoginCore.instance.api.getLogger().warning(result);
            return ResultType.SERVER_ERROR;
        }
        switch (json.get("result").getAsString()) {
            case "ok":
                return ResultType.OK;
            case "name_incorrect":
                if (json.get("correct") == null) {
                    XenforoLoginCore.instance.api.getLogger().warning("if .result is name_incorrect,.correct must be required.");
                    XenforoLoginCore.instance.api.getLogger().warning(result);
                    return ResultType.SERVER_ERROR;
                }
                if (json.get("correct").getAsString() == null) {
                    XenforoLoginCore.instance.api.getLogger().warning(".correct must can be cast to string.");
                    XenforoLoginCore.instance.api.getLogger().warning(result);
                    return ResultType.SERVER_ERROR;
                }
                return ResultType.ERROR_NAME.inheritedObject(ImmutableMap.of(
                        "correct", json.get("correct").getAsString()));
            case "no_user":
                return ResultType.NO_USER;
            case "password_incorrect":
                return ResultType.PASSWORD_INCORRECT;
            case "unknown":
                if (json.get("code") == null) {
                    XenforoLoginCore.instance.api.getLogger().warning("if .result is unknown,.message must be required.");
                    XenforoLoginCore.instance.api.getLogger().warning(result);
                    return ResultType.SERVER_ERROR;
                }
                if (json.get("code").getAsString() == null) {
                    XenforoLoginCore.instance.api.getLogger().warning(".message must can be cast to string.");
                    XenforoLoginCore.instance.api.getLogger().warning(result);
                    return ResultType.SERVER_ERROR;
                }
                if (json.get("message") == null) {
                    XenforoLoginCore.instance.api.getLogger().warning("if .result is unknown,.message must be required.");
                    XenforoLoginCore.instance.api.getLogger().warning(result);
                    return ResultType.SERVER_ERROR;
                }
                if (json.get("message").getAsString() == null) {
                    XenforoLoginCore.instance.api.getLogger().warning(".message must can be cast to string.");
                    XenforoLoginCore.instance.api.getLogger().warning(result);
                    return ResultType.SERVER_ERROR;
                }
                return ResultType.UNKNOWN.inheritedObject(ImmutableMap.of(
                        "code", json.get("code").getAsString(),
                        "message", json.get("message").getAsString()));
            default:
                XenforoLoginCore.instance.api.getLogger().warning(".result only can choose between ok,name_incorrect,no_user.");
                XenforoLoginCore.instance.api.getLogger().warning(result);
                return ResultType.SERVER_ERROR;
        }
    }

    @Nonnull
    @Override
    public ResultType join(AbstractPlayer player) {
        return join(player.getName());
    }

    @Nonnull
    @Override
    public ResultType join(String name) {
        ResponseHandler<String> responseHandler = response -> {
            int status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            } else if (status == 404) {
                XenforoLoginCore.instance.api.getLogger().warning(
                        XenforoLoginCore.instance.langFile("errors.url", ImmutableMap.of(
                                "url", url)));
            }
            return null;
        };
        String result;
        try {
            result = Request.Post(url)
                    .bodyForm(Form.form().add("action", "join")
                            .add("username", name).build())
                    .addHeader("XenforoLogin-Key", key)
                    .execute().handleResponse(responseHandler);
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
            XenforoLoginCore.instance.api.getLogger().warning("Json parse null.");
            XenforoLoginCore.instance.api.getLogger().warning(result);
            return ResultType.SERVER_ERROR;
        }
        if (json == null) {
            XenforoLoginCore.instance.api.getLogger().warning("Json parse null.");
            XenforoLoginCore.instance.api.getLogger().warning(result);
            return ResultType.SERVER_ERROR;
        }
        if (json.get("result") == null) {
            XenforoLoginCore.instance.api.getLogger().warning(".result must be required.");
            XenforoLoginCore.instance.api.getLogger().warning(result);
            return ResultType.SERVER_ERROR;
        }
        if (json.get("result").getAsString() == null) {
            XenforoLoginCore.instance.api.getLogger().warning(".result must can be cast to string.");
            XenforoLoginCore.instance.api.getLogger().warning(result);
            return ResultType.SERVER_ERROR;
        }
        switch (json.get("result").getAsString()) {
            case "ok":
                return ResultType.OK;
            case "name_incorrect":
                if (json.get("correct") == null) {
                    XenforoLoginCore.instance.api.getLogger().warning("if .result is name_incorrect,.correct must be required.");
                    XenforoLoginCore.instance.api.getLogger().warning(result);
                    return ResultType.SERVER_ERROR;
                }
                if (json.get("correct").getAsString() == null) {
                    XenforoLoginCore.instance.api.getLogger().warning(".correct must can be cast to string.");
                    XenforoLoginCore.instance.api.getLogger().warning(result);
                    return ResultType.SERVER_ERROR;
                }
                return ResultType.ERROR_NAME.inheritedObject(ImmutableMap.of(
                        "correct", json.get("correct").getAsString()));
            case "no_user":
                return ResultType.NO_USER;
            default:
                XenforoLoginCore.instance.api.getLogger().warning(".result only can choose between ok,name_incorrect,no_user.");
                XenforoLoginCore.instance.api.getLogger().warning(result);
                return ResultType.SERVER_ERROR;
        }
    }
}

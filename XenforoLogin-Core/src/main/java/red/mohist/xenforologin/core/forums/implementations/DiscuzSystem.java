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
import com.google.gson.JsonArray;
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
import java.util.logging.Level;

@SuppressWarnings("unused")
public class DiscuzSystem implements ForumSystem {

    private final String url;
    private final String key;

    public DiscuzSystem(String url, String key) {
        this.url = url;
        this.key = key;
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public ResultType register(AbstractPlayer player, String password, String email) {
        try {
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

            String result = Request.Post(url + "?action=register")
                    .bodyForm(Form.form().add("username", player.getName())
                            .add("password", password)
                            .add("email", email).build())
                    .addHeader("UC-Api-Key", key)
                    .execute().handleResponse(responseHandler);

            if (result == null) {
                return ResultType.SERVER_ERROR;
            }
            JsonParser parse = new JsonParser();
            JsonObject json;
            try {
                json = parse.parse(result).getAsJsonObject();
            } catch (JsonSyntaxException e) {
                XenforoLoginCore.instance.api.getLogger().warning(result);
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
                        //noinspection SpellCheckingInspection
                        case "usernames_must_be_unique":
                            return ResultType.USER_EXIST;
                        case "please_enter_valid_email":
                            return ResultType.EMAIL_WRONG;
                        case "email_addresses_must_be_unique":
                            return ResultType.EMAIL_EXIST;
                        default:
                            return ResultType.UNKNOWN.inheritedObject(ImmutableMap.of(
                                    "code", errors.get(0).getAsJsonObject().get("code").getAsString(),
                                    "message", errors.get(0).getAsJsonObject().get("message").getAsString()));
                    }
                } else {
                    return ResultType.SERVER_ERROR;
                }
            }
        } catch (Exception e) {
            XenforoLoginCore.instance.api.getLogger().log(Level.WARNING,
                    "Error while register player " + player.getName() + " data", e);
            return ResultType.SERVER_ERROR;
        }
    }


    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public ResultType login(AbstractPlayer player, String password) {
        try {
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

            String result = Request.Post(url + "?action=login")
                    .bodyForm(Form.form().add("login", player.getName())
                            .add("password", password).build())
                    .addHeader("UC-Api-Key", key)
                    .execute().handleResponse(responseHandler);


            if (result == null) {
                return ResultType.SERVER_ERROR;
            }
            JsonParser parse = new JsonParser();
            JsonObject json;
            try {
                json = parse.parse(result).getAsJsonObject();
            } catch (JsonSyntaxException e) {
                XenforoLoginCore.instance.api.getLogger().warning(result);
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
                    return ResultType.ERROR_NAME.inheritedObject(ImmutableMap.of(
                            "correct", json.getAsJsonObject("exact").get("username").getAsString()));
                }
            } else {
                JsonArray errors = json.get("errors").getAsJsonArray();
                if (errors.size() > 0) {
                    if (errors.get(0).getAsJsonObject().get("code").getAsString().equals("incorrect_password")) {
                        return ResultType.PASSWORD_INCORRECT;
                    } else if (errors.get(0).getAsJsonObject().get("code").getAsString().equals("requested_user_x_not_found")) {
                        return ResultType.NO_USER;
                    } else {
                        if (errors.get(0).getAsJsonObject().get("code").getAsString().equals("UC_key_error")) {
                            XenforoLoginCore.instance.api.getLogger().warning(
                                    XenforoLoginCore.instance.langFile("errors.key", ImmutableMap.of(
                                            "key", key)));
                            return ResultType.SERVER_ERROR;
                        }
                        return ResultType.UNKNOWN.inheritedObject(ImmutableMap.of(
                                "code", errors.get(0).getAsJsonObject().get("code").getAsString(),
                                "message", errors.get(0).getAsJsonObject().get("message").getAsString()));
                    }
                } else {
                    return ResultType.SERVER_ERROR;
                }
            }
        } catch (Exception e) {
            XenforoLoginCore.instance.api
                    .getLogger().log(Level.WARNING,
                    "Error while checking player " + player.getName() + " data", e);
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
    @SuppressWarnings("deprecation")
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
            result = Request.Post(url + "?action=join")
                    .bodyForm(Form.form().add("login", name)
                            .build())
                    .addHeader("UC-Api-Key", key)
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
            XenforoLoginCore.instance.api.getLogger().warning(result);
            e.printStackTrace();
            return ResultType.SERVER_ERROR;
        }
        if (json == null) {
            return ResultType.SERVER_ERROR;
        }
        if (json.get("exact") != null && json.get("exact").isJsonNull()) {
            return ResultType.NO_USER;
        }
        if (json.get("errors") != null && json.get("errors").getAsJsonArray().get(0).getAsJsonObject().get("code").getAsString().equals("UC_key_error")) {
            XenforoLoginCore.instance.api.getLogger().warning(
                    XenforoLoginCore.instance.langFile("errors.key", ImmutableMap.of(
                            "key", key)));
            return ResultType.SERVER_ERROR;
        }
        if (!json.getAsJsonObject("exact").get("username").getAsString().equals(name)) {
            return ResultType.ERROR_NAME.inheritedObject(ImmutableMap.of(
                    "correct", json.getAsJsonObject("exact").get("username").getAsString()));
        }
        return ResultType.OK;
    }
}

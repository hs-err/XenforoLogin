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

package red.mohist.xenforologin.core.config;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MainConfiguration {


    public ApiConfiguration api;
    public SessionConfiguration session;
    public YggdrasilConfiguration yggdrasil;
    public SpawnConfiguration spawn;
    public TeleportConfiguration teleport;
    public SecurityConfiguration security;
    public ProtectionConfiguration protection;

    public static MainConfiguration objectFromData(String str) {

        return new Gson().fromJson(str, MainConfiguration.class);
    }

    public static class ApiConfiguration {
        public String system;
        public boolean allowRegister;
        public XenforoConfiguration xenforo;
        public DiscuzConfiguration discuz;
        public SqliteConfiguration sqlite;
        public MysqlConfiguration mysql;

        public static ApiConfiguration objectFromData(String str) {

            return new Gson().fromJson(str, ApiConfiguration.class);
        }

        public static class XenforoConfiguration {
            public String url;
            public String key;

            public static XenforoConfiguration objectFromData(String str) {

                return new Gson().fromJson(str, XenforoConfiguration.class);
            }
        }

        public static class DiscuzConfiguration {
            public String url;
            public String key;

            public static DiscuzConfiguration objectFromData(String str) {

                return new Gson().fromJson(str, DiscuzConfiguration.class);
            }
        }

        public static class SqliteConfiguration {
            public String path;
            public boolean absolute;
            public String tableName;
            public String emailField;
            public String usernameField;
            public String passwordField;
            public String saltField;
            public int saltLength;
            public String passwordHash;

            public static SqliteConfiguration objectFromData(String str) {

                return new Gson().fromJson(str, SqliteConfiguration.class);
            }
        }

        public static class MysqlConfiguration {
            public String host;
            public String username;
            public String password;
            public String database;
            public String tableName;
            public String emailField;
            public String usernameField;
            public String passwordField;
            public String saltField;
            public int saltLength;
            public String passwordHash;

            public static MysqlConfiguration objectFromData(String str) {

                return new Gson().fromJson(str, MysqlConfiguration.class);
            }
        }
    }

    public static class SessionConfiguration {
        public boolean enable;
        public int timeout;

        public static SessionConfiguration objectFromData(String str) {

            return new Gson().fromJson(str, SessionConfiguration.class);
        }
    }

    public static class YggdrasilConfiguration {
        public ServerConfiguration server;
        public TokenConfiguration token;
        public CoreConfiguration core;
        public RatelimitConfiguration ratelimit;
        public SessionConfigurationX session;
        public List<?> skinDomains;

        public static YggdrasilConfiguration objectFromData(String str) {

            return new Gson().fromJson(str, YggdrasilConfiguration.class);
        }

        public static class ServerConfiguration {
            public int port;

            public static ServerConfiguration objectFromData(String str) {

                return new Gson().fromJson(str, ServerConfiguration.class);
            }
        }

        public static class TokenConfiguration {
            public int timeToFullyExpired;
            public boolean enableTimeToPartiallyExpired;
            public String timeToPartiallyExpired;
            public boolean onlyLastSessionAvailable;

            public static TokenConfiguration objectFromData(String str) {

                return new Gson().fromJson(str, TokenConfiguration.class);
            }
        }

        public static class CoreConfiguration {
            public String serverName;
            public String url;
            public List<String> skinDomains;

            public static CoreConfiguration objectFromData(String str) {

                return new Gson().fromJson(str, CoreConfiguration.class);
            }
        }

        public static class RatelimitConfiguration {
            public String limitDuration;

            public static RatelimitConfiguration objectFromData(String str) {

                return new Gson().fromJson(str, RatelimitConfiguration.class);
            }
        }

        public static class SessionConfigurationX {
            public String authExpireTime;

            public static SessionConfigurationX objectFromData(String str) {

                return new Gson().fromJson(str, SessionConfigurationX.class);
            }
        }
    }

    public static class SpawnConfiguration {
        public Object world;
        public Object x;
        public Object y;
        public Object z;

        public static SpawnConfiguration objectFromData(String str) {

            return new Gson().fromJson(str, SpawnConfiguration.class);
        }
    }

    public static class TeleportConfiguration {
        public boolean tpSpawnBeforeLogin;
        public boolean tpBackAfterLogin;

        public static TeleportConfiguration objectFromData(String str) {

            return new Gson().fromJson(str, TeleportConfiguration.class);
        }
    }

    public static class SecurityConfiguration {
        public boolean hideInventory;
        public boolean spectatorLogin;
        public int defaultGamemode;
        public int showTipsTime;
        public int maxLoginTime;
        public boolean cancelChatAfterLogin;

        public static SecurityConfiguration objectFromData(String str) {

            return new Gson().fromJson(str, SecurityConfiguration.class);
        }
    }

    public static class ProtectionConfiguration {
        public AntiProxyConfiguration antiProxy;
        public GeoIPConfiguration GeoIP;
        public RateLimitConfiguration RateLimit;

        public static ProtectionConfiguration objectFromData(String str) {

            return new Gson().fromJson(str, ProtectionConfiguration.class);
        }

        public static class AntiProxyConfiguration {
            public boolean enable;
            public int updateTime;
            public ProxiesProviderConfiguration proxiesProvider;
            public boolean enableLocal;

            public static AntiProxyConfiguration objectFromData(String str) {

                return new Gson().fromJson(str, AntiProxyConfiguration.class);
            }

            public static class ProxiesProviderConfiguration {
                @SerializedName("66ip")
                public boolean _$66ip;
                public boolean QiYun;

                public static ProxiesProviderConfiguration objectFromData(String str) {

                    return new Gson().fromJson(str, ProxiesProviderConfiguration.class);
                }
            }
        }

        public static class GeoIPConfiguration {
            public boolean enable;
            public ListsConfiguration lists;
            @SerializedName("default")
            public boolean defaultX;

            public static GeoIPConfiguration objectFromData(String str) {

                return new Gson().fromJson(str, GeoIPConfiguration.class);
            }

            public static class ListsConfiguration {
                public boolean CN;
                public boolean UNKNOWN;

                public static ListsConfiguration objectFromData(String str) {

                    return new Gson().fromJson(str, ListsConfiguration.class);
                }
            }
        }

        public static class RateLimitConfiguration {
            public boolean enable;
            public int permitsPerSecond;
            public JoinConfiguration join;
            public RegisterConfiguration register;
            public LoginConfiguration login;

            public static RateLimitConfiguration objectFromData(String str) {

                return new Gson().fromJson(str, RateLimitConfiguration.class);
            }

            public static class JoinConfiguration {
                public boolean enable;
                public int permits;

                public static JoinConfiguration objectFromData(String str) {

                    return new Gson().fromJson(str, JoinConfiguration.class);
                }
            }

            public static class RegisterConfiguration {
                public boolean enable;
                public int permits;

                public static RegisterConfiguration objectFromData(String str) {

                    return new Gson().fromJson(str, RegisterConfiguration.class);
                }
            }

            public static class LoginConfiguration {
                public boolean enable;
                public int permits;

                public static LoginConfiguration objectFromData(String str) {

                    return new Gson().fromJson(str, LoginConfiguration.class);
                }
            }
        }
    }
}

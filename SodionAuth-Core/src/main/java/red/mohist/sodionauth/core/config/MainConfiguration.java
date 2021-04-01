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

package red.mohist.sodionauth.core.config;

import red.mohist.sodionauth.core.utils.Config;

import java.util.List;
import java.util.Map;

public class MainConfiguration {

    private String defaultLang;
    private ApiBean api;
    private DependenciesBean dependencies;
    private SessionBean session;
    private YggdrasilBean yggdrasil;
    private SpawnBean spawn;
    private TeleportBean teleport;
    private SecurityBean security;
    private ProtectionBean protection;

    public String getDefaultLang(String def) {
        return getDefaultLang() == null ? def : getDefaultLang();
    }

    public String getDefaultLang() {
        return defaultLang != null ? defaultLang : Config.fallback.defaultLang;
    }

    public ApiBean getApi() {
        return api != null ? api : Config.fallback.api;
    }

    public SessionBean getSession() {
        return session != null ? session : Config.fallback.session;
    }

    public YggdrasilBean getYggdrasil() {
        return yggdrasil != null ? yggdrasil : Config.fallback.yggdrasil;
    }

    public SpawnBean getSpawn() {
        return spawn != null ? spawn : Config.fallback.spawn;
    }

    public TeleportBean getTeleport() {
        return teleport != null ? teleport : Config.fallback.teleport;
    }

    public SecurityBean getSecurity() {
        return security != null ? security : Config.fallback.security;
    }

    public ProtectionBean getProtection() {
        return protection != null ? protection : Config.fallback.protection;
    }

    public DependenciesBean getDependencies() {
        return dependencies != null ? dependencies : Config.fallback.dependencies;
    }

    public static class ApiBean {
        private String system;
        private Boolean allowRegister;
        private XenforoBean xenforo;
        private WebBean web;
        private SqliteBean sqlite;
        private MysqlBean mysql;

        public String getSystem(String def) {
            return getSystem() == null ? def : getSystem();
        }

        public String getSystem() {
            return system != null ? system : Config.fallback.api.system;
        }

        public Boolean getAllowRegister(Boolean def) {
            return getAllowRegister() == null ? def : getAllowRegister();
        }

        public Boolean getAllowRegister() {
            return allowRegister != null ? allowRegister : Config.fallback.api.allowRegister;
        }

        public XenforoBean getXenforo(XenforoBean def) {
            return getXenforo() == null ? def : getXenforo();
        }

        public XenforoBean getXenforo() {
            return xenforo != null ? xenforo : Config.fallback.api.xenforo;
        }

        public WebBean getWeb(WebBean def) {
            return getWeb() == null ? def : getWeb();
        }

        public WebBean getWeb() {
            return web != null ? web : Config.fallback.api.web;
        }

        public SqliteBean getSqlite(SqliteBean def) {
            return getSqlite() == null ? def : getSqlite();
        }

        public SqliteBean getSqlite() {
            return sqlite != null ? sqlite : Config.fallback.api.sqlite;
        }

        public MysqlBean getMysql(MysqlBean def) {
            return getMysql() == null ? def : getMysql();
        }

        public MysqlBean getMysql() {
            return mysql != null ? mysql : Config.fallback.api.mysql;
        }

        public static class XenforoBean {
            private String url;
            private String key;

            public String getUrl(String def) {
                return getUrl() == null ? def : getUrl();
            }

            public String getUrl() {
                return url != null ? url : Config.fallback.api.xenforo.url;
            }

            public String getKey(String def) {
                return getKey() == null ? def : getKey();
            }

            public String getKey() {
                return key != null ? key : Config.fallback.api.xenforo.key;
            }
        }

        public static class WebBean {
            private String url;
            private String key;

            public String getUrl(String def) {
                return getUrl() == null ? def : getUrl();
            }

            public String getUrl() {
                return url != null ? url : Config.fallback.api.web.url;
            }

            public String getKey(String def) {
                return getKey() == null ? def : getKey();
            }

            public String getKey() {
                return key != null ? key : Config.fallback.api.web.key;
            }
        }

        public static class SqliteBean {
            private String path;
            private Boolean absolute;
            private String tableName;
            private String emailField;
            private String usernameField;
            private String passwordField;
            private String saltField;
            private Integer saltLength;
            private String passwordHash;

            public String getPath(String def) {
                return getPath() == null ? def : getPath();
            }

            public String getPath() {
                return path != null ? path : Config.fallback.api.sqlite.path;
            }

            public Boolean getAbsolute(Boolean def) {
                return getAbsolute() == null ? def : getAbsolute();
            }

            public Boolean getAbsolute() {
                return absolute != null ? absolute : Config.fallback.api.sqlite.absolute;
            }

            public String getTableName(String def) {
                return getTableName() == null ? def : getTableName();
            }

            public String getTableName() {
                return tableName != null ? tableName : Config.fallback.api.sqlite.tableName;
            }

            public String getEmailField(String def) {
                return getEmailField() == null ? def : getEmailField();
            }

            public String getEmailField() {
                return emailField != null ? emailField : Config.fallback.api.sqlite.emailField;
            }

            public String getUsernameField(String def) {
                return getUsernameField() == null ? def : getUsernameField();
            }

            public String getUsernameField() {
                return usernameField != null ? usernameField : Config.fallback.api.sqlite.usernameField;
            }

            public String getPasswordField(String def) {
                return getPasswordField() == null ? def : getPasswordField();
            }

            public String getPasswordField() {
                return passwordField != null ? passwordField : Config.fallback.api.sqlite.passwordField;
            }

            public String getSaltField(String def) {
                return getSaltField() == null ? def : getSaltField();
            }

            public String getSaltField() {
                return saltField != null ? saltField : Config.fallback.api.sqlite.saltField;
            }

            public Integer getSaltLength(Integer def) {
                return getSaltLength() == null ? def : getSaltLength();
            }

            public Integer getSaltLength() {
                return saltLength != null ? saltLength : Config.fallback.api.sqlite.saltLength;
            }

            public String getPasswordHash(String def) {
                return getPasswordHash() == null ? def : getPasswordHash();
            }

            public String getPasswordHash() {
                return passwordHash != null ? passwordHash : Config.fallback.api.sqlite.passwordHash;
            }
        }

        public static class MysqlBean {
            private String host;
            private String username;
            private String password;
            private String database;
            private String tableName;
            private String emailField;
            private String usernameField;
            private String passwordField;
            private String saltField;
            private Integer saltLength;
            private String passwordHash;

            public String getHost(String def) {
                return getHost() == null ? def : getHost();
            }

            public String getHost() {
                return host != null ? host : Config.fallback.api.mysql.host;
            }

            public String getUsername(String def) {
                return getUsername() == null ? def : getUsername();
            }

            public String getUsername() {
                return username != null ? username : Config.fallback.api.mysql.username;
            }

            public String getPassword(String def) {
                return getPassword() == null ? def : getPassword();
            }

            public String getPassword() {
                return password != null ? password : Config.fallback.api.mysql.password;
            }

            public String getDatabase(String def) {
                return getDatabase() == null ? def : getDatabase();
            }

            public String getDatabase() {
                return database != null ? database : Config.fallback.api.mysql.database;
            }

            public String getTableName(String def) {
                return getTableName() == null ? def : getTableName();
            }

            public String getTableName() {
                return tableName != null ? tableName : Config.fallback.api.mysql.tableName;
            }

            public String getEmailField(String def) {
                return getEmailField() == null ? def : getEmailField();
            }

            public String getEmailField() {
                return emailField != null ? emailField : Config.fallback.api.mysql.emailField;
            }

            public String getUsernameField(String def) {
                return getUsernameField() == null ? def : getUsernameField();
            }

            public String getUsernameField() {
                return usernameField != null ? usernameField : Config.fallback.api.mysql.usernameField;
            }

            public String getPasswordField(String def) {
                return getPasswordField() == null ? def : getPasswordField();
            }

            public String getPasswordField() {
                return passwordField != null ? passwordField : Config.fallback.api.mysql.passwordField;
            }

            public String getSaltField(String def) {
                return getSaltField() == null ? def : getSaltField();
            }

            public String getSaltField() {
                return saltField != null ? saltField : Config.fallback.api.mysql.saltField;
            }

            public Integer getSaltLength(Integer def) {
                return getSaltLength() == null ? def : getSaltLength();
            }

            public Integer getSaltLength() {
                return saltLength != null ? saltLength : Config.fallback.api.mysql.saltLength;
            }

            public String getPasswordHash(String def) {
                return getPasswordHash() == null ? def : getPasswordHash();
            }

            public String getPasswordHash() {
                return passwordHash != null ? passwordHash : Config.fallback.api.mysql.passwordHash;
            }
        }
    }

    public static class DependenciesBean {
        private String mavenRepository;

        public String getMavenRepository() {
            return mavenRepository != null ? mavenRepository : Config.fallback.dependencies.mavenRepository;
        }
    }

    public static class SessionBean {
        private Boolean enable;
        private Integer timeout;

        public Boolean getEnable(Boolean def) {
            return getEnable() == null ? def : getEnable();
        }

        public Boolean getEnable() {
            return enable != null ? enable : Config.fallback.session.enable;
        }

        public Integer getTimeout(Integer def) {
            return getTimeout() == null ? def : getTimeout();
        }

        public Integer getTimeout() {
            return timeout != null ? timeout : Config.fallback.session.timeout;
        }
    }

    public static class YggdrasilBean {
        private Boolean enable;
        private ServerBean server;
        private TokenBean token;
        private CoreBean core;
        private RatelimitBean ratelimit;

        public Boolean getEnable(Boolean def) {
            return getEnable() == null ? def : getEnable();
        }

        public Boolean getEnable() {
            return enable != null ? enable : Config.fallback.yggdrasil.enable;
        }

        public ServerBean getServer(ServerBean def) {
            return getServer() == null ? def : getServer();
        }

        public ServerBean getServer() {
            return server != null ? server : Config.fallback.yggdrasil.server;
        }

        public TokenBean getToken(TokenBean def) {
            return getToken() == null ? def : getToken();
        }

        public TokenBean getToken() {
            return token != null ? token : Config.fallback.yggdrasil.token;
        }

        public CoreBean getCore(CoreBean def) {
            return getCore() == null ? def : getCore();
        }

        public CoreBean getCore() {
            return core != null ? core : Config.fallback.yggdrasil.core;
        }

        public RatelimitBean getRatelimit(RatelimitBean def) {
            return getRatelimit() == null ? def : getRatelimit();
        }

        public RatelimitBean getRatelimit() {
            return ratelimit != null ? ratelimit : Config.fallback.yggdrasil.ratelimit;
        }

        public static class ServerBean {
            private Integer port;

            public Integer getPort(Integer def) {
                return getPort() == null ? def : getPort();
            }

            public Integer getPort() {
                return port != null ? port : Config.fallback.yggdrasil.server.port;
            }
        }

        public static class TokenBean {
            private Integer timeToFullyExpired;
            private Boolean enableTimeToPartiallyExpired;
            private Integer timeToPartiallyExpired;
            private Boolean onlyLastSessionAvailable;

            public Integer getTimeToFullyExpired(Integer def) {
                return getTimeToFullyExpired() == null ? def : getTimeToFullyExpired();
            }

            public Integer getTimeToFullyExpired() {
                return timeToFullyExpired != null ? timeToFullyExpired : Config.fallback.yggdrasil.token.timeToFullyExpired;
            }

            public Boolean getEnableTimeToPartiallyExpired(Boolean def) {
                return getEnableTimeToPartiallyExpired() == null ? def : getEnableTimeToPartiallyExpired();
            }

            public Boolean getEnableTimeToPartiallyExpired() {
                return enableTimeToPartiallyExpired != null ? enableTimeToPartiallyExpired : Config.fallback.yggdrasil.token.enableTimeToPartiallyExpired;
            }

            public Integer getTimeToPartiallyExpired(Integer def) {
                return getTimeToPartiallyExpired() == null ? def : getTimeToPartiallyExpired();
            }

            public Integer getTimeToPartiallyExpired() {
                return timeToPartiallyExpired != null ? timeToPartiallyExpired : Config.fallback.yggdrasil.token.timeToPartiallyExpired;
            }

            public Boolean getOnlyLastSessionAvailable(Boolean def) {
                return getOnlyLastSessionAvailable() == null ? def : getOnlyLastSessionAvailable();
            }

            public Boolean getOnlyLastSessionAvailable() {
                return onlyLastSessionAvailable != null ? onlyLastSessionAvailable : Config.fallback.yggdrasil.token.onlyLastSessionAvailable;
            }
        }

        public static class CoreBean {
            private String serverName;
            private String url;
            private List<String> skinDomains;

            public String getServerName(String def) {
                return getServerName() == null ? def : getServerName();
            }

            public String getServerName() {
                return serverName != null ? serverName : Config.fallback.yggdrasil.core.serverName;
            }

            public String getUrl(String def) {
                return getUrl() == null ? def : getUrl();
            }

            public String getUrl() {
                return url != null ? url : Config.fallback.yggdrasil.core.url;
            }

            public List<String> getSkinDomains(List<String> def) {
                return getSkinDomains() == null ? def : getSkinDomains();
            }

            public List<String> getSkinDomains() {
                return skinDomains != null ? skinDomains : Config.fallback.yggdrasil.core.skinDomains;
            }
        }

        public static class RatelimitBean {
            private String limitDuration;

            public String getLimitDuration(String def) {
                return getLimitDuration() == null ? def : getLimitDuration();
            }

            public String getLimitDuration() {
                return limitDuration != null ? limitDuration : Config.fallback.yggdrasil.ratelimit.limitDuration;
            }
        }
    }

    public static class SpawnBean {
        private String world;
        private Double x;
        private Double y;
        private Double z;
        private Float yaw;
        private Float pitch;

        public String getWorld(String def) {
            return getWorld() == null ? def : getWorld();
        }

        public String getWorld() {
            return world != null ? world : Config.fallback.spawn.world;
        }

        public Double getX(Double def) {
            return getX() == null ? def : getX();
        }

        public Double getX() {
            return x != null ? x : Config.fallback.spawn.x;
        }

        public Double getY(Double def) {
            return getY() == null ? def : getY();
        }

        public Double getY() {
            return y != null ? y : Config.fallback.spawn.y;
        }

        public Double getZ(Double def) {
            return getZ() == null ? def : getZ();
        }

        public Double getZ() {
            return z != null ? z : Config.fallback.spawn.z;
        }

        public Float getYaw(Float def) {
            return getYaw() == null ? def : getYaw();
        }

        public Float getYaw() {
            return yaw != null ? yaw : Config.fallback.spawn.yaw;
        }

        public Float getPitch(Float def) {
            return getPitch() == null ? def : getPitch();
        }

        public Float getPitch() {
            return pitch != null ? pitch : Config.fallback.spawn.pitch;
        }
    }

    public static class TeleportBean {
        private Boolean tpSpawnBeforeLogin;
        private Boolean tpBackAfterLogin;

        public Boolean getTpSpawnBeforeLogin(Boolean def) {
            return getTpSpawnBeforeLogin() == null ? def : getTpSpawnBeforeLogin();
        }

        public Boolean getTpSpawnBeforeLogin() {
            return tpSpawnBeforeLogin != null ? tpSpawnBeforeLogin : Config.fallback.teleport.tpSpawnBeforeLogin;
        }

        public Boolean getTpBackAfterLogin(Boolean def) {
            return getTpBackAfterLogin() == null ? def : getTpBackAfterLogin();
        }

        public Boolean getTpBackAfterLogin() {
            return tpBackAfterLogin != null ? tpBackAfterLogin : Config.fallback.teleport.tpBackAfterLogin;
        }
    }

    public static class SecurityBean {
        private Boolean hideInventory;
        private Boolean spectatorLogin;
        private Integer defaultGamemode;
        private Integer showTipsTime;
        private Integer maxLoginTime;
        private Boolean cancelChatAfterLogin;

        public Boolean getHideInventory(Boolean def) {
            return getHideInventory() == null ? def : getHideInventory();
        }

        public Boolean getHideInventory() {
            return hideInventory != null ? hideInventory : Config.fallback.security.hideInventory;
        }

        public Boolean getSpectatorLogin(Boolean def) {
            return getSpectatorLogin() == null ? def : getSpectatorLogin();
        }

        public Boolean getSpectatorLogin() {
            return spectatorLogin != null ? spectatorLogin : Config.fallback.security.spectatorLogin;
        }

        public Integer getDefaultGamemode(Integer def) {
            return getDefaultGamemode() == null ? def : getDefaultGamemode();
        }

        public Integer getDefaultGamemode() {
            return defaultGamemode != null ? defaultGamemode : Config.fallback.security.defaultGamemode;
        }

        public Integer getShowTipsTime(Integer def) {
            return getShowTipsTime() == null ? def : getShowTipsTime();
        }

        public Integer getShowTipsTime() {
            return showTipsTime != null ? showTipsTime : Config.fallback.security.showTipsTime;
        }

        public Integer getMaxLoginTime(Integer def) {
            return getMaxLoginTime() == null ? def : getMaxLoginTime();
        }

        public Integer getMaxLoginTime() {
            return maxLoginTime != null ? maxLoginTime : Config.fallback.security.maxLoginTime;
        }

        public Boolean getCancelChatAfterLogin(Boolean def) {
            return getCancelChatAfterLogin() == null ? def : getCancelChatAfterLogin();
        }

        public Boolean getCancelChatAfterLogin() {
            return cancelChatAfterLogin != null ? cancelChatAfterLogin : Config.fallback.security.cancelChatAfterLogin;
        }
    }

    public static class ProtectionBean {
        private ProxySystemsBean ProxySystems;
        private GeoIPBean GeoIP;
        private RateLimitBean RateLimit;

        public ProxySystemsBean getProxySystems(ProxySystemsBean def) {
            return getProxySystems() == null ? def : getProxySystems();
        }

        public ProxySystemsBean getProxySystems() {
            return ProxySystems != null ? ProxySystems : Config.fallback.protection.ProxySystems;
        }

        public GeoIPBean getGeoIP(GeoIPBean def) {
            return getGeoIP() == null ? def : getGeoIP();
        }

        public GeoIPBean getGeoIP() {
            return GeoIP != null ? GeoIP : Config.fallback.protection.GeoIP;
        }

        public RateLimitBean getRateLimit(RateLimitBean def) {
            return getRateLimit() == null ? def : getRateLimit();
        }

        public RateLimitBean getRateLimit() {
            return RateLimit != null ? RateLimit : Config.fallback.protection.RateLimit;
        }

        public static class ProxySystemsBean {
            private Boolean enable;
            private Integer updateTime;
            private Map<String, Boolean> proxiesProvider;
            private Boolean enableLocal;

            public Boolean getEnable(Boolean def) {
                return getEnable() == null ? def : getEnable();
            }

            public Boolean getEnable() {
                return enable != null ? enable : Config.fallback.protection.ProxySystems.enable;
            }

            public Integer getUpdateTime(Integer def) {
                return getUpdateTime() == null ? def : getUpdateTime();
            }

            public Integer getUpdateTime() {
                return updateTime != null ? updateTime : Config.fallback.protection.ProxySystems.updateTime;
            }

            public Map<String, Boolean> getProxiesProvider(Map<String, Boolean> def) {
                return getProxiesProvider() == null ? def : getProxiesProvider();
            }

            public Map<String, Boolean> getProxiesProvider() {
                return proxiesProvider != null ? proxiesProvider : Config.fallback.protection.ProxySystems.proxiesProvider;
            }

            public Boolean getEnableLocal(Boolean def) {
                return getEnableLocal() == null ? def : getEnableLocal();
            }

            public Boolean getEnableLocal() {
                return enableLocal != null ? enableLocal : Config.fallback.protection.ProxySystems.enableLocal;
            }
        }

        public static class GeoIPBean {
            private Boolean enable;
            private Map<String, Boolean> lists;
            private Boolean other;

            public Boolean getEnable(Boolean def) {
                return getEnable() == null ? def : getEnable();
            }

            public Boolean getEnable() {
                return enable != null ? enable : Config.fallback.protection.GeoIP.enable;
            }

            public Map<String, Boolean> getLists(Map<String, Boolean> def) {
                return getLists() == null ? def : getLists();
            }

            public Map<String, Boolean> getLists() {
                return lists != null ? lists : Config.fallback.protection.GeoIP.lists;
            }

            public Boolean getOther(Boolean def) {
                return getOther() == null ? def : getOther();
            }

            public Boolean getOther() {
                return other != null ? other : Config.fallback.protection.GeoIP.other;
            }
        }

        public static class RateLimitBean {
            private Boolean enable;
            private Integer permitsPerSecond;
            private JoinBean join;
            private RegisterBean register;
            private LoginBean login;

            public Boolean getEnable(Boolean def) {
                return getEnable() == null ? def : getEnable();
            }

            public Boolean getEnable() {
                return enable != null ? enable : Config.fallback.protection.RateLimit.enable;
            }

            public Integer getPermitsPerSecond(Integer def) {
                return getPermitsPerSecond() == null ? def : getPermitsPerSecond();
            }

            public Integer getPermitsPerSecond() {
                return permitsPerSecond != null ? permitsPerSecond : Config.fallback.protection.RateLimit.permitsPerSecond;
            }

            public JoinBean getJoin(JoinBean def) {
                return getJoin() == null ? def : getJoin();
            }

            public JoinBean getJoin() {
                return join != null ? join : Config.fallback.protection.RateLimit.join;
            }

            public RegisterBean getRegister(RegisterBean def) {
                return getRegister() == null ? def : getRegister();
            }

            public RegisterBean getRegister() {
                return register != null ? register : Config.fallback.protection.RateLimit.register;
            }

            public LoginBean getLogin(LoginBean def) {
                return getLogin() == null ? def : getLogin();
            }

            public LoginBean getLogin() {
                return login != null ? login : Config.fallback.protection.RateLimit.login;
            }

            public static class JoinBean {
                private Boolean enable;
                private Integer permits;

                public Boolean getEnable(Boolean def) {
                    return getEnable() == null ? def : getEnable();
                }

                public Boolean getEnable() {
                    return enable != null ? enable : Config.fallback.protection.RateLimit.join.enable;
                }

                public Integer getPermits(Integer def) {
                    return getPermits() == null ? def : getPermits();
                }

                public Integer getPermits() {
                    return permits != null ? permits : Config.fallback.protection.RateLimit.join.permits;
                }
            }

            public static class RegisterBean {
                private Boolean enable;
                private Integer permits;

                public Boolean getEnable(Boolean def) {
                    return getEnable() == null ? def : getEnable();
                }

                public Boolean getEnable() {
                    return enable != null ? enable : Config.fallback.protection.RateLimit.register.enable;
                }

                public Integer getPermits(Integer def) {
                    return getPermits() == null ? def : getPermits();
                }

                public Integer getPermits() {
                    return permits != null ? permits : Config.fallback.protection.RateLimit.register.permits;
                }
            }

            public static class LoginBean {
                private Boolean enable;
                private Integer permits;

                public Boolean getEnable(Boolean def) {
                    return getEnable() == null ? def : getEnable();
                }

                public Boolean getEnable() {
                    return enable != null ? enable : Config.fallback.protection.RateLimit.login.enable;
                }

                public Integer getPermits(Integer def) {
                    return getPermits() == null ? def : getPermits();
                }

                public Integer getPermits() {
                    return permits != null ? permits : Config.fallback.protection.RateLimit.login.permits;
                }
            }
        }
    }
}

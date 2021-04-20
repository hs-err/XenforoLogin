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

import com.google.common.collect.ImmutableMap;
import com.google.gson.annotations.Expose;
import red.mohist.sodionauth.core.utils.Helper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainConfiguration extends Configure {

    @Migrate("version")
    @Expose(deserialize = false)
    public Integer version = 2;

    @Migrate("defaultLang")
    @Lore("The default language should message use.")
    @Expose
    public String defaultLang = "en";

    @Migrate("serverId")
    @Lore("The server's ID")
    @Expose
    public String serverId = Helper.toStringUuid(UUID.randomUUID());

    @Expose
    public DatabaseBean database = new DatabaseBean();

    @Expose
    public ApiBean api = new ApiBean();

    @Expose
    public PasswordBean password = new PasswordBean();

    @Expose
    public BungeeBean bungee = new BungeeBean();

    @Expose
    public DependenciesBean dependencies = new DependenciesBean();

    @Expose
    public SessionBean session = new SessionBean();

    @Lore("Where should player login?")
    @Lore("Player will be teleported to world spawn after login.")
    @Lore("If set null, It will use world spawn.")
    @Expose
    public SpawnBean spawn = new SpawnBean();

    @Expose
    public TeleportBean teleport = new TeleportBean();

    @Expose
    public SecurityBean security = new SecurityBean();

    @Expose
    public ProtectionBean protection = new ProtectionBean();

    public static class DatabaseBean extends Configure {
        @Migrate("database.type")
        @Lore("The database type should SodionAuth use.")
        @Expose
        public String type = "sqlite";

        @Migrate("database.tablePrefix")
        @Lore("The table prefix should SodionAuth use.")
        @Expose
        public String tablePrefix = "sa_";

        @Migrate("database.passwordHash")
        @Lore("The password hash should SodionAuth use as default.")
        @Lore("If a player with other kind of hash, It will transfer into this when he login in.")
        @Lore("If It sets to null, sodionAuth will use Api only.")
        @Expose
        public String passwordHash = "BCrypt";

        //@Migrate("database.saltLength")
        @Lore("The password salt length")
        @Expose
        public int saltLength = 6;

        @Expose
        public SqliteBean sqlite = new SqliteBean();

        @Expose
        public MysqlBean mysql = new MysqlBean();

        public static class SqliteBean extends Configure {
            @Migrate("database.sqlite.path")
            @Lore("The path to sqlite database")
            @Expose
            public String path = "Users.db";

            @Migrate("database.sqlite.absolute")
            @Lore("Is absolute path?")
            @Expose
            public Boolean absolute = false;
        }

        public static class MysqlBean extends Configure {
            @Migrate("database.mysql.host")
            @Lore("The hostname to mysql.")
            @Lore("It likes 127.0.0.1:3306")
            @Expose
            public String host = "localhost:3306";

            @Migrate("database.mysql.username")
            @Lore("The username for mysql.")
            @Expose
            public String username = "root";

            @Migrate("database.mysql.password")
            @Lore("The password for mysql.")
            @Expose
            public String password = "";

            @Migrate("database.mysql.database")
            @Lore("The database should SodionAuth use.")
            @Expose
            public String database = "sodionauth";
        }
    }

    public static class ApiBean extends Configure {
        @Migrate("api.xenforo")
        @Expose
        public Map<String,XenforoBean> xenforo = ImmutableMap.of("xenforo",new XenforoBean());

        @Migrate("api.web")
        @Expose
        public Map<String,WebBean> web = ImmutableMap.of("web",new WebBean());

        public static abstract class ApiConfigBean extends Configure{
            @Lore("Could player use this account to login?")
            @Expose
            public Boolean allowLogin = false;

            @Lore("Will auto register when player register?")
            @Expose
            public Boolean allowRegister = false;

            @Lore("The friendly name should display to user.")
            @Expose
            public String friendlyName = "web api";
        }

        public static class XenforoBean extends ApiConfigBean {
            @Lore("The Xenforo API url. Likes http://example.com/api .")
            @Expose
            public String url = "http://example.com/api";

            @Lore("A super-admin key with these permissions: auth & user:read.")
            @Lore("It could create at http://example.com/admin.php?api-keys .")
            @Expose
            public String key = "YOUR_KEY_HERE";
        }

        public static class WebBean extends ApiConfigBean {
            @Lore("The SodionApi url.")
            @Expose
            public String url = "http://example.com/SodionAuth.php";

            @Lore("SodionAuth Api Key .")
            @Expose
            public String key = "YOUR_KEY_HERE";
        }
    }

    public static class PasswordBean extends Configure {
        // @Migrate("password.minimumEntropy")
        @Expose
        public Integer minimumEntropy = 32;
    }

    public static class DependenciesBean extends Configure {
        @Migrate("dependencies.mavenRepository")
        @Lore("Maven Repo Url")
        @Lore("If you are in China, you could try this")
        @Lore("https://maven.aliyun.com/repository/central")
        @Expose
        public String mavenRepository = "https://repo1.maven.org/maven2/";
    }

    public static class BungeeBean extends Configure {
        @Migrate("bungee.clientKey")
        @Lore("The key use when SodionAuth run as Bungee")
        @Lore("It should as same as Server's serverKey")
        @Expose
        public String clientKey = Helper.toStringUuid(UUID.randomUUID());

        @Migrate("bungee.serverKey")
        @Lore("The key use when SodionAuth run as Server")
        @Lore("It should as same as Bungee's clientKey")
        @Lore("If you have a Bungee outside the Bungee")
        @Lore("You should make this as same as outside Bungee's clientKet")
        @Expose
        public String serverKey = Helper.toStringUuid(UUID.randomUUID());
    }

    public static class SessionBean extends Configure {
        @Migrate("session.enable")
        @Lore("Auto login when player rejoin?")
        @Lore("If enable, make sure server can get player's true ip.")
        @Expose
        public Boolean enable = false;

        @Migrate("session.timeout")
        @Lore("Player can autoLogin in (seconds).")
        @Expose
        public Integer timeout = 3600;
    }

    public static class SpawnBean extends Configure {
        @Migrate("spawn.world")
        @Expose
        public String world = null;

        @Migrate("spawn.x")
        @Expose
        public Double x = null;

        @Migrate("spawn.y")
        @Expose
        public Double y = null;

        @Migrate("spawn.z")
        @Expose
        public Double z = null;

        @Migrate("spawn.yaw")
        @Expose
        public Float yaw = null;

        @Migrate("spawn.pitch")
        @Expose
        public Float pitch = null;
    }

    public static class TeleportBean extends Configure {
        @Migrate("teleport.tpSpawnBeforeLogin")
        @Lore("Should player login in spawn during login?")
        @Expose
        public Boolean tpSpawnBeforeLogin = true;

        @Migrate("teleport.tpBackAfterLogin")
        @Lore("Should player tp to location where they quit?")
        @Expose
        public Boolean tpBackAfterLogin = true;
    }

    public static class SecurityBean extends Configure {
        @Migrate("security.hideInventory")
        @Lore("Should hidden player's Inventory before login in?")
        @Lore("It only works in bukkit. And ProtocolLib is need.")
        @Expose
        public Boolean hideInventory = true;

        @Migrate("security.spectatorLogin")
        @Lore("Should keep player in spectator mode before login in?")
        @Expose
        public Boolean spectatorLogin = true;

        @Migrate("security.defaultGamemode")
        @Lore("The default gamemode for player.")
        @Lore("0 Survival, 1 Creative, 2 Adventure, 3 Spectator")
        @Expose
        public Integer defaultGamemode = 0;

        @Migrate("security.showTipsTime")
        @Lore("The time show tip.")
        @Expose
        public Integer showTipsTime = 5;

        @Migrate("security.maxLoginTime")
        @Lore("The time for login. If player use too much time, they will be kicked.")
        @Expose
        public Integer maxLoginTime = 30;

        @Migrate("security.cancelChatAfterLogin")
        @Lore("Could forbid player send message?")
        @Lore("It should enable when SodionAuth in auth server.")
        @Expose
        public Boolean cancelChatAfterLogin = false;
    }

    public static class ProtectionBean extends Configure {
        @Expose
        @Name("proxySystems")
        public ProxySystemsBean ProxySystems = new ProxySystemsBean();

        @Expose
        @Name("geoIp")
        public GeoIpBean GeoIp = new GeoIpBean();

        @Expose
        @Name("rateLimit")
        public RateLimitBean RateLimit = new RateLimitBean();

        public static class ProxySystemsBean extends Configure {
            @Migrate("protection.ProxySystems.enable")
            @Lore("Enable anti-proxy system")
            @Expose
            public Boolean enable = true;

            @Migrate("protection.ProxySystems.updateTime")
            @Lore("The time update proxy list")
            @Expose
            public Integer updateTime = 60;

            @Migrate("protection.ProxySystems.proxiesProvider")
            @Lore("Enable which proxy list provider")
            @Expose
            public Map<String, Boolean> proxiesProvider = ImmutableMap.of(
                    "LiuLiu", true,
                    "QiYun", true
            );

            @Migrate("protection.ProxySystems.enableLocal")
            @Lore("Enable local address join?")
            @Expose
            public Boolean enableLocal = true;
        }

        public static class GeoIpBean extends Configure {
            @Migrate("protection.GeoIp.enable")
            @Lore("Enable anti-proxy system")
            @Expose
            public Boolean enable = true;

            @Migrate("protection.GeoIp.countries")
            @Lore("Could List country player join?")
            @Lore("Use ISO country id")
            @Lore("UNKNOWN means GeoIp can't know their countries.")
            @Expose
            public Map<String, Boolean> countries = ImmutableMap.of(
                    "CN", true,
                    "UNKNOWN", true
            );

            @Migrate("protection.GeoIp.other")
            @Lore("Could player who not list in countries join?")
            @Expose
            public Boolean other = true;
        }

        public static class RateLimitBean extends Configure {
            @Migrate("protection.RateLimit.enable")
            @Lore("Enable rateLimit system")
            @Expose
            public Boolean enable = true;

            @Migrate("protection.RateLimit.permitsPerSecond")
            @Lore("Permit produce per second.")
            @Expose
            public Integer permitsPerSecond = 10;

            @Migrate("protection.RateLimit.join")
            @Lore("Player join will take permit.")
            @Expose
            public Integer join = 1;

            @Migrate("protection.RateLimit.register")
            @Lore("Player register will take permit.")
            @Expose
            public Integer register = 10;

            @Migrate("protection.RateLimit.login")
            @Lore("Player login will take permit.")
            @Expose
            public Integer login = 5;

        }
    }
}

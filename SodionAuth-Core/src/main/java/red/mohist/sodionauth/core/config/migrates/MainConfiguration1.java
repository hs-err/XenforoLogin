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

package red.mohist.sodionauth.core.config.migrates;

import com.google.common.collect.ImmutableMap;
import com.google.gson.annotations.Expose;
import red.mohist.sodionauth.core.config.Configure;
import red.mohist.sodionauth.core.config.Lore;
import red.mohist.sodionauth.core.utils.Helper;

import java.util.Map;
import java.util.UUID;

public class MainConfiguration1 extends Configure {

    @Expose(serialize = true, deserialize = false)
    public Integer version = 1;

    @Lore("The default language should message use.")
    @Expose
    public String defaultLang = "en";

    @Lore("The server's ID")
    @Expose
    public String serverId = Helper.toStringUuid(UUID.randomUUID());

    @Expose
    public DatabaseBean database = new DatabaseBean();

    @Expose
    public ApiBean api = new ApiBean();

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
        @Lore("The database type should SodionAuth use.")
        @Expose
        public String type = "sqlite";

        @Lore("The table prefix should SodionAuth use.")
        @Expose
        public String tablePrefix = "sa_";
        @Lore("The password hash should SodionAuth use as default.")
        @Lore("If a player with other kind of hash, It will transfer into this when he login in.")
        @Lore("If It sets to null, sodionAuth will use Api only.")
        @Expose
        public String passwordHash = "BCrypt";

        @Expose
        public SqliteBean sqlite = new SqliteBean();

        @Expose
        public MysqlBean mysql = new MysqlBean();

        public static class SqliteBean extends Configure {
            @Lore("The path to sqlite database")
            @Expose
            public String path = "Users.db";

            @Lore("Is absolute path?")
            @Expose
            public Boolean absolute = false;
        }

        public static class MysqlBean extends Configure {
            @Lore("The hostname to mysql.")
            @Lore("It likes 127.0.0.1:3306")
            @Expose
            public String host = "localhost:3306";

            @Lore("The username for mysql.")
            @Expose
            public String username = "root";

            @Lore("The password for mysql.")
            @Expose
            public String password = "";

            @Lore("The database should SodionAuth use.")
            @Expose
            public String database = "sodionauth";
        }
    }

    public static class ApiBean extends Configure {
        @Expose
        public XenforoBean[] xenforo = {new XenforoBean()};

        @Expose
        public WebBean[] web = {new WebBean()};

        public static class XenforoBean extends Configure {
            @Lore("Could player use this account to login?")
            @Expose
            public Boolean allowLogin = false;

            @Lore("Will auto register when player register?")
            @Expose
            public Boolean allowRegister = false;

            @Lore("The Xenforo API url. Likes http://example.com/api .")
            @Expose
            public String url = "http://example.com/api";

            @Lore("A super-admin key with these permissions: auth & user:read.")
            @Lore("It could create at http://example.com/admin.php?api-keys .")
            @Expose
            public String key = "YOUR_KEY_HERE";
        }

        public static class WebBean extends Configure {
            @Lore("Could player use this account to login?")
            @Expose
            public Boolean allowLogin = false;

            @Lore("Will auto register when player register?")
            @Expose
            public Boolean allowRegister = false;

            @Lore("The SodionApi url.")
            @Expose
            public String url = "http://example.com/SodionAuth.php";

            @Lore("SodionAuth Api Key .")
            @Expose
            public String key = "YOUR_KEY_HERE";
        }
    }

    public static class DependenciesBean extends Configure {
        @Lore("Maven Repo Url")
        @Lore("If you are in China, you could try this")
        @Lore("https://maven.aliyun.com/repository/central")
        @Expose
        public String mavenRepository = "https://repo1.maven.org/maven2/";
    }

    public static class BungeeBean extends Configure {
        @Lore("The key use when SodionAuth run as Bungee")
        @Lore("It should as same as Server's serverKey")
        @Expose
        public String clientKey = Helper.toStringUuid(UUID.randomUUID());

        @Lore("The key use when SodionAuth run as Server")
        @Lore("It should as same as Bungee's clientKey")
        @Lore("If you have a Bungee outside the Bungee")
        @Lore("You should make this as same as outside Bungee's clientKet")
        @Expose
        public String serverKey = Helper.toStringUuid(UUID.randomUUID());
    }

    public static class SessionBean extends Configure {
        @Lore("Auto login when player rejoin?")
        @Lore("If enable, make sure server can get player's true ip.")
        @Expose
        public Boolean enable = false;

        @Lore("Player can autoLogin in (seconds).")
        @Expose
        public Integer timeout = 3600;
    }

    public static class SpawnBean extends Configure {
        @Expose
        public String world = null;

        @Expose
        public Double x = null;

        @Expose
        public Double y = null;

        @Expose
        public Double z = null;

        @Expose
        public Float yaw = null;

        @Expose
        public Float pitch = null;
    }

    public static class TeleportBean extends Configure {
        @Lore("Should player login in spawn during login?")
        @Expose
        public Boolean tpSpawnBeforeLogin = true;

        @Lore("Should player tp to location where they quit?")
        @Expose
        public Boolean tpBackAfterLogin = true;
    }

    public static class SecurityBean extends Configure {
        @Lore("Should hidden player's Inventory before login in?")
        @Lore("It only works in bukkit. And ProtocolLib is need.")
        @Expose
        public Boolean hideInventory = true;

        @Lore("Should keep player in spectator mode before login in?")
        @Expose
        public Boolean spectatorLogin = true;

        @Lore("The default gamemode for player.")
        @Lore("0 Survival, 1 Creative, 2 Adventure, 3 Spectator")
        @Expose
        public Integer defaultGamemode = 0;

        @Lore("The time show tip.")
        @Expose
        public Integer showTipsTime = 5;

        @Lore("The time for login. If player use too much time, they will be kicked.")
        @Expose
        public Integer maxLoginTime = 30;

        @Lore("Could forbid player send message?")
        @Lore("It should enable when SodionAuth in auth server.")
        @Expose
        public Boolean cancelChatAfterLogin = false;
    }

    public static class ProtectionBean extends Configure {
        @Expose
        public ProxySystemsBean ProxySystems = new ProxySystemsBean();

        @Expose
        public GeoIpBean GeoIp = new GeoIpBean();

        @Expose
        public RateLimitBean RateLimit = new RateLimitBean();

        public static class ProxySystemsBean extends Configure {
            @Lore("Enable anti-proxy system")
            @Expose
            public Boolean enable = true;

            @Lore("The time update proxy list")
            @Expose
            public Integer updateTime = 60;

            @Lore("Enable which proxy list provider")
            @Expose
            public Map<String, Boolean> proxiesProvider = ImmutableMap.of(
                    "LiuLiu", true,
                    "QiYun", true
            );

            @Lore("Enable local address join?")
            @Expose
            public Boolean enableLocal = true;
        }

        public static class GeoIpBean extends Configure {
            @Lore("Enable anti-proxy system")
            @Expose
            public Boolean enable = true;

            @Lore("Could List country player join?")
            @Lore("Use ISO country id")
            @Lore("UNKNOWN means GeoIp can't know their countries.")
            @Expose
            public Map<String, Boolean> countries = ImmutableMap.of(
                    "CN", true,
                    "UNKNOWN", true
            );

            @Lore("Could player who not list in countries join?")
            @Expose
            public Boolean other = true;
        }

        public static class RateLimitBean extends Configure {
            @Lore("Enable rateLimit system")
            @Expose
            public Boolean enable = true;

            @Lore("Permit produce per second.")
            @Expose
            public Integer permitsPerSecond = 10;

            @Lore("Player join will take permit.")
            @Expose
            public Integer join = 1;

            @Lore("Player register will take permit.")
            @Expose
            public Integer register = 10;

            @Lore("Player login will take permit.")
            @Expose
            public Integer login = 5;

        }
    }
}

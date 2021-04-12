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
import org.checkerframework.checker.units.qual.A;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.Helper;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainConfiguration {

    @Lore("The default language should message use.")
    public String defaultLang = "en";
    @Lore("The server's ID")
    public String serverId = Helper.toStringUuid(UUID.randomUUID());
    public DatabaseBean database = new DatabaseBean();
    public ApiBean api = new ApiBean();
    public BungeeBean bungee = new BungeeBean();
    public DependenciesBean dependencies = new DependenciesBean();
    public SessionBean session = new SessionBean();
    @Lore("Where should player login?")
    @Lore("Player will be teleported to world spawn after login.")
    @Lore("If set null, It will use world spawn.")
    public SpawnBean spawn = new SpawnBean();
    public TeleportBean teleport = new TeleportBean();
    public SecurityBean security = new SecurityBean();
    public ProtectionBean protection = new ProtectionBean();
    public static class DatabaseBean {
        @Lore("The database type should SodionAuth use.")
        public String type;
        @Lore("The table prefix should SodionAuth use.")
        public String tablePrefix = "sa_";
        @Lore("The password hash should SodionAuth use as default.")
        @Lore("If a player with other kind of hash, It will transfer into this when he login in.")
        @Lore("If It sets to null, sodionAuth will use Api only.")
        public String passwordHash = "BCrypt";

        public SqliteBean sqlite = new SqliteBean();
        public MysqlBean mysql = new MysqlBean();

        public static class SqliteBean {
            @Lore("The path to sqlite database")
            public String path = "Users.db";
            @Lore("Is absolute path?")
            public Boolean absolute = false;
        }

        public static class MysqlBean {
            @Lore("The hostname to mysql.")
            @Lore("It likes 127.0.0.1:3306")
            public String host = "localhost:3306";
            @Lore("The username for mysql.")
            public String username = "root";
            @Lore("The password for mysql.")
            public String password = "";
            @Lore("The database should SodionAuth use.")
            public String database = "sodionauth";
        }
    }
    public static class ApiBean {
        public XenforoBean[] xenforo = {new XenforoBean()};
        public WebBean[] web = {new WebBean()};
       public static class XenforoBean {
           @Lore("Could player use this account to login?")
           public Boolean allowLogin = false;
           @Lore("Will auto register when player register?")
           public Boolean allowRegister = false;
           @Lore("The Xenforo API url. Likes http://example.com/api .")
            public String url = "http://example.com/api";
           @Lore("A super-admin key with these permissions: auth & user:read.")
           @Lore("It could create at http://example.com/admin.php?api-keys .")
            public String key = "YOUR_KEY_HERE";
        }
        public static class WebBean {
            @Lore("Could player use this account to login?")
            public Boolean allowLogin = false;
            @Lore("Will auto register when player register?")
            public Boolean allowRegister = false;
           @Lore("The SodionApi url.")
            public String url = "http://example.com/SodionAuth.php";
           @Lore("SodionAuth Api Key .")
            public String key = "YOUR_KEY_HERE";
        }
    }

    public static class DependenciesBean {
        @Lore("Maven Repo Url")
        @Lore("If you are in China, you could try this")
        @Lore("https://maven.aliyun.com/repository/central")
        public String mavenRepository = "https://repo1.maven.org/maven2/";
    }

    public static class BungeeBean {
        @Lore("The key use when SodionAuth run as Bungee")
        @Lore("It should as same as Server's serverKey")
        public String clientKey = Helper.toStringUuid(UUID.randomUUID());
        @Lore("The key use when SodionAuth run as Server")
        @Lore("It should as same as Bungee's clientKey")
        @Lore("If you have a Bungee outside the Bungee")
        @Lore("You should make this as same as outside Bungee's clientKet")
        public String serverKey = Helper.toStringUuid(UUID.randomUUID());
    }

    public static class SessionBean {
        @Lore("Auto login when player rejoin?")
        @Lore("If enable, make sure server can get player's true ip.")
        public Boolean enable = false;
        @Lore("Player can autoLogin in (seconds).")
        public Integer timeout = 3600;
    }

    public static class SpawnBean {
        public String world = null;
        public Double x = null;
        public Double y= null;
        public Double z= null;
        public Float yaw= null;
        public Float pitch= null;
    }

    public static class TeleportBean {
        @Lore("Should player login in spawn during login?")
        public Boolean tpSpawnBeforeLogin = true;
        @Lore("Should player tp to location where they quit?")
        public Boolean tpBackAfterLogin = true;
    }

    public static class SecurityBean {
        @Lore("Should hidden player's Inventory before login in?")
        @Lore("It only works in bukkit. And ProtocolLib is need.")
        public Boolean hideInventory = true;
        @Lore("Should keep player in spectator mode before login in?")
        public Boolean spectatorLogin = true;
        @Lore("The default gamemode for player.")
        @Lore("0 Survival, 1 Creative, 2 Adventure, 3 Spectator")
        public Integer defaultGamemode = 0;
        @Lore("The time show tip.")
        public Integer showTipsTime = 5;
        @Lore("The time for login. If player use too much time, they will be kicked.")
        public Integer maxLoginTime = 30;
        @Lore("Could forbid player send message?")
        @Lore("It should enable when SodionAuth in auth server.")
        public Boolean cancelChatAfterLogin = false;
    }

    public static class ProtectionBean {
        public ProxySystemsBean ProxySystems = new ProxySystemsBean();
        public GeoIPBean GeoIP = new GeoIPBean();
        public RateLimitBean RateLimit = new RateLimitBean();
        public static class ProxySystemsBean {
            @Lore("Enable anti-proxy system")
            public Boolean enable = true;
            @Lore("The time update proxy list")
            public Integer updateTime = 60;
            @Lore("Enable which proxy list provider")
            public Map<String, Boolean> proxiesProvider = ImmutableMap.of(
                    "LiuLiu",true,
                    "QiYun",true
            );
            @Lore("Enable local address join?")
            public Boolean enableLocal = true;
        }

        public static class GeoIPBean {
            @Lore("Enable anti-proxy system")
            public Boolean enable = true;
            @Lore("Could List country player join?")
            @Lore("Use ISO country id")
            @Lore("UNKNOWN means GeoIp can't know their countries.")
            public Map<String, Boolean> countries = ImmutableMap.of(
                    "CN",true,
                    "UNKNOWN",true
            );;
            @Lore("Could player who not list in countries join?")
            public Boolean other = true;
       }

        public static class RateLimitBean {
            @Lore("Enable rateLimit system")
            public Boolean enable = true;
            @Lore("Permit produce per second.")
            public Integer permitsPerSecond = 10;
            @Lore("Player join will take permit.")
            public Integer join = 1;
            @Lore("Player register will take permit.")
            public Integer register = 10;
            @Lore("Player login will take permit.")
            public Integer login = 5;
        }
    }
}

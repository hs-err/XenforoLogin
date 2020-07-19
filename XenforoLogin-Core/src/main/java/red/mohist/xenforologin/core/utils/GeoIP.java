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

package red.mohist.xenforologin.core.utils;

import com.maxmind.geoip2.DatabaseReader;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

public class GeoIP {
    public static GeoIP instance;
    public DatabaseReader cityReader;
    public DatabaseReader countryReader;

    public GeoIP() throws IOException {
        instance = this;
        Helper.instance.saveResource("GeoLite2-City.mmdb", false);
        Helper.instance.saveResource("GeoLite2-Country.mmdb", false);
        File database = new File(Helper.getConfigPath("GeoLite2-City.mmdb"));
        cityReader = new DatabaseReader.Builder(database).build();
        database = new File(Helper.getConfigPath("GeoLite2-Country.mmdb"));
        countryReader = new DatabaseReader.Builder(database).build();
    }

    public static String city(String ip) {
        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            return instance.cityReader.city(ipAddress).getCity().getName();
        } catch (Exception e) {
            return Helper.langFile("last_login_unknown");
        }
    }

    public static String country(String ip) {
        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            return instance.countryReader.country(ipAddress).getCountry().getIsoCode();
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }
}

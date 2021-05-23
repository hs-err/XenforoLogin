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

package red.mohist.sodionauth.core.protection.implementations.geoip;

import com.maxmind.geoip2.DatabaseReader;
import red.mohist.sodionauth.core.SodionAuthCore;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.protection.SecuritySystem;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.Helper;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

public class GeoIp implements SecuritySystem {
    private DatabaseReader countryReader;

    public GeoIp() {
        try {
            File database = new File(Helper.getConfigPath("GeoLite2-Country.mmdb"));
            countryReader = new DatabaseReader.Builder(database).build();
        } catch (IOException e) {
            e.printStackTrace();
            Helper.getLogger().warn("Can't open GeoLite2-Country.mmdb.");
            Helper.getLogger().warn("Download it from https://updates.maxmind.com/geoip/databases/GeoLite2-Country/update");
            Helper.getLogger().warn("Put it into configure dir");
            Helper.getLogger().warn("Otherwise you should turn Geoip off in config file");
            Helper.getLogger().warn("Set protection.GeoIP.enable false");
            SodionAuthCore.instance.loadFail();
        }
    }

    @Override
    public String canJoin(AbstractPlayer player) {
        if(player.getAddress() == null){
            return null;
        }
        String country = "UNKNOWN";
        try {
            InetAddress ipAddress = player.getAddress();
            country = countryReader.country(ipAddress).getCountry().getIsoCode();
        } catch (Exception ignored) {
        }
        if (Config.protection.GeoIp.countries.getOrDefault(country,
                Config.protection.GeoIp.other)) {
            return null;
        } else {
            return player.getLang().errors.countryLimit;
        }
    }

    @Override
    public String canLogin(AbstractPlayer player) {
        return null;
    }

    @Override
    public String canRegister(AbstractPlayer player) {
        return null;
    }
}

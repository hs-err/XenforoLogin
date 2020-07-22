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

package red.mohist.sodionauth.core.protection.implementations.geoip;

import com.maxmind.geoip2.DatabaseReader;
import red.mohist.sodionauth.core.modules.AbstractPlayer;
import red.mohist.sodionauth.core.protection.SecureSystem;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.Helper;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

public class GeoIP implements SecureSystem {
    private final DatabaseReader countryReader;
    public GeoIP() throws IOException {
        Helper.instance.saveResource("GeoLite2-Country.mmdb", false);
        File database = new File(Helper.getConfigPath("GeoLite2-Country.mmdb"));
        countryReader = new DatabaseReader.Builder(database).build();
    }
    @Override
    public String canJoin(AbstractPlayer player) {
        String country = "UNKNOWN";
        try {
            InetAddress ipAddress = player.getAddress();
            country = countryReader.country(ipAddress).getCountry().getIsoCode();
        }catch (Throwable ignored){}
        if(Config.getBoolean("protects.GeoIP.lists."+country,
                Config.getBoolean("protects.GeoIP.default"))){
            return null;
        }else{
            return Helper.langFile("errors.country_limit");
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

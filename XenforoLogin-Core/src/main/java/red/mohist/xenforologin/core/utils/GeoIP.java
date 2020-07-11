/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You are not permitted to interfere any protection that prevents loading in CatServer
 *
 * Copyright (c) 2020 Mohist-Community.
 *
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
        instance=this;
        Helper.instance.saveResource("GeoLite2-City.mmdb",false);
        Helper.instance.saveResource("GeoLite2-Country.mmdb",false);
        File database = new File(Helper.getConfigPath("GeoLite2-City.mmdb"));
        cityReader = new DatabaseReader.Builder(database).build();
        database = new File(Helper.getConfigPath("GeoLite2-Country.mmdb"));
        countryReader = new DatabaseReader.Builder(database).build();
    }
    public static String city(String ip) {
        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            return instance.cityReader.city(ipAddress).getCity().getName();
        }catch (Exception e){
            return Helper.langFile("last_login_unknown");
        }
    }
    public static String country(String ip) {
        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            return instance.countryReader.country(ipAddress).getCountry().getIsoCode();
        }catch (Exception e){
            return "UNKNOWN";
        }
    }
}

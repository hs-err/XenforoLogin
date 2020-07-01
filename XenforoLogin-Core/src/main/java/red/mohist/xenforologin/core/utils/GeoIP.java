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
    public DatabaseReader reader;
    public GeoIP() throws IOException {
        instance=this;
        Helper.instance.saveResource("GeoLite2-City.mmdb",false);
        File database = new File(Helper.getConfigPath("GeoLite2-City.mmdb"));
        reader = new DatabaseReader.Builder(database).build();
    }
    public static String city(String ip) {
        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            return instance.reader.city(ipAddress).getCity().getName();
        }catch (Exception e){
            return Helper.langFile("last_login_unknown");
        }
    }
}

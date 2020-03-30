package red.mohist.xenforologin.core.interfaces;

import red.mohist.xenforologin.core.modules.LocationInfo;
import red.mohist.xenforologin.core.modules.PlayerInfo;

public interface LoaderAPI {
    void sendMessage(PlayerInfo player, String message);

    void kickPlayer(PlayerInfo player, String message);

    void log(String message);

    void info(String message);

    void warn(String message);

    LocationInfo getSpawn(String world);

    LocationInfo getLocation(PlayerInfo player);

    void teleport(PlayerInfo player, LocationInfo location);

    Object getConfigValue(String key);

    Object getConfigValue(String key, Object def);

    Object getConfigValue(String file, String key, Object def);

    void setConfigValue(String file, String key, Object value);

    void login(PlayerInfo player);
}

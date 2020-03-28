package red.mohist.xenforologin.interfaces;

import red.mohist.xenforologin.modules.LocationInfo;
import red.mohist.xenforologin.modules.PlayerInfo;

public interface LoaderAPI {
    public void sendMessage(PlayerInfo player, String message);
    public void kickPlayer(PlayerInfo player, String message);
    public void log(String message);
    public void info(String message);
    public void warn(String message);
    public LocationInfo getSpawn(String world);
    public LocationInfo getLocation(PlayerInfo player);
    public void teleport(PlayerInfo player,LocationInfo location);
    public Object getConfigValue(String key);
    public Object getConfigValue(String key,Object def);
    public Object getConfigValue(String file,String key,Object def);
    public void setConfigValue(String file, String key, Object value);

    public void login(PlayerInfo player);
}

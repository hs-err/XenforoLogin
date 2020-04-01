package red.mohist.xenforologin.core.interfaces;

import red.mohist.xenforologin.core.modules.AbstractPlayer;
import red.mohist.xenforologin.core.modules.LocationInfo;

import java.util.logging.Logger;

public interface PlatformAdapter {

    Logger getLogger();

    LocationInfo getSpawn(String world);

    Object getConfigValue(String key);

    Object getConfigValue(String key, Object def);

    Object getConfigValue(String file, String key, Object def);

    int getConfigValueInt(String key, int def);

    void setConfigValue(String file, String key, Object value);

    void login(AbstractPlayer player);

    void sendBlankInventoryPacket(AbstractPlayer player);
}

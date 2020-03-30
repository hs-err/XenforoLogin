package red.mohist.xenforologin.core.modules;


import red.mohist.xenforologin.core.XenforoLogin;

import java.util.UUID;

public class PlayerInfo {
    public String username;
    public UUID uuid;
    public String ip;

    public PlayerInfo(String username, UUID uuid, String ip) {
        this.username = username;
        this.uuid = uuid;
        this.ip = ip;
    }

    public void sendMessage(String message) {
        XenforoLogin.instance.api.sendMessage(this, message);
    }

    public void teleport(LocationInfo location) {
        XenforoLogin.instance.api.teleport(this, location);
    }

    public void kick(String message) {
        XenforoLogin.instance.api.kickPlayer(this, message);
    }

    public LocationInfo getLocation() {
        return XenforoLogin.instance.api.getLocation(this);
    }
}

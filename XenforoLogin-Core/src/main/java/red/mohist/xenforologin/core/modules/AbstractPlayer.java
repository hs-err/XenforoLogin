package red.mohist.xenforologin.core.modules;


import java.net.InetAddress;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractPlayer {
    public String username;
    public UUID uuid;
    public InetAddress ip;

    public AbstractPlayer(String username, UUID uuid, InetAddress ip) {
        this.username = username;
        this.uuid = uuid;
        this.ip = ip;
    }

    public abstract void sendMessage(String message);

    public abstract CompletableFuture<Boolean> teleport(LocationInfo location);

    public abstract void kick(String message);

    public abstract LocationInfo getLocation();
}

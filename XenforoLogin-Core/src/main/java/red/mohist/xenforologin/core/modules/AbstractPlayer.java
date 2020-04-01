package red.mohist.xenforologin.core.modules;


import java.net.InetAddress;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractPlayer {
    private String name;
    private UUID uuid;
    private InetAddress address;

    public AbstractPlayer(String name, UUID uuid, InetAddress address) {
        this.name = name;
        this.uuid = uuid;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public InetAddress getAddress() {
        return address;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public abstract void sendMessage(String message);

    public abstract CompletableFuture<Boolean> teleport(LocationInfo location);

    public abstract void kick(String message);

    public abstract LocationInfo getLocation();

    public abstract boolean isOnline();
}

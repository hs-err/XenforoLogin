package red.mohist.xenforologin.bukkit.implementation;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import red.mohist.xenforologin.bukkit.BukkitLoader;
import red.mohist.xenforologin.core.modules.AbstractPlayer;
import red.mohist.xenforologin.core.modules.LocationInfo;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class BukkitPlayer extends AbstractPlayer {
    private final Player holder;

    public BukkitPlayer(Player holder) {
        super(holder.getName(), holder.getUniqueId(), Objects.requireNonNull(holder.getAddress()).getAddress());
        this.holder = holder;
    }

    @Override
    public void sendMessage(String message) {
        holder.sendMessage(message);
    }

    @Override
    public CompletableFuture<Boolean> teleport(LocationInfo location) {
        try {
            return holder.teleportAsync(new Location(Bukkit.getWorld(location.world),
                    location.x, location.y, location.z, location.yaw, location.pitch));
        } catch (NoSuchMethodError error) {
            BukkitLoader.instance.getSLF4JLogger()
                    .debug("You are not running Paper? Using synchronized teleport.", error);
            CompletableFuture<Boolean> booleanCompletableFuture = new CompletableFuture<>();
            Bukkit.getScheduler().runTask(BukkitLoader.instance, () ->
                    booleanCompletableFuture.complete(holder.teleport(new Location(Bukkit.getWorld(location.world),
                            location.x, location.y, location.z, location.yaw, location.pitch))));
            return booleanCompletableFuture;
        }
    }

    @Override
    public void kick(String message) {
        holder.kickPlayer(message);
    }

    @Override
    public LocationInfo getLocation() {
        final Location holderLocation = holder.getLocation();
        return new LocationInfo(
                holderLocation.getWorld().getName(),
                holderLocation.getX(),
                holderLocation.getY(),
                holderLocation.getZ(),
                holderLocation.getYaw(),
                holderLocation.getPitch()
        );
    }
}

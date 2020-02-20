package red.mohist.xenforologin.interfaces;

import org.bukkit.entity.Player;
import red.mohist.xenforologin.enums.ResultType;

import javax.annotation.Nonnull;

public interface ForumSystem {

    boolean isAvailable();

    @Nonnull
    ResultType register(Player player, String password, String email);

    @Nonnull
    ResultType login(Player player, String password);

    @Nonnull
    ResultType join(Player player);

}

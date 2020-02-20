package red.mohist.xenforologin.interfaces;

import org.bukkit.entity.Player;
import red.mohist.xenforologin.enums.ResultType;

import javax.annotation.Nonnull;

public interface ForumSystem {

    @SuppressWarnings({"SameReturnValue", "unused"})
    boolean isAvailable();

    @SuppressWarnings({"SameReturnValue", "unused"})
    @Nonnull
    ResultType register(Player player, String password, String email);

    @Nonnull
    ResultType login(Player player, String password);

    @SuppressWarnings("unused")
    @Nonnull
    ResultType join(Player player);

    @Nonnull
    ResultType join(String name);

}

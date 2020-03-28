package red.mohist.xenforologin.forums;

import org.bukkit.entity.Player;
import red.mohist.xenforologin.enums.ResultType;
import red.mohist.xenforologin.modules.PlayerInfo;

import javax.annotation.Nonnull;

public interface ForumSystem {

    @SuppressWarnings({"SameReturnValue", "unused"})
    boolean isAvailable();

    @SuppressWarnings({"SameReturnValue", "unused"})
    @Nonnull
    ResultType register(PlayerInfo player, String password, String email);

    @Nonnull
    ResultType login(PlayerInfo player, String password);

    @SuppressWarnings("unused")
    @Nonnull
    ResultType join(PlayerInfo player);

    @Nonnull
    ResultType join(String name);

}

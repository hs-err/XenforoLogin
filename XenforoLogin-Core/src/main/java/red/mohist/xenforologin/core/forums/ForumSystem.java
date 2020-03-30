package red.mohist.xenforologin.core.forums;

import red.mohist.xenforologin.core.enums.ResultType;
import red.mohist.xenforologin.core.modules.AbstractPlayer;

import javax.annotation.Nonnull;

public interface ForumSystem {

    @SuppressWarnings({ "SameReturnValue", "unused" })
    boolean isAvailable();

    @SuppressWarnings({ "SameReturnValue", "unused" })
    @Nonnull
    ResultType register(AbstractPlayer player, String password, String email);

    @Nonnull
    ResultType login(AbstractPlayer player, String password);

    @SuppressWarnings("unused")
    @Nonnull
    ResultType join(AbstractPlayer player);

    @Nonnull
    ResultType join(String name);

}

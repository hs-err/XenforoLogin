/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.core.forums;

import red.mohist.xenforologin.core.enums.ResultType;
import red.mohist.xenforologin.core.modules.AbstractPlayer;

import javax.annotation.Nonnull;

public interface ForumSystem {

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

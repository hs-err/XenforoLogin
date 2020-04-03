/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.core.hasher.implementations;

import red.mohist.xenforologin.core.hasher.HasherTool;

import javax.annotation.Nonnull;

public class PlainSaltHasherTool extends HasherTool {
    public PlainSaltHasherTool(int saltLength) {
        super(saltLength);
    }

    @Nonnull
    @Override
    public boolean needSalt() {
        return true;
    }
}

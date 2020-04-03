/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.core.hasher.implementations;

public class SHA1SaltHasherTool extends SHA1HasherTool {
    public SHA1SaltHasherTool(int saltLength) {
        super(saltLength);
    }

    @Override
    public boolean needSalt() {
        return true;
    }
}

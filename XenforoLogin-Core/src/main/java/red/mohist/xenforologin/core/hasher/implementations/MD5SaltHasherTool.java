/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.core.hasher.implementations;

public class MD5SaltHasherTool extends MD5HasherTool {
    public MD5SaltHasherTool(int saltLength) {
        super(saltLength);
    }

    @Override
    public boolean needSalt() {
        return true;
    }
}

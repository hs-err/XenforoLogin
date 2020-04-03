/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.core.hasher;

import red.mohist.xenforologin.core.hasher.implementations.PlainHasherTool;
import red.mohist.xenforologin.core.hasher.implementations.PlainSaltHasherTool;

public class HasherTools {
    private static HasherTool currentHasherTool = null;

    public static void loadHasher(String name,int saltLength) {
        HasherTool cs;
        switch (name) {
            case "plain":
                cs = new PlainHasherTool(saltLength);
                break;
            case "plainSalt":
                cs = new PlainSaltHasherTool(saltLength);
                break;
            default:
                cs = null;
        }
        if (cs == null) throw new NullPointerException();
        currentHasherTool = cs;
    }

    public static HasherTool getCurrentSystem() {
        return currentHasherTool;
    }

}

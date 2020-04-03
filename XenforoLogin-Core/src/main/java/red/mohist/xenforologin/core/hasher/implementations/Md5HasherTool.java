/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.core.hasher.implementations;

import red.mohist.xenforologin.core.hasher.HasherTool;

import javax.annotation.Nonnull;
public class Md5HasherTool extends HasherTool {
    public Md5HasherTool(int saltLength) {
        super(saltLength);
    }

    //@Nonnull
    //@Override
    //public String hash(String data) {
    //    return Hasher;
    //}
}

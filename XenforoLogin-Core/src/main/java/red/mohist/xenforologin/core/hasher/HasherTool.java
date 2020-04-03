/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.core.hasher;

import javax.annotation.Nonnull;
import java.util.Random;

public class HasherTool {
    protected int saltLength;

    public HasherTool(int saltLength) {
        this.saltLength = saltLength;
    }

    @Nonnull
    public String hash(String data) {
        return data;
    }

    @Nonnull
    public String hash(String data, String salt) {
        return hash(hash(data) + salt);
    }

    @Nonnull
    public boolean verify(String hash, String data) {
        return hash(data).equals(hash);
    }

    @Nonnull
    public boolean verify(String hash, String data, String salt) {
        return hash(hash(data) + salt).equals(hash);
    }

    @Nonnull
    public boolean needSalt() {
        return false;
    }

    @Nonnull
    public String generateSalt() {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random1 = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < saltLength; i++) {
            int number = random1.nextInt(str.length());
            char charAt = str.charAt(number);
            sb.append(charAt);
        }
        return String.valueOf(sb);
    }
}

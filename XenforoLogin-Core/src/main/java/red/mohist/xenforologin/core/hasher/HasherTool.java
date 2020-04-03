/*
 * This file is part of XenforoLogin, licensed under the GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * Copyright (c) 2020 Mohist-Community.
 *
 */

package red.mohist.xenforologin.core.hasher;

import javax.annotation.Nonnull;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class HasherTool {
    protected int saltLength;

    public HasherTool(int saltLength) {
        this.saltLength = saltLength;
    }

    public String hash(String data) {
        return data;
    }

    public String hash(String data, String salt) {
        return hash(hash(data) + salt);
    }

    public boolean verify(String hash, String data) {
        return hash(data).toLowerCase().equals(hash.toLowerCase());
    }

    public boolean verify(String hash, String data, String salt) {
        return hash(hash(data) + salt).toLowerCase().equals(hash.toLowerCase());
    }

    public boolean needSalt() {
        return false;
    }

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

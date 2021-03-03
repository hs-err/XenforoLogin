/*
 * Copyright 2021 Mohist-Community
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package red.mohist.sodionauth.core.hasher;

import java.util.Random;

public class HasherTool {
    protected final int saltLength;

    public HasherTool(int saltLength) {
        this.saltLength = saltLength;
    }

    public String hash(String data) {
        return data;
    }

    public String hash(String data, String salt) {
        return hash(hash(data) + salt);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean verify(String hash, String data) {
        return hash(data).toLowerCase().equals(hash.toLowerCase());
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean verify(String hash, String data, String salt) {
        return hash(hash(data) + salt).toLowerCase().equals(hash.toLowerCase());
    }

    public boolean needSalt() {
        return false;
    }

    public String generateSalt() {
        @SuppressWarnings("SpellCheckingInspection")
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

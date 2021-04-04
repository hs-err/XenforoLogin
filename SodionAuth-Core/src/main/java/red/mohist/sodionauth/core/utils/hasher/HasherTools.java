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

/*
 * 2020.4.4 逝者安息
 * 哀悼前线牺牲的烈士和已经逝世的同胞
 * 愿逝者安息！愿生者奋发！愿祖国昌盛！
 */
package red.mohist.sodionauth.core.utils.hasher;

import red.mohist.sodionauth.core.utils.hasher.implementations.*;

public class HasherTools {
    private static HasherTool currentHasherTool = null;

    public static void loadHasher(String name, int saltLength) {
        HasherTool cs;
        switch (name) {
            case "BCrypt":
                cs = new BCryptHasherTool(saltLength);
                break;
            case "MD5":
                cs = new MD5HasherTool(saltLength);
                break;
            case "MD5Salt":
                cs = new MD5SaltHasherTool(saltLength);
                break;
            case "Plain":
                cs = new PlainHasherTool(saltLength);
                break;
            case "SHA1":
                cs = new SHA1HasherTool(saltLength);
                break;
            case "SHA1Salt":
                cs = new SHA1SaltHasherTool(saltLength);
                break;
            case "SHA224":
                cs = new SHA224HasherTool(saltLength);
                break;
            case "SHA224Salt":
                cs = new SHA224SaltHasherTool(saltLength);
                break;
            case "SHA256":
                cs = new SHA256HasherTool(saltLength);
                break;
            case "SHA256Salt":
                cs = new SHA256SaltHasherTool(saltLength);
                break;
            case "SHA384":
                cs = new SHA384HasherTool(saltLength);
                break;
            case "SHA384Salt":
                cs = new SHA384SaltHasherTool(saltLength);
                break;
            case "SHA512":
                cs = new SHA512HasherTool(saltLength);
                break;
            case "SHA512Salt":
                cs = new SHA512SaltHasherTool(saltLength);
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

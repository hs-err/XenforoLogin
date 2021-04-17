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

import com.google.common.collect.ImmutableMap;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.hasher.implementations.*;

import java.util.Map;

public class HasherTools {
    private static Map<String,HasherTool> hasherTools;
    static {
        int saltLength = Config.database.saltLength;
        hasherTools = new ImmutableMap.Builder<String,HasherTool>()
                .put("BCrypt",new BCryptHasherTool(saltLength))
                .put("MD5",new MD5HasherTool(saltLength))
                .put("MD5Salt",new MD5SaltHasherTool(saltLength))
                .put("Plain",new PlainHasherTool(saltLength))
                .put("SHA1",new SHA1HasherTool(saltLength))
                .put("SHA1Salt",new SHA1SaltHasherTool(saltLength))
                .put("SHA224",new SHA224HasherTool(saltLength))
                .put("SHA224Salt",new SHA224SaltHasherTool(saltLength))
                .put("SHA256",new SHA256HasherTool(saltLength))
                .put("SHA256Salt",new SHA256SaltHasherTool(saltLength))
                .put("SHA384",new SHA384HasherTool(saltLength))
                .put("SHA384Salt",new SHA384SaltHasherTool(saltLength))
                .put("SHA512",new SHA512HasherTool(saltLength))
                .put("SHA512Salt",new SHA512SaltHasherTool(saltLength))
                .build();
    }
    public static HasherTool getByName(String name) {
        return hasherTools.get(name);
    }

    public static HasherTool getDefault() {
        return getByName(Config.database.passwordHash);
    }
}

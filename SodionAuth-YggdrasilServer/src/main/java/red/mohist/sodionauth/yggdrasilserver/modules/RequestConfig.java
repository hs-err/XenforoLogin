/*
 * Copyright 2020 Mohist-Community
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

package red.mohist.sodionauth.yggdrasilserver.modules;

import java.util.ArrayList;
import java.util.HashMap;

public class RequestConfig {
    public HashMap<String, String> meta;
    public ArrayList<String> skinDomains;
    public String signaturePublickey;

    public RequestConfig() {
        meta = new HashMap<String, String>();
        skinDomains = new ArrayList<String>();
    }

    public RequestConfig addMeta(String key, String value) {
        meta.put(key, value);
        return this;
    }

    public RequestConfig removeMeta(String key) {
        meta.remove(key);
        return this;
    }

    public RequestConfig addSkinDomains(String value) {
        skinDomains.add(value);
        return this;
    }

    public RequestConfig setSignaturePublickey(String value) {
        signaturePublickey = value;
        return this;
    }
}

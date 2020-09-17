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

package red.mohist.sodionauth.core.enums;

import java.util.Map;

public enum ResultType {
    OK, SERVER_ERROR, PASSWORD_INCORRECT, ERROR_NAME, NO_USER, UNKNOWN, USER_EXIST, EMAIL_EXIST, EMAIL_WRONG;

    Map<String, Object> inheritedMap;
    private boolean shouldLogin;

    ResultType() {
        shouldLogin = false;
    }

    public ResultType inheritedObject(String key,Object value) {
        inheritedMap.put(key,value);
        return this;
    }

    public ResultType shouldLogin(boolean should) {
        shouldLogin = should;
        return this;
    }

    public Map<String, Object> getInheritedObject(){
        return inheritedMap;
    }
    public Object getInheritedObject(String key) {
        return inheritedMap.get(key);
    }

    public Object getInheritedObject(String key,Object defaultValue) {
        return inheritedMap.getOrDefault(key,defaultValue);
    }

    public boolean isShouldLogin() {
        return shouldLogin;
    }
}

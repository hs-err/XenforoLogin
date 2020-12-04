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
import java.util.List;

public class Profile {
    public String id;
    public String name;
    public List<Property> properties;

    public Profile(){
        properties=new ArrayList<>();
    }

    public Profile setId(String id) {
        this.id = id;
        return this;
    }

    public Profile setName(String name) {
        this.name = name;
        return this;
    }

    public Profile addProperties(String name, String value) {
        properties.add(new Property(name, value));
        return this;
    }

    public Profile addProperties(Property property) {
        properties.add(property);
        return this;
    }

    public Profile removeProperties(String name) {
        for (int i = 0; i < properties.size(); i++) {
            if (properties.get(i).name.equals(name)) {
                properties.remove(i);
                break;
            }
        }
        return this;
    }

    public Property getProperty(String name) {
        for (Property property : properties) {
            if(property.name.equals(name)){
                return property;
            }
        }
        return null;
    }
}

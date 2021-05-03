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

package red.mohist.sodionauth.core.database.entities;

import red.mohist.sodionauth.core.services.Service;
import red.mohist.sodionauth.core.utils.Helper;

import java.io.Serializable;
import java.lang.reflect.Field;

public abstract class Entity implements Serializable {
    public int save() {
        try {
            Field idField = this.getClass().getDeclaredField("id");
            if (!idField.isAccessible()) {
                idField.setAccessible(true);
            }
            if (idField.get(this) == null) {
                int id = (int) Service.database.suid.insertAndReturnId(this);
                idField.set(this, id);
                return id;
            } else {
                Service.database.suid.update(this);
                return (int) idField.get(this);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Helper.getLogger().warn("Id field don't exist or accessAble, can't save.", e);
            return -1;
        }
    }

    public void delete() {
        Service.database.suid.delete(this);
    }
}

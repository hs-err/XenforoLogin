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

package red.mohist.sodionauth.core.database;

import org.teasoft.bee.osql.annotation.Table;
import org.teasoft.honey.osql.core.BeeFactory;
import org.teasoft.honey.osql.core.HoneyFactory;

public abstract class Mapper {

    public static final HoneyFactory honeyFactory = BeeFactory.getHoneyFactory();

    public String translate(Class<?> entity) {
        if (entity.isAnnotationPresent(Table.class)) {
            return entity.getAnnotation(Table.class).value();
        } else {
            return BeeFactory.getHoneyFactory().getNameTranslate().toTableName(entity.getSimpleName());
        }
    }

    public abstract boolean isTableExist(String name);

    public boolean isTableExist(Class<?> entity) {
        return isTableExist(translate(entity));
    }

    public abstract void dropTable(String name);

    public void dropTable(Class<?> entity) {
        dropTable(translate(entity));
    }

    public void dropIfExist(String name) {
        if (isTableExist(name)) {
            dropTable(name);
        }
    }

    public void dropIfExist(Class<?> entity) {
        dropIfExist(translate(entity));
    }

    public abstract void createByEntity(Class<?> entity);

    protected abstract String getTypeName(Class<?> object);
}

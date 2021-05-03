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

import org.teasoft.bee.osql.annotation.Entity;
import org.teasoft.bee.osql.annotation.Table;
import org.teasoft.honey.osql.core.BeeFactory;
import org.teasoft.honey.osql.core.HoneyFactory;
import red.mohist.sodionauth.core.services.Service;

import java.lang.reflect.Field;

public abstract class Mapper {

    public HoneyFactory honeyFactory;

    public static String translateTable(Class<?> entity) {
        if (entity.isAnnotationPresent(Table.class)) {
            return entity.getAnnotation(Table.class).value();
        } else if (entity.isAnnotationPresent(Entity.class)) {
            return translateTable(entity.getAnnotation(Entity.class).value());
        } else {
            return translateTable(entity.getSimpleName());
        }
    }

    public static String translateTable(Field field) {
        return translateTable(field.getDeclaringClass());
    }

    public static String translateTable(String name) {
        return Service.database.nameTranslate.toTableName(name);
    }

    public static String translateField(Field field) {
        return translateField(field.getName());
    }

    public static String translateField(String name) {
        return Service.database.nameTranslate.toColumnName(name);
    }

    public abstract boolean isTableExist(String name);

    public boolean isTableExist(Class<?> entity) {
        return isTableExist(translateTable(entity));
    }

    public abstract boolean isFieldExist(String tableName, String fieldName);

    public boolean isFieldExist(Field field) {
        return isFieldExist(translateTable(field), translateField(field));
    }

    public abstract void dropTable(String name);

    public void dropTable(Class<?> entity) {
        dropTable(translateTable(entity));
    }

    public void dropIfExist(String name) {
        if (isTableExist(name)) {
            dropTable(name);
        }
    }

    public void dropIfExist(Class<?> entity) {
        dropIfExist(translateTable(entity));
    }

    public abstract void createByEntity(Class<?> entity);

    public void initEntity(Class<?> entity) {
        if (isTableExist(entity)) {
            Field[] fields = entity.getDeclaredFields();
            for (Field field : fields) {
                if (!isFieldExist(field)) {
                    addField(field);
                }
            }
        } else {
            createByEntity(entity);
        }
    }

    public abstract void addField(Field field);

    protected abstract String getTypeName(Class<?> object);
}

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

package red.mohist.sodionauth.core.database.sqlite;

import org.teasoft.bee.osql.annotation.Table;
import org.teasoft.honey.osql.core.BeeFactory;
import red.mohist.sodionauth.core.database.annotations.Ignore;
import red.mohist.sodionauth.core.database.Mapper;
import red.mohist.sodionauth.core.database.annotations.limits.NotNull;
import red.mohist.sodionauth.core.database.annotations.limits.PrimaryKey;

import java.lang.reflect.Field;

public class SqliteMapper extends Mapper {
    @Override
    public boolean isTableExist(String name){
        return honeyFactory.getBeeSql().select(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='"+name+"';"
        ).size() == 1;
    }

    @Override
    public void dropTable(String name) {
        honeyFactory.getBeeSql().modify("DROP TABLE "+name+";");
    }

    @Override
    public void createByEntity(Class<?> entity) {
        String tableName = translate(entity);
        StringBuilder sql = new StringBuilder("CREATE TABLE " + tableName + " (");
        Field[] fields = entity.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if(i != 0){
                sql.append(",");
            }
            sql.append(honeyFactory.getNameTranslate().toColumnName(field.getName()));
            if(field.isAnnotationPresent(Ignore.class)){
                break;
            }
            sql.append(" ").append(getTypeName(field.getType()));
            if(field.isAnnotationPresent(PrimaryKey.class)){
                sql.append(" PRIMARY KEY");
            }
            if(field.isAnnotationPresent(NotNull.class)){
                sql.append(" NOT NULL");
            }
        }
        sql.append(");");
        honeyFactory.getBeeSql().modify(sql.toString());
    }

    @Override
    protected String getTypeName(Class<?> object) {
        if (String.class.equals(object)) {
            return "STRING";
        } else if (Integer.class.equals(object)) {
            return "INTEGER";
        } else if (Boolean.class.equals(object)) {
            return "BOOLEAN";
        }
        return "UNKNOWN";
    }
}

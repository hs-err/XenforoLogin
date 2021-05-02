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

package red.mohist.sodionauth.core.database.mappers;

import org.teasoft.honey.osql.core.BeeFactory;
import org.teasoft.honey.osql.core.HoneyConfig;
import red.mohist.sodionauth.core.database.Mapper;
import red.mohist.sodionauth.core.database.annotations.Ignore;
import red.mohist.sodionauth.core.database.annotations.limits.NotNull;
import red.mohist.sodionauth.core.database.annotations.limits.PrimaryKey;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.Helper;

import java.lang.reflect.Field;

public class H2Mapper extends Mapper {
    public H2Mapper() {
        HoneyConfig.getHoneyConfig().dbName = "H2";

        HoneyConfig.getHoneyConfig().setUrl("jdbc:h2:" + Config.database.h2.url);

        if (!Config.database.h2.username.equals("")) {
            HoneyConfig.getHoneyConfig().setUsername(Config.database.h2.username);
        }
        if (!Config.database.h2.password.equals("")) {
            HoneyConfig.getHoneyConfig().setPassword(Config.database.h2.password);
        }
        HoneyConfig.getHoneyConfig().setDriverName("org.h2.Driver");
        honeyFactory = BeeFactory.getHoneyFactory();
    }

    @Override
    public boolean isTableExist(String name) {
        return honeyFactory.getBeeSql().select(
                "SELECT table_name FROM information_schema.tables WHERE table_schema = 'PUBLIC' AND table_name = '" + name.toUpperCase() + "';"
        ).size() == 1;
    }

    @Override
    public boolean isFieldExist(String tableName, String fieldName) {
        return honeyFactory.getBeeSql().select(
                "SELECT table_name FROM information_schema.columns WHERE " +
                        "table_schema = 'PUBLIC' AND " +
                        "table_name = '" + tableName.toUpperCase() + "' AND " +
                        "column_name = '" + fieldName.toUpperCase() + "';"
        ).size() == 1;
    }

    @Override
    public void dropTable(String name) {
        honeyFactory.getBeeSql().modify("DROP TABLE " + name + ";");
    }

    @Override
    public void createByEntity(Class<?> entity) {
        String tableName = translateTable(entity);
        StringBuilder sql = new StringBuilder("CREATE TABLE " + tableName + " (");
        Field[] fields = entity.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (i != 0) {
                sql.append(",");
            }
            if (field.isAnnotationPresent(Ignore.class)) {
                break;
            }

            sql.append(Mapper.translateField(field));

            sql.append(" ").append(getTypeName(field.getType()));
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                sql.append(" PRIMARY KEY auto_increment");
            }
            if (field.isAnnotationPresent(NotNull.class)) {
                sql.append(" NOT NULL");
            }
        }
        sql.append(");");
        honeyFactory.getBeeSql().modify(sql.toString());
    }

    @Override
    public void addField(Field field) {
        String tableName = translateTable(field);
        StringBuilder sql = new StringBuilder("ALTER TABLE `" + tableName + "` ADD ");
        sql.append(Mapper.translateField(field));

        sql.append(" ").append(getTypeName(field.getType()));

        if (field.isAnnotationPresent(NotNull.class)) {
            sql.append(" NOT NULL");
        }
        sql.append(";");
        Helper.getLogger().info(sql.toString());
        honeyFactory.getBeeSql().modify(sql.toString());
    }

    @Override
    protected String getTypeName(Class<?> object) {
        if (String.class.equals(object)) {
            return "TEXT";
        } else if (Integer.class.equals(object)) {
            return "INTEGER";
        } else if (Boolean.class.equals(object)) {
            return "BOOLEAN";
        }
        return "UNKNOWN";
    }
}

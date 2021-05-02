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

public class PostgresqlMapper extends Mapper {
    public PostgresqlMapper() {
        HoneyConfig.getHoneyConfig().dbName = "PostgreSQL";
        HoneyConfig.getHoneyConfig().setUrl("jdbc:postgresql://" + Config.database.postgresql.host + "/" + Config.database.postgresql.database);
        HoneyConfig.getHoneyConfig().setUsername(Config.database.postgresql.username);
        HoneyConfig.getHoneyConfig().setPassword(Config.database.postgresql.password);
        HoneyConfig.getHoneyConfig().setDriverName("org.postgresql.Driver");
        honeyFactory = BeeFactory.getHoneyFactory();
    }

    @Override
    public boolean isTableExist(String name) {
        return honeyFactory.getBeeSql().select(
                "SELECT table_name FROM information_schema.tables WHERE table_catalog='" + Config.database.postgresql.database + "' AND table_name='" + name + "';"
        ).size() == 1;
    }

    @Override
    public boolean isFieldExist(String tableName, String fieldName) {
        return honeyFactory.getBeeSql().select(
                "SELECT table_name FROM information_schema.columns WHERE " +
                        "table_catalog='" + Config.database.postgresql.database + "' AND " +
                        "table_name='" + tableName + "' AND " +
                        "column_name='" + fieldName + "';"
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
            sql.append(getFieldSql(field, true));
        }
        sql.append(");");
        Helper.getLogger().info(sql.toString());
        honeyFactory.getBeeSql().modify(sql.toString());
    }

    @Override
    public void addField(Field field) {
        String tableName = translateTable(field);
        StringBuilder sql = new StringBuilder("ALTER TABLE " + tableName + " ADD");
        sql.append(getFieldSql(field, false));
        sql.append(";");
        Helper.getLogger().info(sql.toString());
        honeyFactory.getBeeSql().modify(sql.toString());
    }

    private String getFieldSql(Field field, boolean setPrimaryKey) {
        StringBuilder sql = new StringBuilder();
        sql.append(Mapper.translateField(field));

        if (field.getName().equals("id")) {
            sql.append(" serial");
        }else{
            sql.append(" ").append(getTypeName(field.getType()));
        }
        if (setPrimaryKey && field.isAnnotationPresent(PrimaryKey.class)) {
            sql.append(" PRIMARY KEY");
        }else{
            if (field.isAnnotationPresent(NotNull.class)) {
                sql.append(" NOT");
            }
            sql.append(" NULL");
        }
        return sql.toString();
    }

    @Override
    protected String getTypeName(Class<?> object) {
        if (String.class.equals(object)) {
            return "text";
        } else if (Integer.class.equals(object)) {
            return "integer";
        } else if (Boolean.class.equals(object)) {
            return "boolean";
        }
        return "text";
    }
}

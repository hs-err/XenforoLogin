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

package red.mohist.sodionauth.core.services;

import com.google.common.eventbus.Subscribe;
import org.teasoft.bee.osql.NameTranslate;
import org.teasoft.bee.osql.Suid;
import org.teasoft.bee.osql.SuidRich;
import org.teasoft.honey.osql.core.BeeFactory;
import org.teasoft.honey.osql.core.HoneyConfig;
import org.teasoft.honey.osql.core.HoneyFactory;
import org.teasoft.honey.osql.core.Logger;
import org.teasoft.honey.osql.name.UnderScoreAndCamelName;
import red.mohist.sodionauth.core.database.Mapper;
import red.mohist.sodionauth.core.database.mappers.H2Mapper;
import red.mohist.sodionauth.core.database.mappers.MysqlMapper;
import red.mohist.sodionauth.core.database.mappers.PostgresqlMapper;
import red.mohist.sodionauth.core.database.mappers.SqliteMapper;
import red.mohist.sodionauth.core.events.BootEvent;
import red.mohist.sodionauth.core.utils.Config;
import red.mohist.sodionauth.core.utils.Helper;

import java.io.IOException;
import java.lang.reflect.Field;

public class DatabaseService {
    public HoneyFactory honeyFactory;
    public Suid suid;
    public SuidRich suidRich;
    public Mapper mapper;
    public NameTranslate nameTranslate;

    public DatabaseService() {
        Service.database = this;
        Helper.getLogger().info("Initializing database service...");

        HoneyConfig.getHoneyConfig().loggerType = "log4j2";
        // HoneyConfig.getHoneyConfig().loggerType = "noLogging";

        honeyFactory = BeeFactory.getHoneyFactory();

        nameTranslate=new UnderScoreAndCamelName() {
            private final String prefix = Config.database.tablePrefix;

            @Override
            public String toTableName(String entityName) {
                return prefix + super.toTableName(entityName);
            }

            @Override
            public String toEntityName(String tableName) {
                return super.toEntityName(tableName.substring(prefix.length()));
            }
        };

        try {
            Field nameTranslateField = HoneyFactory.class.getDeclaredField("nameTranslate");
            nameTranslateField.setAccessible(true);
            nameTranslateField.set(honeyFactory,nameTranslate);
        }catch (Exception e){
            Helper.getLogger().info("nameTranslate init failed");
        }

        suid = BeeFactory.getHoneyFactory().getSuid();
        suidRich = BeeFactory.getHoneyFactory().getSuidRich();
        switch (Config.database.type) {
            case "sqlite":
                mapper = new SqliteMapper();
                break;
            case "mysql":
                mapper = new MysqlMapper();
                break;
            case "h2":
                mapper = new H2Mapper();
                break;
            case "postgresql":
                mapper = new PostgresqlMapper();
                break;
            default:
                mapper = null;
        }
    }
}

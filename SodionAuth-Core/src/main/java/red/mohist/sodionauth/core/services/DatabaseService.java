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

import com.eloli.sodioncore.orm.OrmService;
import com.eloli.sodioncore.orm.SodionEntity;
import org.hibernate.SessionFactory;
import red.mohist.sodionauth.core.SodionAuthCore;
import red.mohist.sodionauth.core.entities.AuthInfo;
import red.mohist.sodionauth.core.entities.AuthLastInfo;
import red.mohist.sodionauth.core.entities.AuthSession;
import red.mohist.sodionauth.core.entities.User;
import red.mohist.sodionauth.core.utils.Helper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseService {
    public final OrmService ormService;
    public final SessionFactory sessionFactory;

    public DatabaseService() {
        Service.database = this;
        Helper.getLogger().info("Initializing database service...");

        ormService = SodionAuthCore.instance.sodionCore.getOrmService();

        List<Class<? extends SodionEntity>> entities = new ArrayList<>();
        entities.add(AuthInfo.class);
        entities.add(AuthLastInfo.class);
        entities.add(AuthSession.class);
        entities.add(User.class);
        ormService.addEntities(entities);

        sessionFactory = ormService.sessionFactory;
    }
}

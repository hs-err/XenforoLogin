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

package red.mohist.sodionauth.core.repositories;

import org.hibernate.Session;
import red.mohist.sodionauth.core.entities.AuthInfo;
import red.mohist.sodionauth.core.entities.User;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class AuthinfoRepository {
    private AuthinfoRepository() {
        //
    }

    public static List<AuthInfo> getByUser(Session session, User user) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<AuthInfo> criteriaQuery = builder.createQuery(AuthInfo.class);
        Root<AuthInfo> root = criteriaQuery.from(AuthInfo.class);
        criteriaQuery.where(builder.equal(root.get("userId"), user.getId()));
        return session.createQuery(criteriaQuery).getResultList();
    }
}

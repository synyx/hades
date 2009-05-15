/*
 * Copyright 2002-2008 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.synyx.hades.dao.query;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;


/**
 * Abstract base class to implement {@link HadesQuery}s. Simply looks up a JPA
 * {@link Query} through {@link #getQuery(EntityManager)} and executes it.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
abstract class AbstractHadesQuery implements HadesQuery {

    private FinderMethod method;


    /**
     * Creates a new {@link AbstractHadesQuery} from the given
     * {@link FinderMethod}.
     */
    public AbstractHadesQuery(FinderMethod method) {

        this.method = method;
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.synyx.hades.dao.orm.FinderMethod.HadesQuery#execute(javax.persistence
     * .EntityManager, java.lang.Object[])
     */
    public Object execute(Object... parameters) {

        Query query = getQuery(method.getEntityManager());
        query = method.prepareQuery(query, parameters);

        try {
            return method.isCollectionFinder() ? query.getResultList() : query
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }


    /**
     * Returns the actual JPA {@link Query} to be executed.
     * 
     * @param em
     * @return
     */
    protected abstract Query getQuery(EntityManager em);
}
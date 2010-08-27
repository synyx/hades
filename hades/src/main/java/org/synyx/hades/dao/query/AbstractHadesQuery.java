/*
 * Copyright 2008-2010 the original author or authors.
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

import static org.synyx.hades.dao.query.QueryExecution.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;


/**
 * Abstract base class to implement {@link HadesQuery}s. Simply looks up a JPA
 * {@link Query} through {@link #createQuery(EntityManager, Parameters)} and
 * executes it.
 * 
 * @author Oliver Gierke
 */
abstract class AbstractHadesQuery implements HadesQuery {

    private final QueryMethod method;
    private final EntityManager em;


    /**
     * Creates a new {@link AbstractHadesQuery} from the given
     * {@link QueryMethod}.
     */
    public AbstractHadesQuery(QueryMethod method, EntityManager em) {

        this.method = method;
        this.em = em;
    }


    /**
     * Creates a JPA {@link Query} with the given {@link ParameterBinder} from
     * the {@link HadesQuery}.
     * 
     * @param binder
     * @return
     */
    public Query createJpaQuery(ParameterBinder binder) {

        return createQuery(em, binder);
    }


    /**
     * Creates a JPA {@link Query} to count the instances of the
     * {@link HadesQuery} to be returned.
     * 
     * @param binder
     * @return
     */
    public Query createCountQuery() {

        return createCountQuery(em);
    }


    /**
     * Executes the {@link javax.persistence.Query} backing the
     * {@link QueryMethod} with the given parameters.
     * 
     * @param em
     * @param parameters
     * @return
     */
    public Object execute(Object... parameters) {

        ParameterBinder binder =
                new ParameterBinder(method.getParameters(), parameters);

        if (method.isCollectionQuery()) {
            return COLLECTION.execute(this, binder);
        }

        if (method.isPageQuery()) {
            return PAGE.execute(this, binder);
        }

        if (method.isModifyingQuery()) {
            return MODIFY.execute(this, binder);
        }

        return SINGLE_ENTITY.execute(this, binder);
    }


    /**
     * Returns the actual JPA {@link Query} to be executed. Has to return a
     * fresh instance on each call.
     * 
     * @param em
     * @param binder
     * @return
     */
    protected abstract Query createQuery(EntityManager em,
            ParameterBinder binder);


    /**
     * Returns the projecting count JPA {@link Query} to be executed. Has to
     * return a fresh instance on each call.
     * 
     * @param em
     * @return
     */
    protected abstract Query createCountQuery(EntityManager em);
}
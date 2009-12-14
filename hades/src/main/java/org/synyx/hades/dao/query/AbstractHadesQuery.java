/*
 * Copyright 2008-2009 the original author or authors.
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
import javax.persistence.Query;


/**
 * Abstract base class to implement {@link HadesQuery}s. Simply looks up a JPA
 * {@link Query} through {@link #createQuery(EntityManager, Parameters)} and
 * executes it.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
abstract class AbstractHadesQuery implements HadesQuery {

    private QueryMethod method;


    /**
     * Creates a new {@link AbstractHadesQuery} from the given
     * {@link QueryMethod}.
     */
    public AbstractHadesQuery(QueryMethod method) {

        this.method = method;
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.synyx.hades.dao.query.HadesQuery#createJpaQuery(org.synyx.hades.dao
     * .query.ParameterBinder)
     */
    public Query createJpaQuery(ParameterBinder binder) {

        return createQuery(method.getEntityManager(), binder);
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.synyx.hades.dao.query.HadesQuery#createCountQuery(org.synyx.hades
     * .dao.query.ParameterBinder)
     */
    public Query createCountQuery(ParameterBinder binder) {

        return createCountQuery(method.getEntityManager(), binder);
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
     * @param binder
     * @return
     */
    protected abstract Query createCountQuery(EntityManager em,
            ParameterBinder binder);
}
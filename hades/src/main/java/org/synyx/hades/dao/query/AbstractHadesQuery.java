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

import org.synyx.hades.domain.PageImpl;


/**
 * Abstract base class to implement {@link HadesQuery}s. Simply looks up a JPA
 * {@link Query} through {@link #createQuery(EntityManager, Parameters)} and
 * executes it.
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
     * org.synyx.hades.dao.query.HadesQuery#execute(org.synyx.hades.dao.query
     * .Parameters)
     */
    @SuppressWarnings("unchecked")
    public Object execute(Parameters parameters) {

        try {

            if (method.isPageFinder()) {

                // Execute query to compute total
                Query projection = parameters.bind(createQuery(parameters));
                int total = projection.getResultList().size();

                Query query =
                        parameters.bindAndPrepare(createQuery(parameters));

                return new PageImpl(query.getResultList(), parameters
                        .getPageable(), total);

            }

            if (method.isCollectionFinder()) {
                return parameters.bindAndPrepare(createQuery(parameters))
                        .getResultList();
            }

            return parameters.bind(createQuery(parameters)).getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }


    /**
     * Creates a new {@link Query} for the given {@link Parameters}.
     * 
     * @param parameters
     * @return
     */
    private Query createQuery(Parameters parameters) {

        return createQuery(method.getEntityManager(), parameters);
    }


    /**
     * Returns the actual JPA {@link Query} to be executed. Has to return a
     * fresh instance on each call.
     * 
     * @param em
     * @param parameters TODO
     * @return
     */
    protected abstract Query createQuery(EntityManager em, Parameters parameters);

}
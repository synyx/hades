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

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.synyx.hades.domain.PageImpl;


/**
 * Enum to contain query execution strategies. Depending (mostly) on the return
 * type of a {@link QueryMethod} a {@link HadesQuery} can be executed in various
 * flavours.
 * 
 * @author Oliver Gierke
 */
enum QueryExecution {

    /**
     * Executes the {@link HadesQuery} to return a simple collection of
     * entities.
     */
    COLLECTION {

        @Override
        protected Object doExecute(HadesQuery query, ParameterBinder binder) {

            return binder.bindAndPrepare(query.createJpaQuery(binder))
                    .getResultList();
        }
    },

    /**
     * Executes the {@link HadesQuery} to return a
     * {@link org.synyx.hades.domain.Page} of entities.
     */
    PAGE {

        @Override
        @SuppressWarnings("unchecked")
        protected Object doExecute(HadesQuery query, ParameterBinder binder) {

            // Execute query to compute total
            Query projection = binder.bind(query.createCountQuery(binder));
            Long total = (Long) projection.getSingleResult();

            Query jpaQuery =
                    binder.bindAndPrepare(query.createJpaQuery(binder));

            return new PageImpl(jpaQuery.getResultList(), binder.getPageable(),
                    total);
        }
    },

    /**
     * Executes a {@link HadesQuery} to return a single entity.
     */
    SINGLE_ENTITY {

        @Override
        protected Object doExecute(HadesQuery query, ParameterBinder binder) {

            return binder.bind(query.createJpaQuery(binder)).getSingleResult();
        }
    },

    /**
     * Executes a modifying query such as an update or an insert.
     */
    MODIFY {

        @Override
        protected Object doExecute(HadesQuery query, ParameterBinder binder) {

            return binder.bind(query.createJpaQuery(binder)).executeUpdate();
        }
    };

    /**
     * Executes the given {@link HadesQuery} with the given {@link Parameters}.
     * 
     * @param query
     * @param binder
     * @return
     */
    public Object execute(HadesQuery query, ParameterBinder binder) {

        try {
            return doExecute(query, binder);
        } catch (NoResultException e) {
            return null;
        }
    }


    /**
     * Method to implement {@link HadesQuery} executions by single enum values.
     * 
     * @param query
     * @param binder
     * @return
     */
    protected abstract Object doExecute(HadesQuery query, ParameterBinder binder);
}
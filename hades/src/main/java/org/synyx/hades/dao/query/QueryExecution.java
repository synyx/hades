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

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.synyx.hades.domain.Page;
import org.synyx.hades.domain.PageImpl;


/**
 * Enum to contain query execution strategies. Depending (mostly) on the return
 * type of a {@link QueryMethod} a {@link HadesQuery} can be executed in
 * various flavours.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
enum QueryExecution {

    /**
     * Executes the {@link HadesQuery} to return a simple collection of
     * entities.
     */
    COLLECTION {

        @Override
        protected Object doExecute(HadesQuery query, Parameters parameters) {

            return parameters.bindAndPrepare(query.createJpaQuery(parameters))
                    .getResultList();
        }
    },

    /**
     * Executes the {@link HadesQuery} to return a {@link Page} of entities.
     */
    PAGE {

        @Override
        @SuppressWarnings("unchecked")
        protected Object doExecute(HadesQuery query, Parameters parameters) {

            // Execute query to compute total
            Query projection =
                    parameters.bind(query.createJpaQuery(parameters));
            int total = projection.getResultList().size();

            Query jpaQuery =
                    parameters.bindAndPrepare(query.createJpaQuery(parameters));

            return new PageImpl(jpaQuery.getResultList(), parameters
                    .getPageable(), total);
        }
    },

    /**
     * Executes a {@link HadesQuery} to return a single entity.
     */
    SINGLE_ENTITY {

        @Override
        protected Object doExecute(HadesQuery query, Parameters parameters) {

            return parameters.bind(query.createJpaQuery(parameters))
                    .getSingleResult();
        }
    },

    /**
     * Executes a modifying query such as an update or an insert.
     */
    MODIFY {

        @Override
        protected Object doExecute(HadesQuery query, Parameters parameters) {

            return parameters.bind(query.createJpaQuery(parameters))
                    .executeUpdate();
        }
    };

    /**
     * Executes the given {@link HadesQuery} with the given {@link Parameters}.
     * 
     * @param query
     * @param parameters
     * @return
     */
    public Object execute(HadesQuery query, Parameters parameters) {

        try {
            return doExecute(query, parameters);
        } catch (NoResultException e) {
            return null;
        }
    }


    /**
     * Method to implement {@link HadesQuery} executions by single enum values.
     * 
     * @param query
     * @param parameters
     * @return
     */
    protected abstract Object doExecute(HadesQuery query, Parameters parameters);
}
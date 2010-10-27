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

import java.lang.reflect.Method;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.util.Assert;
import org.synyx.hades.domain.PageImpl;


/**
 * Enum to contain query execution strategies. Depending (mostly) on the return
 * type of a {@link QueryMethod} a {@link HadesQuery} can be executed in various
 * flavours.
 * 
 * @author Oliver Gierke
 */
abstract class QueryExecution {

    /**
     * Executes the given {@link HadesQuery} with the given {@link Parameters}.
     * 
     * @param query
     * @param binder
     * @return
     */

    public Object execute(AbstractHadesQuery query, ParameterBinder binder) {

        Assert.notNull(query);
        Assert.notNull(binder);

        try {
            return doExecute(query, binder);
        } catch (NoResultException e) {
            return null;
        }
    }


    /**
     * Method to implement {@link AbstractHadesQuery} executions by single enum
     * values.
     * 
     * @param query
     * @param binder
     * @return
     */
    protected abstract Object doExecute(AbstractHadesQuery query,
            ParameterBinder binder);

    /**
     * Executes the {@link HadesQuery} to return a simple collection of
     * entities.
     */
    static class CollectionExecution extends QueryExecution {

        @Override
        protected Object doExecute(AbstractHadesQuery query,
                ParameterBinder binder) {

            return binder.bindAndPrepare(query.createJpaQuery(binder))
                    .getResultList();
        }
    }

    /**
     * Executes the {@link HadesQuery} to return a
     * {@link org.synyx.hades.domain.Page} of entities.
     */
    static class PagedExecution extends QueryExecution {

        @Override
        @SuppressWarnings("unchecked")
        protected Object doExecute(AbstractHadesQuery query,
                ParameterBinder binder) {

            // Execute query to compute total
            Query projection = binder.bind(query.createCountQuery());
            Long total = (Long) projection.getSingleResult();

            Query jpaQuery =
                    binder.bindAndPrepare(query.createJpaQuery(binder));

            return new PageImpl<Object>(jpaQuery.getResultList(),
                    binder.getPageable(), total);
        }
    }

    /**
     * Executes a {@link HadesQuery} to return a single entity.
     */
    static class SingleEntityExecution extends QueryExecution {

        @Override
        protected Object doExecute(AbstractHadesQuery query,
                ParameterBinder binder) {

            return binder.bind(query.createJpaQuery(binder)).getSingleResult();
        }
    }

    /**
     * Executes a modifying query such as an update, insert or delete.
     */
    static class ModifyingExecution extends QueryExecution {

        private final EntityManager em;


        /**
         * Creates an execution that automatically clears the given
         * {@link EntityManager} after execution if the given
         * {@link EntityManager} is not {@literal null}.
         * 
         * @param em
         */
        public ModifyingExecution(Method method, EntityManager em) {

            Class<?> type = method.getReturnType();

            boolean isVoid = void.class.equals(type) || Void.class.equals(type);
            boolean isInt =
                    int.class.equals(type) || Integer.class.equals(type);

            Assert.isTrue(isInt || isVoid,
                    "Modifying queries can only use void or int/Integer as return type!");

            this.em = em;
        }


        @Override
        protected Object doExecute(AbstractHadesQuery query,
                ParameterBinder binder) {

            int result =
                    binder.bind(query.createJpaQuery(binder)).executeUpdate();

            if (em != null) {
                em.clear();
            }

            return result;
        }
    }
}
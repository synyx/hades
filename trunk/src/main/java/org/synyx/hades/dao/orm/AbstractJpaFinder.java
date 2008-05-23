/*
 * Copyright 2002-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.synyx.hades.dao.orm;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.util.Assert;
import org.synyx.hades.dao.FinderExecuter;
import org.synyx.hades.domain.Persistable;


/**
 * Abstract base class for generic DAOs. Allows execution of so called "finders"
 * that are backed by JPA named queries.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 * @param <T> the type of entity to be handled
 * @param <PK> the type of the entity's identifier
 */
public abstract class AbstractJpaFinder<T extends Persistable<PK>, PK extends Serializable>
        implements InitializingBean, FinderExecuter<T> {

    private JpaTemplate jpaTemplate;
    private Class<T> domainClass;


    /**
     * Setter to inject {@code EntityManagerFactory}.
     * 
     * @param entityManagerFactory
     */
    @Required
    @PersistenceUnit
    public void setEntityManagerFactory(
            final EntityManagerFactory entityManagerFactory) {

        this.jpaTemplate = new JpaTemplate(entityManagerFactory);
    }


    /**
     * Returns the {@code JpaTemplate}.
     * 
     * @return the {@code JpaTemplate}
     */
    protected JpaTemplate getJpaTemplate() {

        return this.jpaTemplate;
    }


    /**
     * Returns the domain class to handle.
     * 
     * @return the domain class
     */
    protected Class<T> getDomainClass() {

        return domainClass;
    }


    /**
     * Sets the domain class to handle.
     * 
     * @param domainClass the domain class to set
     */
    @Required
    public void setDomainClass(final Class<T> domainClass) {

        this.domainClass = domainClass;
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.synyx.jpa.support.FinderExecuter#executeFinder(java.lang.String,
     *      java.lang.Object[])
     */
    @SuppressWarnings("unchecked")
    public List<T> executeFinder(final String methodName,
            final Object... queryArgs) {

        return getJpaTemplate().executeFind(new JpaCallback() {

            /*
             * (non-Javadoc)
             * 
             * @see org.springframework.orm.jpa.JpaCallback#doInJpa(javax.persistence.EntityManager)
             */
            public List<T> doInJpa(final EntityManager em)
                    throws PersistenceException {

                Query namedQuery = prepareQuery(em, methodName, queryArgs);

                return namedQuery.getResultList();
            }
        });
    }


    /**
     * Executes a named query for a single result.
     * 
     * @param methodName
     * @param queryArgs
     * @return a single result returned by the named query
     * @throws EntityNotFoundException if no entity was found
     * @throws NonUniqueResultException if more than one entity was found
     */
    @SuppressWarnings("unchecked")
    public T executeObjectFinder(final String methodName,
            final Object... queryArgs) {

        return (T) getJpaTemplate().execute(new JpaCallback() {

            /*
             * (non-Javadoc)
             * 
             * @see org.springframework.orm.jpa.JpaCallback#doInJpa(javax.persistence.EntityManager)
             */
            public T doInJpa(final EntityManager em)
                    throws PersistenceException {

                Query namedQuery = prepareQuery(em, methodName, queryArgs);

                return (T) namedQuery.getSingleResult();
            }
        });
    }


    /**
     * Prepares a named query by resolving it against entity mapping queries.
     * Queries have to be named as follows: T.methodName where methodName has to
     * start with find.
     * 
     * @param methodName
     * @param queryArgs
     * @return
     */
    private Query prepareQuery(final EntityManager em, final String methodName,
            final Object... queryArgs) {

        final String queryName = domainClass.getSimpleName() + "." + methodName;
        final Query namedQuery = em.createNamedQuery(queryName);

        if (queryArgs != null) {

            for (int i = 0; i < queryArgs.length; i++) {
                namedQuery.setParameter(i + 1, queryArgs[i]);
            }
        }

        return namedQuery;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {

        Assert.notNull(jpaTemplate, "JpaTemplate must not be null! Either "
                + "set an EntityManagerFactory or a JpaTemplate yourself!");
    }
}

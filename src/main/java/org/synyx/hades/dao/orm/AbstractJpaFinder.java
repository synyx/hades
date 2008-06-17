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
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.synyx.hades.dao.FinderExecuter;
import org.synyx.hades.dao.config.DaoConfigContext;
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

    private static final Log log = LogFactory.getLog(AbstractJpaFinder.class);

    private QueryLookupStrategy createFinderQueries = QueryLookupStrategy.CREATE_IF_NOT_FOUND;
    private String finderPrefix = DaoConfigContext.DEFAULT_FINDER_PREFIX;

    private JpaTemplate jpaTemplate;
    private Class<T> domainClass;


    /**
     * Setter to configure finder query lookup. If configured to {@code true}
     * (default) the queries will be constructed from the method name. If set to
     * {@code false} it will try to lookup a named query with the following
     * naming convention: {@code $DomainClass.$DaoMethodName}.
     * 
     * @param createFinderQueries the constructQueries to set
     */
    public void setCreateFinderQueries(QueryLookupStrategy createFinderQueries) {

        this.createFinderQueries = createFinderQueries;
    }


    /**
     * Sets the prefix for methods that should be treatened as finder. Defaults
     * to {@value #DEFAULT_FINDER_PREFIX}.
     * 
     * @param finderPrefix the finderPrefix to set
     */
    public void setFinderPrefix(String finderPrefix) {

        this.finderPrefix = finderPrefix;
    }


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
    public List<T> executeFinder(final Method method, final Object... queryArgs) {

        return getJpaTemplate().executeFind(new JpaCallback() {

            /*
             * (non-Javadoc)
             * 
             * @see org.springframework.orm.jpa.JpaCallback#doInJpa(javax.persistence.EntityManager)
             */
            public List<T> doInJpa(final EntityManager em)
                    throws PersistenceException {

                Query namedQuery = prepareQuery(method, em, queryArgs);

                return namedQuery.getResultList();
            }
        });
    }


    /**
     * Executes a named query for a single result.
     * 
     * @param method
     * @param queryArgs
     * @return a single result returned by the named query
     * @throws EntityNotFoundException if no entity was found
     * @throws NonUniqueResultException if more than one entity was found
     */
    @SuppressWarnings("unchecked")
    public T executeObjectFinder(final Method method, final Object... queryArgs) {

        return (T) getJpaTemplate().execute(new JpaCallback() {

            /*
             * (non-Javadoc)
             * 
             * @see org.springframework.orm.jpa.JpaCallback#doInJpa(javax.persistence.EntityManager)
             */
            public T doInJpa(final EntityManager em)
                    throws PersistenceException {

                Query namedQuery = prepareQuery(method, em, queryArgs);

                return (T) namedQuery.getSingleResult();
            }
        });
    }


    /**
     * Prepares a named query by resolving it against entity mapping queries.
     * Queries have to be named as follows: T.methodName where methodName has to
     * start with find.
     * 
     * @param method
     * @param em
     * @param queryArgs
     * @return
     */
    private Query prepareQuery(final Method method, final EntityManager em,
            final Object... queryArgs) {

        Query namedQuery = lookupQuery(method, em);

        if (queryArgs != null) {

            for (int i = 0; i < queryArgs.length; i++) {
                namedQuery.setParameter(i + 1, queryArgs[i]);
            }
        }

        return namedQuery;
    }


    /**
     * Looks up a query according to the configured lookup strategy.
     * 
     * @param method
     * @param em
     * @return
     */
    private Query lookupQuery(final Method method, final EntityManager em) {

        final String queryName = domainClass.getSimpleName() + "."
                + method.getName();

        switch (createFinderQueries) {

            case CREATE:
                return em.createQuery(constructQuery(method));

            case USE_NAMED_QUERY:

                if (log.isDebugEnabled()) {
                    log.debug("Looking up named query " + queryName);
                }

                return em.createNamedQuery(queryName);

            case CREATE_IF_NOT_FOUND:
            default:
                try {

                    if (log.isDebugEnabled()) {
                        log.debug("Looking up named query " + queryName);
                    }

                    return em.createNamedQuery(queryName);

                } catch (IllegalArgumentException e) {

                    if (log.isDebugEnabled()) {
                        log.debug("Failed to lookup named query " + queryName
                                + " triggering query creation...");
                    }

                    return em.createQuery(constructQuery(method));
                }
        }
    }


    /**
     * Returns the query string to retrieve all entities.
     * 
     * @return string to retrieve all entities
     */
    protected String getReadAllQuery() {

        return "from " + getDomainClass().getSimpleName() + " x";
    }


    /**
     * Constructs a query from the given method. The method has to start with
     * {@code #FINDER_PREFIX}.
     * 
     * @param method
     * @return the query string
     */
    private String constructQuery(Method method) {

        if (log.isDebugEnabled()) {
            log.debug("Creating query from method " + method.getName());
        }

        final String AND = "And";
        final String OR = "Or";

        String methodName = method.getName();
        int numberOfBlocks = 0;

        // Reject methods not starting with defined prefix
        if (!methodName.startsWith(finderPrefix)) {
            throw new IllegalArgumentException(
                    "Cannot construct query for non finder method! "
                            + "Make sure the method starts with '"
                            + finderPrefix + "'");
        }

        // Remove prefix
        methodName = methodName.substring(finderPrefix.length(), methodName
                .length());

        StringBuilder queryBuilder = new StringBuilder(getReadAllQuery()
                + " where ");

        // Split OR
        String[] orParts = StringUtils.delimitedListToStringArray(methodName,
                OR);

        for (String orPart : Arrays.asList(orParts)) {

            // Split AND
            String[] andParts = StringUtils.delimitedListToStringArray(orPart,
                    AND);

            StringBuilder andBuilder = new StringBuilder();

            for (String andPart : Arrays.asList(andParts)) {

                andBuilder.append("x.");
                andBuilder.append(StringUtils.uncapitalize(andPart));
                andBuilder.append(" = ?");
                andBuilder.append(" and ");

                numberOfBlocks++;
            }

            andBuilder.delete(andBuilder.length() - 5, andBuilder.length());

            queryBuilder.append(andBuilder);
            queryBuilder.append(" or ");
        }

        // Assert correct number of parameters
        if (numberOfBlocks != method.getParameterTypes().length) {
            throw new IllegalArgumentException(
                    "You have to provide method arguments for each query "
                            + "criteria to construct the query correctly!");
        }

        queryBuilder.delete(queryBuilder.length() - 4, queryBuilder.length());

        return queryBuilder.toString();
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

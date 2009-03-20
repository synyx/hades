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

package org.synyx.hades.dao.orm;

import static org.synyx.hades.dao.query.QueryUtils.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.synyx.hades.domain.Persistable;


/**
 * Abstract base class for generic DAOs.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 * @param <T> the type of entity to be handled
 * @param <PK> the type of the entity's identifier
 */
public abstract class GenericDaoSupport<T extends Persistable<?>> implements
        InitializingBean {

    public static final String DEFAULT_FINDER_PREFIX = "findBy";

    private EntityManager entityManager = null;
    private Class<T> domainClass = null;


    /**
     * Returns the {@link EntityManager}.
     * 
     * @return
     */
    protected EntityManager getEntityManager() {

        return this.entityManager;
    }


    /**
     * Setter to inject {@code EntityManager}.
     * 
     * @param entityManager
     */
    @PersistenceContext
    public void setEntityManager(final EntityManager entityManager) {

        this.entityManager = entityManager;
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


    /**
     * Returns the query string to retrieve all entities.
     * 
     * @return string to retrieve all entities
     */
    protected String getReadAllQueryString() {

        return getQueryString(READ_ALL_QUERY, getDomainClass());
    }


    /**
     * Returns the query string to delete all entities.
     * 
     * @return string to delete all entities
     */
    protected String getDeleteAllQueryString() {

        return getQueryString(DELETE_ALL_QUERY_STRING, getDomainClass());
    }


    /**
     * Returns the query string to count entities.
     * 
     * @return string to count entities
     */
    protected String getCountQueryString() {

        return getQueryString(COUNT_QUERY_STRING, getDomainClass());
    }


    /**
     * Returns the query to retrieve all entities.
     * 
     * @return the query to retrieve all entities.
     */
    protected Query getReadAllQuery() {

        return getEntityManager().createQuery(getReadAllQueryString());
    }


    /**
     * Asserts that the {@code EntityManager} implementation being used by the
     * dao is an instance of the given type.
     * 
     * @param clazz
     * @throws IllegalArgumentException if the entity manager is not of the
     *             given type
     */
    protected void assertEntityManagerClass(Class<? extends EntityManager> clazz) {

        Assert.isInstanceOf(clazz, entityManager, String.format(
                "%s can only be used with %s implementation! "
                        + "Please check configuration or use %s instead!",
                getClass().getSimpleName(), clazz.getSimpleName(),
                GenericJpaDao.class.getSimpleName()));
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {

        Assert.notNull(entityManager, "EntityManager must not be null!");
    }
}

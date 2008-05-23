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
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.springframework.orm.jpa.JpaCallback;
import org.springframework.stereotype.Repository;
import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.domain.Persistable;
import org.synyx.hades.domain.page.Page;
import org.synyx.hades.domain.page.PageImpl;
import org.synyx.hades.domain.page.Pageable;


/**
 * Default implementation of the <code>GenericDao</code> interface. Use
 * <code>GenericDaoFactoryBean</code> to create instances of it. Furthermore
 * it is able to execute named queries.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 * @author Eberhard Wolff
 * @param <T> the type of the entity to handle
 * @param <PK> the type of the entity's identifier
 */
@Repository
public class GenericJpaDao<T extends Persistable<PK>, PK extends Serializable>
        extends AbstractJpaFinder<T, PK> implements GenericDao<T, PK> {

    /*
     * (non-Javadoc)
     * 
     * @see com.synyx.jpa.support.GenericDao#delete(java.lang.Object)
     */
    public void delete(final T entity) {

        getJpaTemplate().remove(entity);
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.synyx.jpa.support.GenericDao#readByPrimaryKey(java.io.Serializable)
     */
    public T readByPrimaryKey(final PK primaryKey) {

        if (null == primaryKey) {
            throw new IllegalArgumentException("primaryKey must not be null!");
        }

        return getJpaTemplate().find(getDomainClass(), primaryKey);
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.synyx.jpa.support.GenericDao#readAll()
     */
    @SuppressWarnings("unchecked")
    public List<T> readAll() {

        return getJpaTemplate()
                .find("from " + getDomainClass().getSimpleName());
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.hades.dao.GenericDao#readAll(org.synyx.hades.hades.dao.Pageable)
     */
    @SuppressWarnings("unchecked")
    public Page<T> readAll(final Pageable pageable) {

        return (Page) getJpaTemplate().execute(new JpaCallback() {

            /*
             * (non-Javadoc)
             * 
             * @see org.springframework.orm.jpa.JpaCallback#doInJpa(javax.persistence.EntityManager)
             */
            public Page doInJpa(final EntityManager em)
                    throws PersistenceException {

                Query query = em.createQuery("from "
                        + getDomainClass().getSimpleName());

                // Apply pagination
                if (null != pageable) {
                    query.setFirstResult(pageable.getPage()
                            * pageable.getNumberOfItems());
                    query.setMaxResults(pageable.getNumberOfItems());
                }

                return new PageImpl(query.getResultList(), pageable.getPage(),
                        pageable.getNumberOfItems(), count());
            }
        });
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.hades.jpa.support.GenericDao#count()
     */
    public Long count() {

        return (Long) getJpaTemplate().execute(new JpaCallback() {

            /*
             * (non-Javadoc)
             * 
             * @see org.springframework.orm.jpa.JpaCallback#doInJpa(javax.persistence.EntityManager)
             */
            public Object doInJpa(final EntityManager em)
                    throws PersistenceException {

                return em.createQuery(
                        "SELECT count(x) FROM "
                                + getDomainClass().getSimpleName() + " x")
                        .getSingleResult();
            }
        });
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.synyx.jpa.support.GenericDao#save(java.lang.Object)
     */
    public T save(final T entity) {

        if (entity.isNew()) {
            getJpaTemplate().persist(entity);
        } else {
            getJpaTemplate().merge(entity);
        }

        return entity;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.hades.jpa.support.GenericDao#saveAndFlush(org.synyx.hades.hades.jpa.support.Entity)
     */
    public T saveAndFlush(final T entity) {

        T result = save(entity);
        flush();

        return result;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.hades.jpa.support.GenericDao#flush()
     */
    public void flush() {

        getJpaTemplate().flush();
    }
}

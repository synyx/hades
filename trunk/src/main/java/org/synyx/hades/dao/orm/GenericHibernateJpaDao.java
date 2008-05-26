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

import org.hibernate.Criteria;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.hibernate.ejb.HibernateEntityManager;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.util.Assert;
import org.synyx.hades.dao.ExtendedGenericDao;
import org.synyx.hades.domain.Persistable;
import org.synyx.hades.domain.Sort;
import org.synyx.hades.domain.page.Page;
import org.synyx.hades.domain.page.PageImpl;
import org.synyx.hades.domain.page.Pageable;


/**
 * Implements extended generic dao functionality with Hibernate.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class GenericHibernateJpaDao<T extends Persistable<PK>, PK extends Serializable>
        extends GenericJpaDao<T, PK> implements ExtendedGenericDao<T, PK> {

    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.ExtendedGenericDao#readAll(org.synyx.hades.domain.page.Pageable,
     *      org.synyx.hades.domain.Sort)
     */
    @Override
    public Page<T> readAll(Pageable pageable, Sort sort) {

        if (null == sort) {
            return readAll(pageable);
        }

        return null;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.hades.dao.ExtendedGenericDao#readByExample(org.synyx.hades.hades.domain.Identifyable)
     */

    public List<T> readByExample(final T... examples) {

        return readByExample((Sort) null, examples);
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.ExtendedGenericDao#readByExample(org.synyx.hades.domain.Sort,
     *      T[])
     */
    @SuppressWarnings("unchecked")
    public List<T> readByExample(final Sort sort, final T... examples) {

        Criteria criteria = prepareCriteria(examples);

        if (null != sort) {
            applySorting(criteria, sort);
        }

        return criteria.list();
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.hades.dao.ExtendedGenericDao#readbyExample(java.awt.print.Pageable,
     *      T[])
     */
    @SuppressWarnings("unchecked")
    public Page<T> readByExample(final Pageable pageable, final T... examples) {

        return readByExample(pageable, null, examples);
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.ExtendedGenericDao#readByExample(org.synyx.hades.domain.page.Pageable,
     *      org.synyx.hades.domain.Sort, T[])
     */
    @SuppressWarnings("unchecked")
    public Page<T> readByExample(final Pageable pageable, final Sort sort,
            final T... examples) {

        Assert.notNull(pageable, "Pageable must not be null!");

        // Prevent null examples to cause trouble
        if (null == examples || examples.length == 0) {
            return readAll(pageable, sort);
        }

        Criteria criteria = prepareCriteria(examples);

        // Apply pagination
        if (null != pageable) {
            criteria.setFirstResult(pageable.getFirstItem());
            criteria.setMaxResults(pageable.getNumberOfItems());
        }

        // Apply sorting
        if (null != sort) {
            applySorting(criteria, sort);
        }

        return new PageImpl(criteria.list(), pageable, count());
    }


    /**
     * Prepares a {@code Criteria} for the given examples.
     * 
     * @param examples
     * @return
     */
    private Criteria prepareCriteria(final T... examples) {

        // Create criteria from hibernate entity manager
        return (Criteria) getJpaTemplate().execute(new HibernateJpaCallback() {

            /*
             * (non-Javadoc)
             * 
             * @see org.synyx.hades.dao.orm.GenericHibernateJpaDao.HibernateJpaCallback#doInHibernateJpa(org.hibernate.ejb.HibernateEntityManager)
             */
            @Override
            public Criteria doInHibernateJpa(final HibernateEntityManager em)
                    throws PersistenceException {

                Criteria criteria = em.getSession().createCriteria(
                        getDomainClass());

                // Add examples
                for (T example : examples) {

                    Example criteriaExample = Example.create(example);
                    criteria.add(criteriaExample);
                }

                return criteria;
            }
        }, true);
    }


    /**
     * Checks, that a {@code HibernateEntityManager} was set and rejects
     * configuration otherwise.
     * 
     * @see org.synyx.hades.dao.orm.AbstractJpaFinder#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        super.afterPropertiesSet();

        EntityManager em = (EntityManager) getJpaTemplate().execute(
                new JpaCallback() {

                    /*
                     * (non-Javadoc)
                     * 
                     * @see org.springframework.orm.jpa.JpaCallback#doInJpa(javax.persistence.EntityManager)
                     */
                    public EntityManager doInJpa(final EntityManager em)
                            throws PersistenceException {

                        return em;
                    }
                }, true);

        Assert.isInstanceOf(HibernateEntityManager.class, em, getClass()
                .getSimpleName()
                + " can only be used with Hibernate EntityManager "
                + "implementation! Please check configuration or use "
                + GenericJpaDao.class.getSimpleName() + " instead!");
    }


    /**
     * Applies sorting options to the given criteria.
     * 
     * @param criteria
     * @param sort
     */
    private void applySorting(Criteria criteria, Sort sort) {

        for (String property : sort.getProperties()) {

            Order order = (sort.isAscending() ? Order.asc(property) : Order
                    .desc(property));

            criteria.addOrder(order);
        }
    }

    /**
     * Simple implementation of {@code HibernateJpaCallback} that exposes a
     * {@code HibernateEntityManager} to a template method.
     * 
     * @author Oliver Gierke - gierke@synyx.de
     */
    private abstract class HibernateJpaCallback implements JpaCallback {

        /*
         * (non-Javadoc)
         * 
         * @see org.springframework.orm.jpa.JpaCallback#doInJpa(javax.persistence.EntityManager)
         */
        public Object doInJpa(EntityManager em) throws PersistenceException {

            HibernateEntityManager hibernateEntityManager = (HibernateEntityManager) em;

            return doInHibernateJpa(hibernateEntityManager);
        }


        /**
         * Execute a JPA operation with propietary Hibernate means.
         * 
         * @param em
         * @return
         */
        protected abstract Object doInHibernateJpa(HibernateEntityManager em);
    }
}

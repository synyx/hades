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

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.ejb.HibernateEntityManager;
import org.synyx.hades.dao.ExtendedGenericDao;
import org.synyx.hades.domain.Page;
import org.synyx.hades.domain.Pageable;
import org.synyx.hades.domain.Persistable;
import org.synyx.hades.domain.Sort;
import org.synyx.hades.domain.support.PageImpl;


/**
 * Implements extended {@link ExtendedGenericDao} functionality with Hibernate.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class GenericHibernateJpaDao<T extends Persistable<PK>, PK extends Serializable>
        extends GenericJpaDao<T, PK> implements ExtendedGenericDao<T, PK> {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.synyx.hades.hades.dao.ExtendedGenericDao#readByExample(org.synyx.
     * hades.hades.domain.Identifyable)
     */
    public List<T> readByExample(final T... examples) {

        return readByExample((Sort) null, examples);
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.synyx.hades.dao.ExtendedGenericDao#readByExample(org.synyx.hades.
     * domain.Sort, T[])
     */
    @SuppressWarnings("unchecked")
    public List<T> readByExample(final Sort sort, final T... examples) {

        Criteria criteria = applyExamples(examples);
        applySorting(criteria, sort);

        return criteria.list();
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.synyx.hades.hades.dao.ExtendedGenericDao#readbyExample(java.awt.print
     * .Pageable, T[])
     */
    @SuppressWarnings("unchecked")
    public Page<T> readByExample(final Pageable pageable, final T... examples) {

        // Prevent null examples to cause trouble
        if (null == examples || examples.length == 0) {
            return readAll(pageable);
        }

        Criteria countCriteria = applyExamples(examples);
        countCriteria.setProjection(Projections.rowCount());
        Integer count = (Integer) countCriteria.uniqueResult();

        Criteria listCriteria = applyExamples(examples);

        // Apply pagination
        applyPagination(listCriteria, pageable);

        return new PageImpl(listCriteria.list(), pageable, count);
    }


    /**
     * Prepares a {@code Criteria} for the given examples.
     * 
     * @param examples
     * @return
     */
    private Criteria applyExamples(final T... examples) {

        Criteria criteria =
                getEntityManager().getSession()
                        .createCriteria(getDomainClass());

        // Add examples
        for (T example : examples) {

            Example criteriaExample = Example.create(example);
            criteria.add(criteriaExample);
        }

        return criteria;
    }


    /**
     * Applies sorting options to the given criteria.
     * 
     * @param criteria
     * @param sort
     */
    private void applySorting(Criteria criteria, Sort sort) {

        if (null == sort) {
            return;
        }

        for (String property : sort.getProperties()) {

            Order order =
                    (sort.isAscending() ? Order.asc(property) : Order
                            .desc(property));

            criteria.addOrder(order);
        }
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.orm.AbstractJpaFinder#getEntityManager()
     */
    @Override
    protected HibernateEntityManager getEntityManager() {

        return (HibernateEntityManager) super.getEntityManager();
    }


    /**
     * Applies the given {@link Pageable} to the given {@link Criteria}.
     * 
     * @param criteria
     * @param pageable
     */
    private void applyPagination(Criteria criteria, Pageable pageable) {

        if (null == pageable) {
            return;
        }

        criteria.setFirstResult(pageable.getFirstItem());
        criteria.setMaxResults(pageable.getNumberOfItems());
        applySorting(criteria, pageable.getSort());
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

        assertEntityManagerClass(HibernateEntityManager.class);
    }
}

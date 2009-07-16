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
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.ejb.HibernateEntityManager;
import org.synyx.hades.dao.ExtendedGenericDao;
import org.synyx.hades.domain.Page;
import org.synyx.hades.domain.PageImpl;
import org.synyx.hades.domain.Pageable;
import org.synyx.hades.domain.Persistable;
import org.synyx.hades.domain.Sort;


/**
 * Implements extended {@link ExtendedGenericDao} functionality with Hibernate.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class GenericHibernateJpaDao<T extends Persistable<PK>, PK extends Serializable>
        extends AbstractExtendedGenericJpaDao<T, PK> {

    /**
     * Factory method to create {@link GenericHibernateJpaDao} instances.
     * 
     * @param <T> the type of the entity to handle
     * @param <PK> the type of the entity's identifier
     * @param entityManager the {@link EntityManager} backing the DAO
     * @param domainClass the domain class to handle
     * @return
     */
    public static <T extends Persistable<PK>, PK extends Serializable> ExtendedGenericDao<T, PK> create(
            final EntityManager entityManager, final Class<T> domainClass) {

        GenericHibernateJpaDao<T, PK> dao = new GenericHibernateJpaDao<T, PK>();
        dao.setEntityManager(entityManager);
        dao.setDomainClass(domainClass);
        dao.validate();

        return dao;
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.synyx.hades.dao.ExtendedGenericDao#readByExample(java.util.Collection
     * )
     */
    public List<T> readByExample(final Collection<T> examples) {

        return readByExample((Sort) null, examples);
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.synyx.hades.dao.ExtendedGenericDao#readByExample(org.synyx.hades.
     * domain.Sort, java.util.Collection)
     */
    @SuppressWarnings("unchecked")
    public List<T> readByExample(final Sort sort, final Collection<T> examples) {

        Criteria criteria = applyExamples(examples);
        applySorting(criteria, sort);

        return criteria.list();
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.synyx.hades.dao.ExtendedGenericDao#readByExample(org.synyx.hades.
     * domain.Pageable, java.util.Collection)
     */
    @SuppressWarnings("unchecked")
    public Page<T> readByExample(final Pageable pageable,
            final Collection<T> examples) {

        // Prevent null examples to cause trouble
        if (null == examples || examples.isEmpty()) {
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


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.synyx.hades.dao.ExtendedGenericDao#deleteByExample(java.util.Collection
     * )
     */
    public void deleteByExample(final Collection<T> examples) {

        for (T entity : readByExample(examples)) {
            delete(entity);
        }
    }


    /**
     * Prepares a {@code Criteria} for the given examples.
     * 
     * @param examples
     * @return
     */
    private Criteria applyExamples(final Collection<T> examples) {

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
        criteria.setMaxResults(pageable.getPageSize());
        applySorting(criteria, pageable.getSort());
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.orm.GenericDaoSupport#validate()
     */
    @Override
    public void validate() {

        super.validate();
        assertEntityManagerClass(HibernateEntityManager.class);
    }
}

/*
 * Copyright 2008-2009 the original author or authors.
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
import java.util.Vector;

import javax.persistence.EntityManager;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.ReportQueryResult;
import org.synyx.hades.dao.ExtendedGenericDao;
import org.synyx.hades.domain.Order;
import org.synyx.hades.domain.Page;
import org.synyx.hades.domain.PageImpl;
import org.synyx.hades.domain.Pageable;
import org.synyx.hades.domain.Persistable;
import org.synyx.hades.domain.Sort;
import org.synyx.hades.domain.Sort.Property;


/**
 * EclipseLink based implementation of {@code ExtendedGenericDao}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class GenericEclipseLinkJpaDao<T extends Persistable<PK>, PK extends Serializable>
        extends AbstractExtendedGenericJpaDao<T, PK> {

    /**
     * Factory method to create {@link GenericEclipseLinkJpaDao} instances.
     * 
     * @param <T> the type of the entity to handle
     * @param <PK> the type of the entity's identifier
     * @param entityManager the {@link EntityManager} backing the DAO
     * @param domainClass the domain class to handle
     * @return
     */
    public static <T extends Persistable<PK>, PK extends Serializable> ExtendedGenericDao<T, PK> create(
            final EntityManager entityManager, final Class<T> domainClass) {

        GenericEclipseLinkJpaDao<T, PK> dao =
                new GenericEclipseLinkJpaDao<T, PK>();
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
    public List<T> readByExample(Collection<T> examples) {

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
    public List<T> readByExample(Sort sort, Collection<T> examples) {

        ReadAllQuery query = new ReadAllQuery();

        applyExamples(query, examples);
        applySorting(query, sort);

        return (List<T>) executeQuery(query);
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.synyx.hades.dao.ExtendedGenericDao#readByExample(org.synyx.hades.
     * domain.Pageable, java.util.Collection)
     */
    @SuppressWarnings("unchecked")
    public Page<T> readByExample(Pageable pageable, Collection<T> examples) {

        // Prevent null examples to cause trouble
        if (null == examples || examples.isEmpty()) {
            return readAll(pageable);
        }

        // Prepare query to count totals
        ReportQuery countQuery = new ReportQuery();
        applyExamples(countQuery, examples);
        countQuery.addCount();

        Vector<ReportQueryResult> totals = executeQuery(countQuery);
        Integer total = (Integer) totals.get(0).getByIndex(0);

        // Prepare page query
        ReadAllQuery query = new ReadAllQuery();
        applyExamples(query, examples);
        applyPagination(query, pageable);

        List<T> entities = (List<T>) executeQuery(query);

        return new PageImpl(entities, pageable, total);
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.synyx.hades.dao.ExtendedGenericDao#deleteByExample(java.util.Collection
     * )
     */
    public void deleteByExample(Collection<T> examples) {

        // For EclipseLink entities have to be merged manually before removing
        // them
        for (T entity : readByExample(examples)) {

            delete(getEntityManager().merge(entity));
        }
    }


    /**
     * Executes the given query and returns the execution result. Clients have
     * to cast to the expected return type.
     * 
     * @param query
     * @return
     */
    @SuppressWarnings("unchecked")
    private <S> S executeQuery(final DatabaseQuery query) {

        return (S) getEntityManager().getServerSession().executeQuery(query);
    }


    /**
     * Applies sorting to the given query if the {@code Sort} instance is not
     * {@code null}.
     * 
     * @param query
     * @param sort
     */
    private void applySorting(ReadAllQuery query, Sort sort) {

        if (null == sort) {
            return;
        }

        for (Property property : sort) {

            Expression expression =
                    new ExpressionBuilder().get(property.getName());

            if (Order.ASCENDING.equals(property.getOrder())) {
                expression.ascending();
            } else {
                expression.descending();
            }

            query.addOrdering(expression);
        }
    }


    /**
     * Applies examples as criterias for the given query if examples is not
     * {@code null}.
     * 
     * @param query
     * @param examples
     */
    private void applyExamples(ReadAllQuery query, Collection<T> examples) {

        if (null == examples) {
            return;
        }

        for (T example : examples) {
            query.setExampleObject(example);
        }
    }


    /**
     * Applies pagination to the given query if the given {@code Pageable} is
     * not {@code null}.
     * 
     * @param query
     * @param pageable
     */
    private void applyPagination(ReadAllQuery query, Pageable pageable) {

        if (null == pageable) {
            return;
        }

        query.setFirstResult(pageable.getFirstItem());

        // Workaround:
        // setMaxRows does NOT count regard setFirstResult as start to add the
        // given value to, but counts from the very beginning. Thus we have to
        // add the offset manually
        query.setMaxRows(pageable.getFirstItem() + pageable.getPageSize());

        applySorting(query, pageable.getSort());
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.orm.AbstractJpaFinder#getEntityManager()
     */
    @Override
    protected JpaEntityManager getEntityManager() {

        return (JpaEntityManager) super.getEntityManager();
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.orm.GenericDaoSupport#validate()
     */
    @Override
    public void validate() {

        super.validate();
        assertEntityManagerClass(JpaEntityManager.class);
    }
}

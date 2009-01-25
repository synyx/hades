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
import java.util.Vector;

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
import org.synyx.hades.domain.Pageable;
import org.synyx.hades.domain.Persistable;
import org.synyx.hades.domain.Sort;
import org.synyx.hades.domain.support.PageImpl;


/**
 * EclipseLink based implementation of {@code ExtendedGenericDao}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class GenericEclipseLinkJpaDao<T extends Persistable<PK>, PK extends Serializable>
        extends GenericJpaDao<T, PK> implements ExtendedGenericDao<T, PK> {

    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.ExtendedGenericDao#readByExample(T[])
     */
    public List<T> readByExample(T... examples) {

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
    public List<T> readByExample(Sort sort, T... examples) {

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
     * domain.page.Pageable, T[])
     */
    @SuppressWarnings("unchecked")
    public Page<T> readByExample(Pageable pageable, T... examples) {

        // Prevent null examples to cause trouble
        if (null == examples || examples.length == 0) {
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

        for (String property : sort.getProperties()) {

            Expression expression = new ExpressionBuilder().get(property);

            if (Order.ASCENDING.equals(sort.getOrder())) {
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
    private void applyExamples(ReadAllQuery query, T... examples) {

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
        query.setMaxRows(pageable.getFirstItem() + pageable.getNumberOfItems());

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
     * @see org.synyx.hades.dao.orm.AbstractJpaFinder#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        super.afterPropertiesSet();

        assertEntityManagerClass(JpaEntityManager.class);
    }
}

package org.synyx.hades.dao.orm;

import java.io.Serializable;
import java.util.List;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.springframework.util.Assert;
import org.synyx.hades.dao.ExtendedGenericDao;
import org.synyx.hades.domain.Order;
import org.synyx.hades.domain.Persistable;
import org.synyx.hades.domain.Sort;
import org.synyx.hades.domain.page.Page;
import org.synyx.hades.domain.page.PageImpl;
import org.synyx.hades.domain.page.Pageable;


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
    public Page<T> readByExample(Pageable pageable, T... examples) {

        return readByExample(pageable, null, examples);
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.synyx.hades.dao.ExtendedGenericDao#readByExample(org.synyx.hades.
     * domain.page.Pageable, org.synyx.hades.domain.Sort, T[])
     */
    @SuppressWarnings("unchecked")
    public Page<T> readByExample(Pageable pageable, Sort sort, T... examples) {

        Assert.notNull(pageable, "Pageable must not be null!");

        // Prevent null examples to cause trouble
        if (null == examples || examples.length == 0) {
            return readAll(pageable, sort);
        }

        // Prepare query to count totals
        ReportQuery countQuery = new ReportQuery();
        applyExamples(countQuery, examples);
        Long total = (Long) executeQuery(countQuery);

        // Prepare page query
        ReadAllQuery query = new ReadAllQuery();
        applySorting(query, sort);
        applyPagination(query, pageable);
        applyExamples(query, examples);

        List<T> entities = (List<T>) executeQuery(query);

        return new PageImpl(entities, pageable, total);
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


    /**
     * Executes the given query and returns the execution result. Clients have
     * to cast to the expected return type.
     * 
     * @param query
     * @return
     */
    private Object executeQuery(final DatabaseQuery query) {

        return getJpaTemplate().execute(
                new NativeEntityManagerJpaCallback<JpaEntityManager>() {

                    /*
                     * (non-Javadoc)
                     * 
                     * @seeorg.synyx.hades.dao.orm.EntityManagerJpaCallback#
                     * doInConcreteEntityManager
                     * (javax.persistence.EntityManager)
                     */
                    @Override
                    protected Object doInNativeEntityManager(
                            JpaEntityManager em) {

                        return em.getSession().executeQuery(query);
                    }
                });
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
        query.setMaxRows(pageable.getNumberOfItems());
    }
}

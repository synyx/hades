/*
 * Copyright 2008-2010 the original author or authors.
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.dao.query.QueryUtils;
import org.synyx.hades.domain.Page;
import org.synyx.hades.domain.PageImpl;
import org.synyx.hades.domain.Pageable;
import org.synyx.hades.domain.Sort;
import org.synyx.hades.domain.Sort.Property;
import org.synyx.hades.domain.Specification;


/**
 * Default implementation of the {@link GenericDao} interface. This will offer
 * you a more sophisticated interface than the plain {@link EntityManager}.
 * 
 * @author Oliver Gierke
 * @author Eberhard Wolff
 * @param <T> the type of the entity to handle
 * @param <PK> the type of the entity's identifier
 */
@Repository
public class GenericJpaDao<T, PK extends Serializable> extends
        GenericDaoSupport<T> implements GenericDao<T, PK> {

    /**
     * Factory method to create {@link GenericJpaDao} instances.
     * 
     * @param <T> the type of the entity to handle
     * @param <PK> the type of the entity's identifier
     * @param entityManager the {@link EntityManager} backing the DAO
     * @param domainClass the domain class to handle
     * @return
     */
    public static <T, PK extends Serializable> GenericDao<T, PK> create(
            final EntityManager entityManager, final Class<T> domainClass) {

        GenericJpaDao<T, PK> dao = new GenericJpaDao<T, PK>();
        dao.setEntityManager(entityManager);
        dao.setDomainClass(domainClass);
        dao.validate();

        return dao;
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.synyx.jpa.support.GenericDao#delete(java.lang.Object)
     */
    public void delete(final T entity) {

        EntityManager em = getEntityManager();
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.GenericDao#delete(java.util.List)
     */
    public void delete(final Collection<? extends T> entities) {

        if (null == entities || entities.isEmpty()) {
            return;
        }

        QueryUtils.applyAndBind(getDeleteAllQueryString(), entities,
                getEntityManager()).executeUpdate();
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.GenericDao#deleteAll()
     */
    public void deleteAll() {

        getEntityManager().createQuery(getDeleteAllQueryString())
                .executeUpdate();
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * com.synyx.jpa.support.GenericDao#readByPrimaryKey(java.io.Serializable)
     */
    public T readByPrimaryKey(final PK primaryKey) {

        Assert.notNull(primaryKey, "The given primaryKey must not be null!");

        return getEntityManager().find(getDomainClass(), primaryKey);
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.GenericDao#exists(java.io.Serializable)
     */
    public boolean exists(final PK primaryKey) {

        Assert.notNull(primaryKey, "The given primary key must not be null!");

        return null != readByPrimaryKey(primaryKey);
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.synyx.jpa.support.GenericDao#readAll()
     */
    public List<T> readAll() {

        return getReadAllQuery().getResultList();
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.synyx.hades.dao.GenericDao#readAll(org.synyx.hades.domain.Specification
     * )
     */
    public List<T> readAll(Specification<T> spec) {

        return getQuery(spec, null).getResultList();
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.synyx.hades.dao.GenericDao#readAll(org.synyx.hades.domain.Specification
     * , org.synyx.hades.domain.Pageable)
     */
    public Page<T> readAll(Specification<T> spec, Pageable pageable) {

        if (spec == null) {
            return readAll(pageable);
        }

        TypedQuery<T> query = getQuery(spec, pageable);

        return pageable == null ? new PageImpl<T>(query.getResultList())
                : readPage(query, pageable, spec);
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.GenericDao#readAll(org.synyx.hades.domain.Sort)
     */
    public List<T> readAll(final Sort sort) {

        String queryString =
                QueryUtils.applySorting(getReadAllQueryString(), sort);
        TypedQuery<T> query =
                getEntityManager().createQuery(queryString, getDomainClass());

        return (null == sort) ? readAll() : query.getResultList();
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.synyx.hades.hades.dao.GenericDao#readAll(org.synyx.hades.hades.dao
     * .Pageable)
     */
    public Page<T> readAll(final Pageable pageable) {

        if (null == pageable) {

            return new PageImpl<T>(readAll());
        }

        return readPage(pageable, getReadAllQueryString());
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.hades.jpa.support.GenericDao#count()
     */
    public Long count() {

        return getEntityManager()
                .createQuery(getCountQueryString(), Long.class)
                .getSingleResult();
    }


    private Long count(Specification<T> spec) {

        return getCountQuery(spec).getSingleResult();
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.synyx.jpa.support.GenericDao#save(java.lang.Object)
     */
    public T save(final T entity) {

        if (getIsNewStrategy().isNew(entity)) {
            getEntityManager().persist(entity);
            return entity;
        } else {
            return getEntityManager().merge(entity);
        }
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.synyx.hades.hades.jpa.support.GenericDao#saveAndFlush(org.synyx.hades
     * .hades.jpa.support.Entity)
     */
    public T saveAndFlush(final T entity) {

        T result = save(entity);
        flush();

        return result;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.GenericDao#saveAll(java.util.List)
     */
    public List<T> save(Collection<? extends T> entities) {

        List<T> result = new ArrayList<T>();

        if (entities == null) {
            return result;
        }

        for (T entity : entities) {
            result.add(save(entity));
        }

        return result;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.hades.jpa.support.GenericDao#flush()
     */
    public void flush() {

        getEntityManager().flush();
    }


    /**
     * Reads a page of entities for the given JPQL query.
     * 
     * @param pageable
     * @param query
     * @return a page of entities for the given JPQL query
     */
    protected Page<T> readPage(final Pageable pageable, final String query) {

        String queryString = QueryUtils.applySorting(query, pageable.getSort());
        TypedQuery<T> jpaQuery =
                getEntityManager().createQuery(queryString, getDomainClass());

        return readPage(jpaQuery, pageable, null);
    }


    /**
     * @param query
     * @param spec
     * @param pageable
     * @return
     */
    private Page<T> readPage(final TypedQuery<T> query,
            final Pageable pageable, final Specification<T> spec) {

        query.setFirstResult(pageable.getFirstItem());
        query.setMaxResults(pageable.getPageSize());

        return new PageImpl<T>(query.getResultList(), pageable, count(spec));
    }


    /**
     * Creates a new {@link TypedQuery} from the given {@link Specification}.
     * 
     * @param spec can be {@literal null}
     * @param pageable can be {@literal null}
     * @return
     */
    private TypedQuery<T> getQuery(Specification<T> spec, Pageable pageable) {

        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(getDomainClass());

        Root<T> root = applySpecificationToCriteria(spec, query);
        query.select(root);

        if (pageable != null) {
            query.orderBy(toOrders(pageable.getSort(), root, builder));
        }

        return getEntityManager().createQuery(query);
    }


    /**
     * Creates a new count query for the given {@link Specification}.
     * 
     * @param spec can be {@literal null}.
     * @return
     */
    private TypedQuery<Long> getCountQuery(Specification<T> spec) {

        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);

        Root<T> root = applySpecificationToCriteria(spec, query);
        query.select(builder.count(root)).distinct(true);

        return getEntityManager().createQuery(query);
    }


    /**
     * Applies the given {@link Specification} to the given
     * {@link CriteriaQuery}.
     * 
     * @param spec can be {@literal null}
     * @param query
     * @return
     */
    private <S> Root<T> applySpecificationToCriteria(Specification<T> spec,
            CriteriaQuery<S> query) {

        Assert.notNull(query);
        Root<T> root = query.from(getDomainClass());

        if (spec == null) {
            return root;
        }

        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        Predicate predicate = spec.toPredicate(root, query, builder);

        if (predicate != null) {
            query.where(predicate);
        }

        return root;
    }


    private List<Order> toOrders(Sort sort, Root<T> root, CriteriaBuilder cb) {

        List<Order> orders = new ArrayList<Order>();

        if (sort == null) {
            return orders;
        }

        for (Property property : sort) {
            Expression<?> expression = root.get(property.getName());
            orders.add(property.isAscending() ? cb.asc(expression) : cb
                    .desc(expression));

        }

        return orders;
    }
}

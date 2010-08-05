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
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.dao.query.QueryUtils;
import org.synyx.hades.domain.Page;
import org.synyx.hades.domain.PageImpl;
import org.synyx.hades.domain.Pageable;
import org.synyx.hades.domain.Sort;
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
@Transactional
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

        getEntityManager().remove(entity);
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
    @Transactional(readOnly = true)
    public T readByPrimaryKey(final PK primaryKey) {

        Assert.notNull(primaryKey, "The given primaryKey must not be null!");

        return getEntityManager().find(getDomainClass(), primaryKey);
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.GenericDao#exists(java.io.Serializable)
     */
    @Transactional(readOnly = true)
    public boolean exists(final PK primaryKey) {

        Assert.notNull(primaryKey, "The given primary key must not be null!");

        return null != readByPrimaryKey(primaryKey);
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.synyx.jpa.support.GenericDao#readAll()
     */
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public List<T> readAll(Specification<T> spec) {

        return createQueryFrom(spec).getResultList();
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.synyx.hades.dao.GenericDao#readAll(org.synyx.hades.domain.Specification
     * , org.synyx.hades.domain.Pageable)
     */
    @Transactional(readOnly = true)
    public Page<T> readAll(Specification<T> spec, Pageable pageable) {

        if (spec == null) {
            return readAll(pageable);
        }

        TypedQuery<T> query = createQueryFrom(spec);

        return pageable == null ? new PageImpl<T>(query.getResultList())
                : readPage(query, pageable);
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.GenericDao#readAll(org.synyx.hades.domain.Sort)
     */
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public Long count() {

        return getEntityManager()
                .createQuery(getCountQueryString(), Long.class)
                .getSingleResult();
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

        return readPage(jpaQuery, pageable);
    }


    /**
     * @param query
     * @param pageable
     * @return
     */
    private Page<T> readPage(final TypedQuery<T> query, final Pageable pageable) {

        query.setFirstResult(pageable.getFirstItem());
        query.setMaxResults(pageable.getPageSize());

        return new PageImpl<T>(query.getResultList(), pageable, count());
    }


    /**
     * Creates a {@link TypedQuery} from the given {@link Specification}.
     * 
     * @param spec
     * @return
     */
    private TypedQuery<T> createQueryFrom(Specification<T> spec) {

        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(getDomainClass());
        Root<T> root = query.from(getDomainClass());

        Predicate predicate = spec.toPredicate(root, query, builder);

        if (predicate != null) {
            query.where(predicate);
        }

        return getEntityManager().createQuery(query);
    }
}

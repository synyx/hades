package org.synyx.hades.dao.orm;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.domain.Persistable;
import org.synyx.hades.domain.Pageable;


/**
 * Default implementation of the <code>GenericDao</code> interface. Use
 * <code>GenericDaoFactoryBean</code> to create instances of it. Furthermore
 * it is able to execute named queries.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 * @author Eberhard Wolff
 */
@Repository
public class GenericJpaDao<T extends Persistable<PK>, PK extends Serializable>
        extends AbstractJpaFinder<T, PK> implements GenericDao<T, PK> {

    /*
     * (non-Javadoc)
     * 
     * @see com.synyx.jpa.support.GenericDao#delete(java.lang.Object)
     */
    public void delete(T entity) {

        getEntityManager().remove(entity);
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.synyx.jpa.support.GenericDao#readByPrimaryKey(java.io.Serializable)
     */
    public T readByPrimaryKey(PK primaryKey) {

        if (null == primaryKey) {
            throw new IllegalArgumentException("primaryKey must not be null!");
        }

        return getEntityManager().find(getDomainClass(), primaryKey);
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.synyx.jpa.support.GenericDao#readAll()
     */
    @SuppressWarnings("unchecked")
    public List<T> readAll() {

        return readAll(null);
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.hades.dao.GenericDao#readAll(org.synyx.hades.hades.dao.Pageable)
     */
    @SuppressWarnings("unchecked")
    public List<T> readAll(Pageable pageable) {

        Query query = getEntityManager().createQuery(
                "SELECT x FROM " + getDomainClass().getSimpleName() + " x");

        // Apply pagination
        if (null != pageable) {
            query.setFirstResult(pageable.getFirstItem());
            query.setMaxResults(pageable.getNumberOfItems());
        }

        return query.getResultList();
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.hades.jpa.support.GenericDao#count()
     */
    public Long count() {

        return (Long) getEntityManager().createQuery(
                "SELECT count(x) FROM " + getDomainClass().getSimpleName()
                        + " x").getSingleResult();
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.synyx.jpa.support.GenericDao#save(java.lang.Object)
     */
    public T save(T entity) {

        if (entity.isNew()) {
            getEntityManager().persist(entity);
        } else {
            getEntityManager().merge(entity);
        }

        return entity;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.hades.jpa.support.GenericDao#saveAndFlush(org.synyx.hades.hades.jpa.support.Entity)
     */
    public T saveAndFlush(T entity) {

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

        getEntityManager().flush();
    }
}

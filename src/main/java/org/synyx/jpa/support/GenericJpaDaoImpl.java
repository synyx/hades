package org.synyx.jpa.support;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Repository;


/**
 * Default implementation of the <code>GenericDao</code> interface. Use
 * <code>GenericDaoFactoryBean</code> to create instances of it. Furthermore
 * it is able to execute named queries.
 * 
 * @author Eberhard Wolff
 * @author Oliver Gierke
 */
@Repository
public class GenericJpaDaoImpl<T extends Entity<PK>, PK extends Serializable>
        implements GenericDao<T, PK> {

    private EntityManager entityManager;
    private Class<T> type;


    /**
     * Setter to inject entity type class.
     * 
     * @param type
     */
    @Required
    public void setType(Class<T> type) {

        this.type = type;
    }


    /**
     * @return the entityManager
     */
    protected EntityManager getEntityManager() {

        return entityManager;
    }


    /**
     * Setter to inject <code>EntityManager</code>.
     * 
     * @param entityManager the entityManager to set
     */
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {

        this.entityManager = entityManager;
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.synyx.jpa.support.GenericDao#delete(java.lang.Object)
     */
    public void delete(T entity) {

        entityManager.remove(entity);
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

        return entityManager.find(type, primaryKey);
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.synyx.jpa.support.GenericDao#readAll()
     */
    @SuppressWarnings("unchecked")
    public List<T> readAll() {

        return entityManager.createQuery(
                "SELECT x FROM " + type.getSimpleName() + " x").getResultList();
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.synyx.jpa.support.GenericDao#save(java.lang.Object)
     */
    public T save(T entity) {

        if (null == entity.getId()) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }

        return entity;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.jpa.support.GenericDao#saveAndFlush(org.synyx.jpa.support.Entity)
     */
    public T saveAndFlush(T entity) {

        T result = save(entity);
        flush();

        return result;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.jpa.support.GenericDao#flush()
     */
    public void flush() {

        entityManager.flush();
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.synyx.jpa.support.FinderExecuter#executeFinder(java.lang.String,
     *      java.lang.Object[])
     */
    @SuppressWarnings("unchecked")
    public List<T> executeFinder(String methodName, Object... queryArgs) {

        final Query namedQuery = prepareQuery(methodName, queryArgs);

        return namedQuery.getResultList();
    }


    /**
     * Executes a named query for a single result.
     * 
     * @param methodName
     * @param queryArgs
     * @return
     * @throws EntityNotFoundException if no entity was found
     * @throws NonUniqueResultException if more than one entity was found
     */
    @SuppressWarnings("unchecked")
    protected T executeObjectFinder(String methodName, Object... queryArgs) {

        final Query namedQuery = prepareQuery(methodName, queryArgs);

        return (T) namedQuery.getSingleResult();
    }


    /**
     * Prepares a named query by resolving it against entity mapping queries.
     * Queries have to be named as follows: T.methodName where methodName has to
     * start with find.
     * 
     * @param methodName
     * @param queryArgs
     * @return
     */
    private Query prepareQuery(String methodName, Object... queryArgs) {

        final String queryName = type.getSimpleName() + "." + methodName;

        Query namedQuery = entityManager.createNamedQuery(queryName);

        if (queryArgs != null) {

            for (int i = 0; i < queryArgs.length; i++) {

                namedQuery.setParameter(i + 1, queryArgs[i]);
            }
        }

        return namedQuery;
    }
}

package org.synyx.hades.dao.orm;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Required;
import org.synyx.hades.dao.FinderExecuter;
import org.synyx.hades.domain.Persistable;


/**
 * Abstract base class for generic DAOs. Allows execution of so called "finders"
 * that are backed by JPA named queries.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public abstract class AbstractJpaFinder<T extends Persistable<PK>, PK extends Serializable>
        implements FinderExecuter<T> {

    private EntityManager entityManager;
    private Class<T> domainClass;


    /**
     * Returns the {@code EntityManager}.
     * 
     * @return the entityManager
     */
    protected EntityManager getEntityManager() {

        return entityManager;
    }


    /**
     * Setter to inject an {@code EntityManager}.
     * 
     * @param entityManager the entityManager to set
     */
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {

        this.entityManager = entityManager;
    }


    /**
     * Returns the domain class to handle.
     * 
     * @return the type
     */
    protected Class<T> getDomainClass() {

        return domainClass;
    }


    /**
     * Sets the domain class to handle.
     * 
     * @param domainClass the domain class to set
     */
    @Required
    public void setDomainClass(Class<T> domainClass) {

        this.domainClass = domainClass;
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.synyx.jpa.support.FinderExecuter#executeFinder(java.lang.String,
     *      java.lang.Object[])
     */
    @SuppressWarnings("unchecked")
    public List<T> executeFinder(String methodName, Object... queryArgs) {

        Query namedQuery = prepareQuery(methodName, queryArgs);

        return namedQuery.getResultList();
    }


    /**
     * Executes a named query for a single result.
     * 
     * @param methodName
     * @param queryArgs
     * @return a single result returned by the named query
     * @throws EntityNotFoundException if no entity was found
     * @throws NonUniqueResultException if more than one entity was found
     */
    @SuppressWarnings("unchecked")
    protected T executeObjectFinder(String methodName, Object... queryArgs) {

        Query namedQuery = prepareQuery(methodName, queryArgs);

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

        String queryName = domainClass.getSimpleName() + "." + methodName;
        Query namedQuery = entityManager.createNamedQuery(queryName);

        if (queryArgs != null) {

            for (int i = 0; i < queryArgs.length; i++) {

                namedQuery.setParameter(i + 1, queryArgs[i]);
            }
        }

        return namedQuery;
    }
}

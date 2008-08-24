package org.synyx.hades.dao.orm;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.springframework.orm.jpa.JpaCallback;


/**
 * Simple helper implementation of {@code JpaCallback} to provide access to a
 * native implementation of {@code EntityManager}. Clients using this helper
 * class have to ensure that they really use the declared custom implementation.
 * 
 * @param <T> the concrete {@code EntityManager} implementation
 * @author Oliver Gierke - gierke@synyx.de
 */
public abstract class NativeEntityManagerJpaCallback<T extends EntityManager>
        implements JpaCallback {

    /*
     * (non-Javadoc)
     * 
     * @seeorg.springframework.orm.jpa.JpaCallback#doInJpa(javax.persistence.
     * EntityManager)
     */
    @SuppressWarnings("unchecked")
    public Object doInJpa(EntityManager em) throws PersistenceException {

        return doInNativeEntityManager((T) em);
    }


    /**
     * Execute a JPA operation with a native {@code EntityManager}
     * implementation.
     * 
     * @param em
     * @return
     */
    protected abstract Object doInNativeEntityManager(T em);

}

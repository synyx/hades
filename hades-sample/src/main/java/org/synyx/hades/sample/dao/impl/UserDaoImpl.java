package org.synyx.hades.sample.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.synyx.hades.sample.dao.UserDaoCustom;
import org.synyx.hades.sample.domain.User;


/**
 * @author Oliver Gierke - gierke@synyx.de
 */
public class UserDaoImpl implements UserDaoCustom {

    @PersistenceContext
    private EntityManager em;


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.sample.dao.UserDaoCustom#myCustomBatchOperation()
     */
    @SuppressWarnings("unchecked")
    public List<User> myCustomBatchOperation() {

        return em.createQuery("from User u").getResultList();
    }

}

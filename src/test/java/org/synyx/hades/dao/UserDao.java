package org.synyx.hades.dao;

import java.util.List;

import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.domain.User;


/**
 * DAO Interface for <code>Person</code>s.
 * 
 * @author Eberhard Wolff
 * @author Oliver Gierke
 */
public interface UserDao extends GenericDao<User, Integer> {

    /**
     * Retrieve users by their email address.
     * 
     * @param emailAddress
     * @return
     */
    public List<User> findByEmailAddress(String emailAddress);
}

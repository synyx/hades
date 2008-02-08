package org.synyx.jpa.support.test.dao;

import java.util.List;

import org.synyx.jpa.support.GenericDao;
import org.synyx.jpa.support.test.domain.User;


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

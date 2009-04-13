package org.synyx.hades.sample.dao;

import java.util.List;

import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.sample.domain.User;


/**
 * Simple DAO interface for {@link User} instances. Provides basic CRUD
 * operations.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public interface SimpleUserDao extends GenericDao<User, Long> {

    /**
     * Find all users with the given lastname.
     * 
     * @param lastname
     * @return
     */
    List<User> findByUsername(String username);
}

package org.synyx.hades.sample.dao;

import java.util.List;

import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.sample.domain.User;


/**
 * DAO interface for {@link User} instances. Provides basic CRUD operations due
 * to the extension of {@link GenericDao}. Includes custom implented
 * functionality by extending {@link UserDaoCustom}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public interface UserDao extends GenericDao<User, Long>, UserDaoCustom {

    /**
     * Find all users with the given lastname.
     * 
     * @param lastname
     * @return
     */
    List<User> findByUsername(String username);
}

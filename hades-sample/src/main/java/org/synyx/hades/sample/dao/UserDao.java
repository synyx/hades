package org.synyx.hades.sample.dao;

import java.util.List;

import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.dao.Query;
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
     * Find the user with the given username. This method will be translated
     * into a query using the {@link javax.persistence.NamedQuery} annotation at
     * the {@link User} class.
     * 
     * @param lastname
     * @return
     */
    User findByTheUsersName(String username);


    /**
     * Find all users with the given lastname. This method will be translated
     * into a query by constructing it directly from the method name as there is
     * no other query declared.
     * 
     * @param lastname
     * @return
     */
    List<User> findByLastname(String lastname);


    /**
     * Returns all users with the given firstname. This method will be
     * translated into a query using the one declared in the {@link Query}
     * annotation declared one.
     * 
     * @param firstname
     * @return
     */
    @Query("from User u where u.firstname = ?")
    List<User> findByFirstname(String firstname);
}

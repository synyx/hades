package org.synyx.hades.sample.dao.simple;

import java.util.List;

import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.dao.Param;
import org.synyx.hades.dao.Query;
import org.synyx.hades.sample.domain.User;


/**
 * Simple DAO interface for {@link User} instances. The interface is used to
 * declare so called finder methods, methods to retrieve single entities or
 * collections of them.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public interface SimpleUserDao extends GenericDao<User, Long> {

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


    /**
     * Returns all users with the given name as first- or lastname. Makes use of
     * the {@link Param} annotation to use named parameters in queries. This
     * makes the query to method relation much more refactoring safe as the
     * order of the method parameters is completely irrelevant.
     * 
     * @param name
     * @return
     */
    @Query("from User u where u.firstname = :name or u.lastname = :name")
    List<User> findByFirstnameOrLastname(@Param("name") String name);
}

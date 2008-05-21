package org.synyx.hades.dao;

import java.util.List;

import org.synyx.hades.domain.User;


/**
 * DAO interface for {@code User}s. The two declared methods will trigger named
 * queries as they start with {@code find}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public interface UserDao extends GenericDao<User, Integer> {

    /**
     * Retrieve users by their lastname. The finder
     * {@literal User.findByLastname} is declared in {@literal META-INF/orm.xml}.
     * 
     * @param lastname
     * @return all users with the given lastname
     */
    List<User> findByLastname(final String lastname);


    /**
     * Retrieve users by their email address. The finder
     * {@literal User.findByEmailAddress} is declared as annotation at
     * {@code User}.
     * 
     * @param emailAddress
     * @return the user with the given email address
     */
    User findByEmailAddress(final String emailAddress);
}

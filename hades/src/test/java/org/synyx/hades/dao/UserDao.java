/*
 * Copyright 2008-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.synyx.hades.dao;

import java.util.List;

import javax.persistence.QueryHint;

import org.synyx.hades.domain.Page;
import org.synyx.hades.domain.Pageable;
import org.synyx.hades.domain.User;


/**
 * DAO interface for {@code User}s. The declared methods will trigger named
 * queries as they start with {@code findBy}.
 * 
 * @author Oliver Gierke
 */
public interface UserDao extends GenericDao<User, Integer>, UserDaoCustom {

    /**
     * Retrieve users by their lastname. The finder
     * {@literal User.findByLastname} is declared in {@literal META-INF/orm.xml}
     * .
     * 
     * @param lastname
     * @return all users with the given lastname
     */
    @QueryHints({ @QueryHint(name = "foo", value = "bar") })
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


    @Query("select u from User u ")
    Page<User> findAllPaged(final Pageable pageable);


    /**
     * Retrieves users by the given email and lastname. Acts as a dummy method
     * declaration to test finder query creation.
     * 
     * @param emailAddress
     * @param lastname
     * @return the user with the given email address and lastname
     */
    User findByEmailAddressAndLastname(final String emailAddress,
            final String lastname);


    /**
     * Retrieves users by email address and lastname or firstname. Acts as a
     * dummy method declaration to test finder query creation.
     * 
     * @param emailAddress
     * @param lastname
     * @param username
     * @return the users with the given email address and lastname or the given
     *         firstname
     */
    List<User> findByEmailAddressAndLastnameOrFirstname(
            final String emailAddress, final String lastname,
            final String username);


    /**
     * Retrieves a user by its username using the query annotated to the method.
     * 
     * @param username
     * @return
     */
    @Query("select u from User u where u.emailAddress = ?1")
    User findByHadesQuery(final String emailAddress);


    /**
     * Method to directly create query from and adding a {@link Pageable}
     * parameter to be regarded on query execution.
     * 
     * @param pageable
     * @param firstname
     * @return
     */
    Page<User> findByFirstname(Pageable pageable, String firstname);


    /**
     * Method to directly create query from and adding a {@link Pageable}
     * parameter to be regarded on query execution. Just returns the queried
     * {@link Page}'s contents.
     * 
     * @param firstname
     * @param pageable
     * @return
     */
    List<User> findByFirstname(String firstname, Pageable pageable);


    /**
     * Manipulating query to set all {@link User}'s names to the given one.
     * 
     * @param lastname
     */
    @Modifying
    @Query("update User u set u.lastname = ?")
    void renameAllUsersTo(String lastname);


    @Query("select count(u) from User u where u.firstname = ?")
    Long countWithFirstname(String firstname);


    /**
     * Method where parameters will be applied by name. Note that the order of
     * the parameters is then not crucial anymore.
     * 
     * @param firstname
     * @param lastname
     * @return
     */
    @Query("select u from User u where u.lastname = :lastname or u.firstname = :firstname")
    List<User> findByLastnameOrFirstname(@Param("firstname") String firstname,
            @Param("lastname") String lastname);


    /**
     * Method to check query creation and named parameter usage go well hand in
     * hand.
     * 
     * @param lastname
     * @param firstname
     * @return
     */
    List<User> findByFirstnameOrLastname(@Param("lastname") String lastname,
            @Param("firstname") String firstname);
}

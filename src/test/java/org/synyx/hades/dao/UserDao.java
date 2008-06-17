/*
 * Copyright 2002-2008 the original author or authors.
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

import org.synyx.hades.domain.User;


/**
 * DAO interface for {@code User}s. The declared methods will trigger named
 * queries as they start with {@code findBy}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public interface UserDao extends GenericDao<User, Integer>, UserDaoCustom {

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
}

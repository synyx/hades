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

package org.synyx.hades.dao.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.synyx.hades.core.QueryLookupStrategy;
import org.synyx.hades.dao.UserDao;
import org.synyx.hades.domain.User;


/**
 * Integration test for executing finders, thus testing various query lookup
 * strategies.
 * 
 * @see QueryLookupStrategy
 * @author Oliver Gierke - gierke@synyx.de
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:namespace-applicationContext.xml")
@Transactional
public class UserDaoFinderIntegrationTest {

    @Autowired
    private UserDao userDao;

    private User firstUser;
    private User secondUser;


    @Before
    public void setUp() {

        // This one matches both criterias
        firstUser = new User();
        firstUser.setEmailAddress("foo");
        firstUser.setLastname("bar");
        firstUser.setFirstname("foobar");

        userDao.save(firstUser);

        // This one matches only the second one
        secondUser = new User();
        secondUser.setEmailAddress("bar");
        secondUser.setLastname("foo");

        userDao.save(secondUser);
    }


    /**
     * Tests creation of a simple query.
     */
    @Test
    public void testSimpleCustomCreatedFinder() {

        User user = userDao.findByEmailAddressAndLastname("foo", "bar");

        Assert.assertEquals(firstUser, user);
    }


    /**
     * Tests creation of a simple query consisting of {@code AND} and {@code OR}
     * parts.
     */
    @Test
    public void testAndOrFinder() {

        List<User> users = userDao.findByEmailAddressAndLastnameOrFirstname(
                "bar", "foo", "foobar");

        Assert.assertNotNull(users);
        Assert.assertEquals(2, users.size());
        Assert.assertTrue(users.contains(firstUser));
        Assert.assertTrue(users.contains(secondUser));
    }
}

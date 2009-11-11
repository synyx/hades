/*
 * Copyright 2002-2008 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.synyx.hades.dao.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.synyx.hades.dao.ExtendedGenericDao;
import org.synyx.hades.dao.UserExtendedDao;
import org.synyx.hades.dao.orm.GenericHibernateJpaDao;
import org.synyx.hades.domain.Order;
import org.synyx.hades.domain.Page;
import org.synyx.hades.domain.PageRequest;
import org.synyx.hades.domain.Sort;
import org.synyx.hades.domain.User;
import org.synyx.hades.domain.Sort.Property;


/**
 * Integration test for {@code UserExtendedDao}. Actually this test is inteded
 * to test various implementations of {@link ExtendedGenericDao}. The default
 * configuration will use the {@link GenericHibernateJpaDao} as well as the
 * Hibernate setup.
 * <p>
 * To test other implementations, simply subclass this class and load another
 * config file that provides a bean named {@code jpaVendorAdaptor} as well as a
 * Hades configuration for {@link UserExtendedDao}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:namespace-applicationContext.xml" })
@Transactional
public class ExtendedGenericDaoIntegrationTest {

    private static final int NUMBER_OF_INSTANCES = 9;

    @Autowired
    private UserExtendedDao userExtendedDao;
    private List<User> referenceUsers;


    /**
     * Creates {@value #NUMBER_OF_INSTANCES} {@link User} instances.
     */
    @Before
    public void setUp() {

        referenceUsers = new ArrayList<User>();

        for (int i = 0; i < NUMBER_OF_INSTANCES; i++) {

            User user =
                    new User("Firstname" + i, "Lastname" + i % 2, "foo@bar.de"
                            + (NUMBER_OF_INSTANCES - i));
            referenceUsers.add(userExtendedDao.save(user));
        }

        assertEquals(NUMBER_OF_INSTANCES, userExtendedDao.count().longValue());
    }


    /**
     * Tests looking up a user by a given example.
     * 
     * @throws Exception
     */
    @Test
    public void findByCriteria() throws Exception {

        User user = new User();
        user.setFirstname("Firstname5");

        List<User> users = userExtendedDao.readByExample(user);

        assertNotNull(users);
        assertEquals(1, users.size());

        User reference = referenceUsers.get(5);
        assertEquals(reference, users.get(0));
    }


    /**
     * Tests, that the {@link ExtendedGenericDao} implementation applies sorting
     * correctly.
     * 
     * @throws Exception
     */
    @Test
    public void readAllSorted() throws Exception {

        List<User> users =
                userExtendedDao.readAll(new Sort(Order.ASCENDING,
                        "emailAddress"));

        assertNotNull(users);
        assertEquals(NUMBER_OF_INSTANCES, users.size());

        assertEquals(referenceUsers.get(NUMBER_OF_INSTANCES - 1), users.get(0));
    }


    /**
     * Tests, that the {@link ExtendedGenericDao} implementation applies sorting
     * with 1.1 API correctly.
     * 
     * @throws Exception
     */
    @Test
    public void readAllSorted11() throws Exception {

        List<Property> sortProperties = new ArrayList<Property>(2);
        sortProperties.add(new Sort.Property(Order.ASCENDING, "lastname"));
        sortProperties.add(new Sort.Property(Order.DESCENDING, "firstname"));

        List<User> users = userExtendedDao.readAll(new Sort(sortProperties));

        assertNotNull(users);
        assertEquals(NUMBER_OF_INSTANCES, users.size());

        assertEquals(referenceUsers.get(NUMBER_OF_INSTANCES - 1), users.get(0));
    }


    /**
     * Tests that the DAO sorts example queries correctly.
     * 
     * @throws Exception
     */
    @Test
    public void readSortedByExample() throws Exception {

        User sampleUser = new User();
        sampleUser.setLastname("Lastname0");

        List<User> users =
                userExtendedDao.readByExample(new Sort(Order.ASCENDING,
                        "emailAddress"), sampleUser);

        assertNotNull(users);
        assertEquals((NUMBER_OF_INSTANCES + 1) / 2, users.size());

        User reference = users.get(0);

        // Asser email address order
        for (User user : users) {

            assertTrue(0 >= reference.getEmailAddress().compareTo(
                    user.getEmailAddress()));
        }
    }


    /**
     * Tests that the DAO returns the correct page for a request containing
     * pagination, sorting and examples.
     * 
     * @throws Exception
     */
    @Test
    public void readSortedAndPagedByExample() throws Exception {

        User sampleUser = new User();
        sampleUser.setLastname("Lastname0");

        // Request second page with 2 instances
        PageRequest request =
                new PageRequest(1, 2, Order.ASCENDING, "emailAddress");

        Page<User> page = userExtendedDao.readByExample(request, sampleUser);

        assertNotNull(page);
        assertTrue(page.hasNextPage());
        assertTrue(page.hasPreviousPage());
        assertEquals(5, page.getTotalElements());
        assertEquals(2, page.getNumberOfElements());
    }


    /**
     * Tests deleting users by giving example objects.
     * 
     * @throws Exception
     */
    @Test
    public void deleteByExample() {

        User user = new User();
        user.setFirstname("Firstname5");

        userExtendedDao.deleteByExample(user);

        assertEquals(NUMBER_OF_INSTANCES - 1, userExtendedDao.count()
                .longValue());
    }
}

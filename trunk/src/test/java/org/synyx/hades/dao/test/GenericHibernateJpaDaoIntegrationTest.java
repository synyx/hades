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

import java.util.ArrayList;
import java.util.List;

import org.springframework.test.jpa.AbstractJpaTests;
import org.synyx.hades.dao.UserExtendedDao;
import org.synyx.hades.domain.Order;
import org.synyx.hades.domain.Sort;
import org.synyx.hades.domain.User;
import org.synyx.hades.domain.page.Page;
import org.synyx.hades.domain.page.PageRequest;


/**
 * Integration test for {@code GenericHibernateJpaDao}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class GenericHibernateJpaDaoIntegrationTest extends AbstractJpaTests {

    private static final int NUMBER_OF_INSTANCES = 9;

    private UserExtendedDao userExtendedDao;
    private List<User> referenceUsers;


    /**
     * @param extendedUserDao the userDao to set
     */
    public void setUserExtendedDao(UserExtendedDao extendedUserDao) {

        this.userExtendedDao = extendedUserDao;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.test.AbstractSingleSpringContextTests#getConfigLocations()
     */
    @Override
    protected String[] getConfigLocations() {

        return new String[] { "namespace-applicationContext.xml" };
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.test.AbstractTransactionalSpringContextTests#onSetUpInTransaction()
     */
    @Override
    protected void onSetUpInTransaction() throws Exception {

        referenceUsers = new ArrayList<User>();

        for (int i = 0; i < NUMBER_OF_INSTANCES; i++) {

            User user = new User("Firstname" + i, "Lastname" + i % 2,
                    "foo@bar.de" + (NUMBER_OF_INSTANCES - i));
            referenceUsers.add(userExtendedDao.save(user));
        }

        assertEquals(NUMBER_OF_INSTANCES, userExtendedDao.count().longValue());
    }


    /**
     * Tests looking up a user by a given example.
     * 
     * @throws Exception
     */
    public void testFindByCriteria() throws Exception {

        User user = new User();
        user.setFirstname("Firstname5");

        List<User> users = userExtendedDao.readByExample(user);

        assertNotNull(users);
        assertEquals(1, users.size());

        User reference = referenceUsers.get(5);
        assertEquals(reference, users.get(0));
    }


    /**
     * Tests, that the generic DAO implementation applies sorting correctly.
     * 
     * @throws Exception
     */
    public void testReadAllSorted() throws Exception {

        List<User> users = userExtendedDao.readAll(new Sort(Order.ASCENDING,
                "emailAddress"));

        assertNotNull(users);
        assertEquals(NUMBER_OF_INSTANCES, users.size());

        assertEquals(referenceUsers.get(NUMBER_OF_INSTANCES - 1), users.get(0));
    }


    /**
     * Tests that the DAO sorts example queries correctly.
     * 
     * @throws Exception
     */
    public void testReadSortedByExample() throws Exception {

        User sampleUser = new User();
        sampleUser.setLastname("Lastname0");

        List<User> users = userExtendedDao.readByExample(new Sort(
                Order.ASCENDING, "emailAddress"), sampleUser);

        assertNotNull(users);
        assertEquals((NUMBER_OF_INSTANCES + 1) / 2, users.size());

        User reference = users.get(0);

        // Asser email address order
        for (int i = 0; i < users.size(); i++) {

            User user = users.get(i);
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
    public void testReadSortedAndPagedByExample() throws Exception {

        User sampleUser = new User();
        sampleUser.setLastname("Lastname0");

        PageRequest request = new PageRequest(1, 2);

        Page<User> page = userExtendedDao.readByExample(request, new Sort(
                Order.ASCENDING, "emailAddress"), sampleUser);

        assertNotNull(page);
        assertTrue(page.hasNextPage());
        assertTrue(page.hasPreviousPage());
        assertEquals(5, page.getTotalElements());
        assertEquals(2, page.getNumberOfElements());
    }
}

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

package org.synyx.hades.domain.page.test;

import org.springframework.test.jpa.AbstractJpaTests;
import org.synyx.hades.dao.UserDao;
import org.synyx.hades.domain.User;
import org.synyx.hades.domain.page.Page;
import org.synyx.hades.domain.page.PageRequest;


/**
 * Integration test for pagination functionality.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class PaginationIntegrationTest extends AbstractJpaTests {

    private UserDao userDao;


    /**
     * Setter to inject {@code UserDao}.
     * 
     * @param userDao the userDao to set
     */
    public void setUserDao(UserDao userDao) {

        this.userDao = userDao;
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

        for (int i = 0; i < 10; i++) {

            User user = new User(String.valueOf(i), String.valueOf(i),
                    "foo@bar.de" + i);

            userDao.save(user);
        }

        assertEquals(10L, userDao.count().longValue());
    }


    /**
     * Tests lookup of a first page. Checks, that it has no previous page but a
     * next page.
     */
    public void testCorrectFirstPage() {

        Page<User> users = userDao.readAll(new PageRequest(0, 2));

        assertEquals(2, users.getPageSize());
        assertEquals(5, users.getTotalPages());
        assertFalse(users.hasPreviousPage());
        assertTrue(users.hasNextPage());
    }


    /**
     * Tests lookups of a last page. Checks, that it has no next page but a
     * previous one.
     */
    public void testCorrectLastPage() {

        Page<User> users = userDao.readAll(new PageRequest(4, 2));

        assertEquals(2, users.getPageSize());
        assertTrue(users.hasPreviousPage());
        assertFalse(users.hasNextPage());

        users = userDao.readAll(new PageRequest(3, 3));

        assertEquals(3, users.getPageSize());
        assertEquals(4, users.getTotalPages());
        assertEquals(1, users.getNumberOfElements());
        assertTrue(users.hasPreviousPage());
        assertFalse(users.hasNextPage());
    }


    /**
     * Tests accessing an invalid page.
     */
    public void testAccessingInvalidPage() {

        Page<User> users = userDao.readAll(new PageRequest(2, 5));

        assertEquals(5, users.getPageSize());
        assertEquals(0, users.getNumberOfElements());
        assertTrue(users.hasPreviousPage());
        assertFalse(users.hasNextPage());
    }
}

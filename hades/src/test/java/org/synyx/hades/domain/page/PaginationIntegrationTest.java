/*
 * Copyright 2008-2009 the original author or authors.
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

package org.synyx.hades.domain.page;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.synyx.hades.dao.UserDao;
import org.synyx.hades.domain.Page;
import org.synyx.hades.domain.PageRequest;
import org.synyx.hades.domain.User;


/**
 * Integration test for pagination functionality.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:namespace-applicationContext.xml")
@Transactional
public class PaginationIntegrationTest {

    @Autowired
    private UserDao userDao;


    @Before
    public void setUp() {

        for (int i = 0; i < 10; i++) {

            User user =
                    new User(String.valueOf(i), String.valueOf(i), "foo@bar.de"
                            + i);

            userDao.save(user);
        }

        assertEquals(10L, userDao.count().longValue());
    }


    /**
     * Tests lookup of a first page. Checks, that it has no previous page but a
     * next page.
     */
    @Test
    public void testCorrectFirstPage() {

        Page<User> users = userDao.readAll(new PageRequest(0, 2));

        assertEquals(2, users.getSize());
        assertEquals(5, users.getTotalPages());
        assertFalse(users.hasPreviousPage());
        assertTrue(users.hasNextPage());
    }


    /**
     * Tests lookups of a last page. Checks, that it has no next page but a
     * previous one.
     */
    @Test
    public void testCorrectLastPage() {

        Page<User> users = userDao.readAll(new PageRequest(4, 2));

        assertEquals(2, users.getSize());
        assertTrue(users.hasPreviousPage());
        assertFalse(users.hasNextPage());

        users = userDao.readAll(new PageRequest(3, 3));

        assertEquals(3, users.getSize());
        assertEquals(4, users.getTotalPages());
        assertEquals(1, users.getNumberOfElements());
        assertTrue(users.hasPreviousPage());
        assertFalse(users.hasNextPage());
    }


    /**
     * Tests accessing an invalid page.
     */
    @Test
    public void testAccessingInvalidPage() {

        Page<User> users = userDao.readAll(new PageRequest(2, 5));

        assertEquals(5, users.getSize());
        assertEquals(0, users.getNumberOfElements());
        assertTrue(users.hasPreviousPage());
        assertFalse(users.hasNextPage());
    }
}

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
package org.synyx.hades.domain.auditing;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.synyx.hades.dao.AuditableUserDao;
import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.dao.UserDao;
import org.synyx.hades.domain.User;


/**
 * Integration test for {@link AuditingAdvice}.
 * 
 * @author Oliver Gierke
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:auditing-integration-test-context.xml" })
public class AuditingAdviceIntegrationTest {

    @Autowired
    private AuditableUserDao userDao;

    @Autowired
    private UserDao plainUserDao;

    private AuditableUser firstUser;
    private AuditableUser secondUser;


    @Before
    public void setUp() throws Exception {

        assertNotNull(userDao);

        firstUser = new AuditableUser();
        secondUser = new AuditableUser();
    }


    /**
     * Checks that the advice intercepts calls to
     * {@link GenericDao#save(org.synyx.hades.domain.Persistable)} correctly.
     */
    @Test
    public void touchesSingleEntityOnSave() {

        firstUser = userDao.save(firstUser);

        assertNotNull(firstUser.getCreatedDate());
        assertNotNull(firstUser.getLastModifiedDate());
    }


    /**
     * Checks that the advice intercepts calls to {@link GenericDao#save(List)}
     * correctly.
     */
    @Test
    public void touchesManyEntitiesOnSave() {

        List<AuditableUser> users =
                userDao.save(Arrays.asList(firstUser, secondUser));

        for (AuditableUser user : users) {

            assertNotNull(user.getCreatedDate());
            assertNotNull(user.getLastModifiedDate());
        }
    }


    /**
     * @see #300
     */
    @Test
    public void doesntTouchNonAuditables() throws Exception {

        plainUserDao.save(Arrays.asList(new User("firstname", "lastname",
                "email")));
    }
}

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
package org.synyx.hades.dao.config;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.synyx.hades.dao.AuditableUserDao;
import org.synyx.hades.dao.RoleDao;
import org.synyx.hades.dao.UserDao;


/**
 * Abstract base class for integration test for namespace configuration.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractDaoConfigIntegrationTest {

    @Autowired(required = false)
    protected UserDao userDao;

    @Autowired(required = false)
    protected RoleDao roleDao;

    @Autowired(required = false)
    protected AuditableUserDao auditableUserDao;


    /**
     * Asserts that context creation detects 3 DAO beans.
     */
    @Test
    public void testContextCreation() {

        assertNotNull(userDao);
        assertNotNull(roleDao);
        assertNotNull(auditableUserDao);
    }
}

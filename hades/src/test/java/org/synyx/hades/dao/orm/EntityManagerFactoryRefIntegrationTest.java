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
package org.synyx.hades.dao.orm;

import javax.persistence.EntityManagerFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.synyx.hades.dao.AuditableUserDao;
import org.synyx.hades.dao.UserDao;
import org.synyx.hades.domain.User;
import org.synyx.hades.domain.auditing.AuditableUser;


/**
 * Assures the injected DAO instances are wired to the customly configured
 * {@link EntityManagerFactory}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:foo.xml")
public class EntityManagerFactoryRefIntegrationTest {

    @Autowired
    UserDao userDao;

    @Autowired
    AuditableUserDao auditableUserDao;


    @Test
    @Transactional
    public void useUserDao() throws Exception {

        userDao.saveAndFlush(new User("firstname", "lastname", "foo@bar.de"));
    }


    @Test
    @Transactional("transactionManager-2")
    public void useAuditableUserDao() throws Exception {

        auditableUserDao.saveAndFlush(new AuditableUser());
    }
}

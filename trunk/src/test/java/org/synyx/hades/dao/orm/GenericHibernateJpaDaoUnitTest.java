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

package org.synyx.hades.dao.orm;

import static org.easymock.EasyMock.*;

import javax.persistence.EntityManager;

import org.easymock.classextension.EasyMock;
import org.hibernate.ejb.HibernateEntityManager;
import org.junit.Before;
import org.junit.Test;
import org.synyx.hades.domain.User;


/**
 * Unit test for {@code GenericHibernateJpaDao}. Primarily tests configuration
 * issues.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class GenericHibernateJpaDaoUnitTest {

    private GenericHibernateJpaDao<User, Integer> hibernateDao;

    private EntityManager entityManager;


    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() {

        // Setup mocks
        entityManager = EasyMock.createNiceMock(HibernateEntityManager.class);

        // Setup DAO
        hibernateDao = new GenericHibernateJpaDao<User, Integer>();
        hibernateDao.setEntityManager(entityManager);
    }


    /**
     * Tests, that {@code InitializingBean#afterPropertiesSet()} does not reject
     * configuration if an instance of {@code HibernateEntityManager} is
     * configured.
     * 
     * @throws Exception
     */
    @Test
    public void correctConfiguration() throws Exception {

        EasyMock.replay(entityManager);

        hibernateDao.afterPropertiesSet();

        EasyMock.verify(entityManager);
    }


    /**
     * Tests, that {@code InitializingBean#afterPropertiesSet()} rejects non
     * {@code HibernateEntityManager} implementations.
     * 
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void preventsNonHibernateEntityManager() throws Exception {

        entityManager = createNiceMock(EntityManager.class);
        hibernateDao.setEntityManager(entityManager);

        replay(entityManager);

        hibernateDao.afterPropertiesSet();
    }
}

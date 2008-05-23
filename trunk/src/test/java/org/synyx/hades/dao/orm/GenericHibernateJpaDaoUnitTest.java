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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import junit.framework.TestCase;

import org.easymock.classextension.EasyMock;
import org.hibernate.ejb.HibernateEntityManager;
import org.synyx.hades.domain.User;


/**
 * Unit test for {@code GenericHibernateJpaDao}. Primarily tests configuration
 * issues.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class GenericHibernateJpaDaoUnitTest extends TestCase {

    private GenericHibernateJpaDao<User, Integer> hibernateDao;

    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;


    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {

        // Setup mocks
        entityManagerFactory = EasyMock
                .createNiceMock(EntityManagerFactory.class);
        entityManager = EasyMock.createNiceMock(HibernateEntityManager.class);

        // Setup DAO
        hibernateDao = new GenericHibernateJpaDao<User, Integer>();
        hibernateDao.setEntityManagerFactory(entityManagerFactory);
    }


    /**
     * Tests, that {@code InitializingBean#afterPropertiesSet()} does not reject
     * configuration if an instance of {@code HibernateEntityManager} is
     * configured.
     * 
     * @throws Exception
     */
    public void testCorrectConfiguration() throws Exception {

        EasyMock.expect(entityManagerFactory.createEntityManager()).andReturn(
                entityManager);
        EasyMock.replay(entityManagerFactory);

        hibernateDao.afterPropertiesSet();

        EasyMock.verify(entityManagerFactory);
    }


    /**
     * Tests, that {@code InitializingBean#afterPropertiesSet()} rejects non
     * {@code HibernateEntityManager} implementations.
     * 
     * @throws Exception
     */
    public void testPreventsNonHibernateEntityManager() throws Exception {

        EasyMock.expect(entityManagerFactory.createEntityManager()).andReturn(
                EasyMock.createNiceMock(EntityManager.class));

        EasyMock.replay(entityManagerFactory);

        try {
            hibernateDao.afterPropertiesSet();
            fail("Expected IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            EasyMock.verify(entityManagerFactory);
        }
    }
}

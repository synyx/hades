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

package org.synyx.hades.dao.orm.support;

import static junit.framework.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.dao.UserDao;
import org.synyx.hades.dao.UserExtendedDao;
import org.synyx.hades.dao.support.GenericDaoFactoryBean;
import org.synyx.hades.domain.User;


/**
 * Unit test for {@code GenericDaoFactoryBean}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
@SuppressWarnings("unchecked")
public class GenericDaoFactoryBeanUnitTest {

    private GenericDaoFactoryBean factory;

    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;


    @Before
    public void setUp() {

        entityManagerFactory =
                EasyMock.createNiceMock(EntityManagerFactory.class);
        entityManager = EasyMock.createNiceMock(EntityManager.class);

        EasyMock.expect(entityManagerFactory.createEntityManager()).andReturn(
                entityManager);

        EasyMock.replay(entityManagerFactory, entityManager);

        // Setup standard factory configuration
        factory = new GenericDaoFactoryBean();
        factory.setDomainClass(User.class);
        factory.setDaoInterface(UserDao.class);
        factory.setEntityManagerFactory(entityManagerFactory);
    }


    /**
     * Assert that the instance created for the standard configuration is a
     * valid {@code UserDao}.
     * 
     * @throws Exception
     */
    @Test
    public void setsUpBasicInstanceCorrectly() throws Exception {

        factory.afterPropertiesSet();

        UserDao userDao = (UserDao) factory.getObject();

        assertNotNull(userDao);
    }


    /**
     * Assert that the factory rejects calls to {@code
     * GenericDaoFactoryBean#setDaoInterface(Class)} with {@code null} or any
     * other parameter instance not implementing {@code GenericDao}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void preventsNullDaoInterface() {

        factory.setDaoInterface(null);
    }


    @Test(expected = IllegalArgumentException.class)
    public void preventsInvalidDaoInterface() {

        factory.setDaoInterface(List.class);
    }


    /**
     * Assert that the factory rejects calls to {@code
     * GenericDaoFactoryBean#setDomainClass(Class)} with {@code null} or any
     * other parameter instance not implementing {@code Persistable}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void preventsNullDomainClasses() {

        factory.setDomainClass(null);
    }


    @Test(expected = IllegalArgumentException.class)
    public void preventsInvalidDomainClasses() {

        factory.setDomainClass(String.class);
    }


    /**
     * Assert that the factory rejects calls to {@code
     * GenericDaoFactoryBean#setDaoClass(Class)} with {@code null} or any other
     * parameter instance not implementing {@code GenericDao}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void preventsNullDaoClass() {

        factory.setDaoClass(null);
    }


    @Test(expected = IllegalArgumentException.class)
    public void preventsInvalidDaoClass() {

        factory.setDaoClass(String.class);
    }


    /**
     * Assert that the factory detects unset DAO class and interface in {@code
     * GenericDaoFactoryBean#afterPropertiesSet()}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void preventsUnsetDaoInterface() throws Exception {

        factory = new GenericDaoFactoryBean();
        factory.afterPropertiesSet();
    }


    @Test(expected = IllegalArgumentException.class)
    public void preventsUnsetDomainClass() throws Exception {

        factory = new GenericDaoFactoryBean();
        factory.setDaoInterface(UserDao.class);
        factory.afterPropertiesSet();
    }


    /**
     * Assert that the factory does not allow DAO instance creation if you have
     * configured a DAO interface extending {@code UserExtendedDao} but not
     * provided any custom base implementation class implementing this
     * interface.
     * 
     * @throws Exception
     */
    @Test(expected = BeanCreationException.class)
    public void preventsCreationOfExtendedGenericDaosIfMisconfigured()
            throws Exception {

        factory.setDaoInterface(UserExtendedDao.class);
        factory.afterPropertiesSet();
    }


    /**
     * Asserts that the factory recognized configured DAO classes that contain
     * custom method but no custom implementation could be found. Furthremore
     * the exception has to contain the name of the DAO interface as for a large
     * DAO configuration it's hard to find out where this error occured.
     * 
     * @throws Exception
     */
    @Test
    public void capturesMissingCustomImplementationAndProvidesInterfacename()
            throws Exception {

        factory.setDaoInterface(SampleCustomDao.class);

        try {
            factory.afterPropertiesSet();
            fail("Expected BeanCreationException");
        } catch (BeanCreationException e) {
            assertTrue(e.getMessage().contains(SampleCustomDao.class.getName()));
        }
    }

    /**
     * Sample interface to contain a custom method.
     * 
     * @author Oliver Gierke - gierke@synyx.de
     */
    private interface SampleCustomDao extends GenericDao<User, Integer> {

        void someSampleMethod();
    }
}

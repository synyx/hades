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

import static org.junit.Assert.*;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.dao.orm.GenericDaoFactoryBean;
import org.synyx.hades.domain.User;


/**
 * Unit test for {@code GenericDaoFactoryBean}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class GenericDaoFactoryBeanUnitTest {

    private GenericDaoFactoryBean<SampleDao> factory;

    private EntityManager entityManager;


    @Before
    public void setUp() {

        entityManager = EasyMock.createNiceMock(EntityManager.class);

        EasyMock.replay(entityManager);

        // Setup standard factory configuration
        factory = GenericDaoFactoryBean.create(SampleDao.class, entityManager);
        factory.setEntityManager(entityManager);
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

        SampleDao sampleDao = (SampleDao) factory.getObject();

        assertNotNull(sampleDao);
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


    /**
     * Assert that the factory detects unset DAO class and interface in {@code
     * GenericDaoFactoryBean#afterPropertiesSet()}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void preventsUnsetDaoInterface() throws Exception {

        factory = new GenericDaoFactoryBean<SampleDao>();
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
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(SampleCustomDao.class.getName()));
        }
    }

    /**
     * Sample interface to contain a custom method.
     * 
     * @author Oliver Gierke - gierke@synyx.de
     */
    private interface SampleCustomDao extends Serializable, SampleDao {

        void someSampleMethod();
    }

    private interface SampleDao extends GenericDao<User, Integer> {

    }
}

/*
 * Copyright 2008-2010 the original author or authors.
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
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.dao.orm.GenericDaoFactoryBean;
import org.synyx.hades.domain.User;


/**
 * Unit test for {@code GenericDaoFactoryBean}.
 * <p>
 * TODO: Check if test methods double the ones in
 * {@link GenericDaoFactoryUnitTest}.
 * 
 * @author Oliver Gierke
 */
@RunWith(MockitoJUnitRunner.class)
public class GenericDaoFactoryBeanUnitTest {

    private GenericDaoFactoryBean<SimpleSampleDao> factory;

    @Mock
    private EntityManager entityManager;

    @Mock
    private ListableBeanFactory beanFactory;
    @Mock
    private PersistenceExceptionTranslator translator;


    @Before
    public void setUp() {

        Map<String, PersistenceExceptionTranslator> beans =
                new HashMap<String, PersistenceExceptionTranslator>();
        beans.put("foo", translator);
        when(
                beanFactory.getBeansOfType(
                        eq(PersistenceExceptionTranslator.class), anyBoolean(),
                        anyBoolean())).thenReturn(beans);

        // Setup standard factory configuration
        factory =
                GenericDaoFactoryBean.create(SimpleSampleDao.class,
                        entityManager);
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

        factory.setBeanFactory(beanFactory);
        factory.afterPropertiesSet();

        assertNotNull(factory.getObject());
    }


    @Test(expected = IllegalArgumentException.class)
    public void requiresListableBeanFactory() throws Exception {

        factory.setBeanFactory(mock(BeanFactory.class));
    }


    /**
     * Assert that the factory rejects calls to
     * {@code GenericDaoFactoryBean#setDaoInterface(Class)} with {@code null} or
     * any other parameter instance not implementing {@code GenericDao}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void preventsNullDaoInterface() {

        factory.setDaoInterface(null);
    }


    /**
     * Assert that the factory detects unset DAO class and interface in
     * {@code GenericDaoFactoryBean#afterPropertiesSet()}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void preventsUnsetDaoInterface() throws Exception {

        factory = new GenericDaoFactoryBean<SimpleSampleDao>();
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

        GenericDaoFactoryBean<SampleDao> factory =
                GenericDaoFactoryBean.create(SampleDao.class, entityManager);

        try {
            factory.afterPropertiesSet();
            fail("Expected IllegalArgumentException!");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(SampleDao.class.getName()));
        }
    }

    private interface SimpleSampleDao extends GenericDao<User, Integer> {

    }

    /**
     * Sample interface to contain a custom method.
     * 
     * @author Oliver Gierke
     */
    private interface SampleCustomDao {

        void someSampleMethod();
    }

    private interface SampleDao extends GenericDao<User, Integer>,
            SampleCustomDao {

    }
}

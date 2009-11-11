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

import java.io.IOException;

import javax.persistence.EntityManager;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.dao.orm.GenericDaoFactory;
import org.synyx.hades.daocustom.CustomGenericDaoFactory;
import org.synyx.hades.daocustom.UserCustomExtendedDao;
import org.synyx.hades.domain.User;


/**
 * Unit test for {@code GenericDaoFactoryBean}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class GenericDaoFactoryUnitTest {

    private GenericDaoFactory factory;

    private EntityManager entityManager;


    @Before
    public void setUp() {

        entityManager = EasyMock.createNiceMock(EntityManager.class);

        EasyMock.replay(entityManager);

        // Setup standard factory configuration
        factory = GenericDaoFactory.create(entityManager);
    }


    /**
     * Assert that the instance created for the standard configuration is a
     * valid {@code UserDao}.
     * 
     * @throws Exception
     */
    @Test
    public void setsUpBasicInstanceCorrectly() throws Exception {

        assertNotNull(factory.getDao(SimpleSampleDao.class));
    }


    @Test
    public void allowsCallingOfObjectMethods() {

        SimpleSampleDao userDao = factory.getDao(SimpleSampleDao.class);

        userDao.hashCode();
        userDao.toString();
        userDao.equals(userDao);
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

        try {
            factory.getDao(SampleDao.class);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(SampleDao.class.getName()));
        }
    }


    @Test(expected = IllegalArgumentException.class)
    public void handlesRuntimeExceptionsCorrectly() {

        SampleDao dao =
                factory.getDao(SampleDao.class, new SampleCustomDaoImpl());
        dao.throwingRuntimeException();
    }


    @Test(expected = IOException.class)
    public void handlesCheckedExceptionsCorrectly() throws Exception {

        SampleDao dao =
                factory.getDao(SampleDao.class, new SampleCustomDaoImpl());
        dao.throwingCheckedException();
    }


    @Test(expected = UnsupportedOperationException.class)
    public void createsProxyWithCustomBaseClass() throws Exception {

        GenericDaoFactory factory =
                CustomGenericDaoFactory.create(entityManager);
        UserCustomExtendedDao dao = factory.getDao(UserCustomExtendedDao.class);

        dao.customMethod(1);
    }

    private interface SimpleSampleDao extends GenericDao<User, Integer> {

    }

    /**
     * Sample interface to contain a custom method.
     * 
     * @author Oliver Gierke - gierke@synyx.de
     */
    public interface SampleCustomDao {

        void throwingRuntimeException();


        void throwingCheckedException() throws IOException;
    }

    /**
     * Implementation of the custom DAO interface.
     * 
     * @author Oliver Gierke - gierke@synyx.de
     */
    private class SampleCustomDaoImpl implements SampleCustomDao {

        public void throwingRuntimeException() {

            throw new IllegalArgumentException("You lose!");
        }


        public void throwingCheckedException() throws IOException {

            throw new IOException("You lose!");
        }
    }

    private interface SampleDao extends GenericDao<User, Integer>,
            SampleCustomDao {

    }
}

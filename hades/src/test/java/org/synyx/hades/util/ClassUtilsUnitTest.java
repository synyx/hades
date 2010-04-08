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
package org.synyx.hades.util;

import static org.junit.Assert.*;
import static org.synyx.hades.util.ClassUtils.*;

import java.io.Serializable;

import javax.persistence.Entity;

import org.junit.Test;
import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.dao.UserDao;
import org.synyx.hades.daocustom.UserCustomExtendedDao;
import org.synyx.hades.domain.Page;
import org.synyx.hades.domain.Pageable;
import org.synyx.hades.domain.User;


/**
 * Unit test for {@link ClassUtils}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class ClassUtilsUnitTest {

    @Test
    public void looksUpDomainClassCorrectly() throws Exception {

        assertEquals(User.class, getDomainClass(UserDao.class));
        assertEquals(User.class, getDomainClass(SomeDao.class));
        assertNull(getDomainClass(Serializable.class));
    }


    @Test
    public void looksUpIdClassCorrectly() throws Exception {

        assertEquals(Integer.class, getIdClass(UserDao.class));
        assertNull(getIdClass(Serializable.class));
    }


    @Test(expected = IllegalStateException.class)
    public void rejectsInvalidReturnType() throws Exception {

        assertReturnType(SomeDao.class.getMethod("findByFirstname",
                Pageable.class, String.class), User.class);
    }


    @Test
    public void usesSimpleClassNameIfNoEntityNameGiven() throws Exception {

        assertEquals("User", getEntityName(User.class));
        assertEquals("AnotherNamedUser", getEntityName(NamedUser.class));
    }


    @Test
    public void findsDomainClassOnExtensionOfDaoInterface() throws Exception {

        assertEquals(User.class,
                getDomainClass(ExtensionOfUserCustomExtendedDao.class));
    }


    /**
     * References #256.
     */
    @Test
    public void detectsParameterizedEntitiesCorrectly() {

        assertEquals(GenericEntity.class,
                getDomainClass(GenericEntityDao.class));
    }

    /**
     * Sample entity with a custom name.
     * 
     * @author Oliver Gierke - gierke@synyx.de
     */
    @Entity(name = "AnotherNamedUser")
    private class NamedUser {

    }

    /**
     * Sample interface to serve two purposes:
     * <ol>
     * <li>Check that {@link ClassUtils#getDomainClass(Class)} skips non
     * {@link GenericDao} interfaces</li>
     * <li>Check that {@link ClassUtils#getDomainClass(Class)} traverses
     * interface hierarchy</li>
     * </ol>
     * 
     * @author Oliver Gierke - gierke@synyx.de
     */
    private interface SomeDao extends Serializable, UserDao {

        Page<User> findByFirstname(Pageable pageable, String firstname);
    }

    /**
     * Sample interface to test recursive lookup of domain class.
     * 
     * @author Oliver Gierke - gierke@synyx.de
     */
    private interface ExtensionOfUserCustomExtendedDao extends
            UserCustomExtendedDao {

    }

    /**
     * Helper class to reproduce #256.
     * 
     * @author Oliver Gierke - gierke@synyx.de
     */
    private class GenericEntity<T> {
    }

    private interface GenericEntityDao extends
            GenericDao<GenericEntity<String>, Long> {

    }
}

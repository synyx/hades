package org.synyx.hades.util;

import static org.junit.Assert.*;

import java.io.Serializable;

import javax.persistence.Entity;

import org.junit.Test;
import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.dao.UserDao;
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
    public void testname() throws Exception {

        assertEquals(User.class, ClassUtils.getDomainClass(UserDao.class));
        assertEquals(User.class, ClassUtils.getDomainClass(SomeDao.class));
    }


    @Test(expected = IllegalStateException.class)
    public void rejectsInvalidReturnType() throws Exception {

        ClassUtils.assertReturnType(SomeDao.class.getMethods()[0], User.class);
    }


    @Test
    public void usesSimpleClassNameIfNoEntityNameGiven() throws Exception {

        assertEquals("User", ClassUtils.getEntityName(User.class));
        assertEquals("AnotherNamedUser", ClassUtils
                .getEntityName(NamedUser.class));
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
     * Sample entity with a custom name.
     * 
     * @author Oliver Gierke - gierke@synyx.de
     */
    @Entity(name = "AnotherNamedUser")
    private class NamedUser {

    }
}

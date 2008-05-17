package org.synyx.hades.dao.orm.support;

import java.util.List;

import junit.framework.TestCase;

import org.springframework.beans.factory.BeanCreationException;
import org.synyx.hades.dao.ExtendedUserDao;
import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.dao.UserDao;
import org.synyx.hades.domain.Persistable;
import org.synyx.hades.domain.User;


/**
 * Unit test for {@link GenericDaoFactoryBean}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
@SuppressWarnings("unchecked")
public class GenericDaoFactoryBeanUnitTest extends TestCase {

    private GenericDaoFactoryBean factory;


    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {

        // Setup standard factory configuration
        factory = new GenericDaoFactoryBean();
        factory.setDomainClass(User.class);
        factory.setDaoInterface(UserDao.class);
    }


    /**
     * Assert that the instance created for the standard configuration is a
     * valid {@link UserDao}.
     * 
     * @throws Exception
     */
    public void testSetsUpBasicInstanceCorrectly() throws Exception {

        factory.afterPropertiesSet();

        UserDao userDao = (UserDao) factory.getObject();

        assertNotNull(userDao);
    }


    /**
     * Assert that the factory rejects calls to
     * {@link GenericDaoFactoryBean#setDaoInterface(Class)} with {@code null} or
     * any other parameter instance not implementing {@link GenericDao}.
     */
    public void testPreventsNullOrInvalidDaoInterface() {

        try {
            factory.setDaoInterface(null);
            fail("Expected IllegalArgumentException!");
        } catch (IllegalArgumentException e) {

        }

        try {
            factory.setDaoInterface(List.class);
            fail("Expected IllegalArgumentException!");
        } catch (IllegalArgumentException e) {

        }
    }


    /**
     * Assert that the factory rejects calls to
     * {@link GenericDaoFactoryBean#setDomainClass(Class)} with {@code null} or
     * any other parameter instance not implementing {@link Persistable}.
     */
    public void testPreventsNullOrInvalidDomainClasses() {

        try {
            factory.setDomainClass(null);
            fail("Expected IllegalArgumentException!");
        } catch (IllegalArgumentException e) {

        }

        try {
            factory.setDomainClass(String.class);
            fail("Expected IllegalArgumentException!");
        } catch (IllegalArgumentException e) {

        }
    }


    /**
     * Assert that the factory rejects calls to
     * {@link GenericDaoFactoryBean#setDaoClass(Class)} with {@code null} or any
     * other parameter instance not implementing {@link GenericDao}.
     */
    public void testPreventsNullOrInvalidDaoClass() {

        try {
            factory.setDaoClass(null);
            fail("Expected IllegalArgumentException!");
        } catch (IllegalArgumentException e) {

        }

        try {
            factory.setDaoClass(String.class);
            fail("Expected IllegalArgumentException!");
        } catch (IllegalArgumentException e) {

        }
    }


    /**
     * Assert that the factory detects unset DAO class and interface in
     * {@link GenericDaoFactoryBean#afterPropertiesSet()}.
     */
    public void testPreventsUnsetDomainClassAndDaoInterface() throws Exception {

        factory = new GenericDaoFactoryBean();

        try {
            factory.afterPropertiesSet();
            fail("Expected IllegalArgumentException!");
        } catch (IllegalArgumentException e) {

        }

        factory.setDaoInterface(UserDao.class);

        try {
            factory.afterPropertiesSet();
            fail("Expected IllegalArgumentException!");
        } catch (IllegalArgumentException e) {

        }

        factory = new GenericDaoFactoryBean();
        factory.setDomainClass(User.class);

        try {
            factory.afterPropertiesSet();
            fail("Expected IllegalArgumentException!");
        } catch (IllegalArgumentException e) {

        }
    }


    /**
     * Assert that the factory does not allow DAO instance creation if you have
     * configured a DAO interface extending {@link ExtendedUserDao} but not
     * provided any custom base implementation class implementing this
     * interface.
     * 
     * @throws Exception
     */
    public void testPreventsCreationOfExtendedGenericDaosIfMisconfigured()
            throws Exception {

        factory.setDaoInterface(ExtendedUserDao.class);

        try {
            factory.afterPropertiesSet();
            fail("Expected BeanCreationException");
        } catch (BeanCreationException e) {

        }
    }
}

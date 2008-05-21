package org.synyx.hades.dao.orm.support;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.springframework.beans.factory.BeanCreationException;
import org.synyx.hades.dao.UserDao;
import org.synyx.hades.dao.UserExtendedDao;
import org.synyx.hades.domain.User;


/**
 * Unit test for {@code GenericDaoFactoryBean}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
@SuppressWarnings("unchecked")
public class GenericDaoFactoryBeanUnitTest extends TestCase {

    private GenericDaoFactoryBean factory;

    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;


    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {

        entityManagerFactory = EasyMock
                .createNiceMock(EntityManagerFactory.class);
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
    public void testSetsUpBasicInstanceCorrectly() throws Exception {

        factory.afterPropertiesSet();

        UserDao userDao = (UserDao) factory.getObject();

        assertNotNull(userDao);
    }


    /**
     * Assert that the factory rejects calls to
     * {@code GenericDaoFactoryBean#setDaoInterface(Class)} with {@code null} or
     * any other parameter instance not implementing {@code GenericDao}.
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
     * {@code GenericDaoFactoryBean#setDomainClass(Class)} with {@code null} or
     * any other parameter instance not implementing {@code Persistable}.
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
     * {@code GenericDaoFactoryBean#setDaoClass(Class)} with {@code null} or any
     * other parameter instance not implementing {@code GenericDao}.
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
     * {@code GenericDaoFactoryBean#afterPropertiesSet()}.
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
     * configured a DAO interface extending {@code UserExtendedDao} but not
     * provided any custom base implementation class implementing this
     * interface.
     * 
     * @throws Exception
     */
    public void testPreventsCreationOfExtendedGenericDaosIfMisconfigured()
            throws Exception {

        factory.setDaoInterface(UserExtendedDao.class);

        try {
            factory.afterPropertiesSet();
            fail("Expected BeanCreationException");
        } catch (BeanCreationException e) {

        }
    }
}

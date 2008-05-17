package org.synyx.hades.dao.orm.support;

import javax.persistence.EntityManager;

import junit.framework.TestCase;

import org.easymock.classextension.EasyMock;
import org.hibernate.ejb.EntityManagerImpl;
import org.synyx.hades.dao.orm.GenericHibernateJpaDao;
import org.synyx.hades.domain.User;


/**
 * @author Oliver Gierke - gierke@synyx.de
 */
public class GenericHibernateJpaDaoUnitTest extends TestCase {

    private GenericHibernateJpaDao<User, Integer> hibernateDao;


    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {

        hibernateDao = new GenericHibernateJpaDao<User, Integer>();
        hibernateDao.setEntityManager(EasyMock
                .createNiceMock(EntityManagerImpl.class));

        hibernateDao.afterPropertiesSet();
    }


    public void testPreventsNonHibernateEntityManager() throws Exception {

        EntityManager entityManager = EasyMock
                .createNiceMock(EntityManager.class);
        EasyMock.replay(entityManager);

        hibernateDao.setEntityManager(entityManager);

        try {
            hibernateDao.afterPropertiesSet();
            fail("Expected IllegalArgumentException!");
        } catch (IllegalArgumentException e) {

        }
    }
}

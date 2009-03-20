package org.synyx.hades.dao.query;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.lang.reflect.Method;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.synyx.hades.dao.UserDao;
import org.synyx.hades.domain.User;


/**
 * Unit test for {@link FinderMethod}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class FinderMethodUnitTest {

    private static final String PREFIX = "findBy";
    private static final Class<?> DOMAIN_CLASS = User.class;

    private Method daoMethod;
    private EntityManager em;


    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {

        daoMethod = UserDao.class.getMethod("findByLastname", String.class);
        em = createNiceMock(EntityManager.class);
    }


    @Test
    public void testname() throws Exception {

        FinderMethod method =
                new FinderMethod(daoMethod, PREFIX, DOMAIN_CLASS, null, em);

        assertEquals(daoMethod, method.getMethod());
        assertEquals("User.findByLastname", method.getNamedQueryName());
        assertTrue(method.isCollectionFinder());
        assertEquals("select x from User x where x.lastname = ?", method
                .constructQuery());
    }


    @Test(expected = IllegalArgumentException.class)
    public void preventsNullDaoMethod() throws Exception {

        new FinderMethod(null, PREFIX, DOMAIN_CLASS, null, em);
    }


    @Test(expected = IllegalArgumentException.class)
    public void preventsNullPrefix() throws Exception {

        new FinderMethod(daoMethod, null, DOMAIN_CLASS, null, em);
    }


    @Test(expected = IllegalArgumentException.class)
    public void preventsEmptyPrefix() throws Exception {

        new FinderMethod(daoMethod, "", DOMAIN_CLASS, null, em);
    }


    @Test(expected = IllegalArgumentException.class)
    public void preventsWrongPrefix() throws Exception {

        new FinderMethod(daoMethod, "readBy", DOMAIN_CLASS, null, em);
    }


    @Test(expected = IllegalArgumentException.class)
    public void preventsNullDomainClass() throws Exception {

        new FinderMethod(daoMethod, PREFIX, null, null, em);
    }


    @Test(expected = IllegalArgumentException.class)
    public void preventsNullEntityManager() throws Exception {

        new FinderMethod(daoMethod, PREFIX, DOMAIN_CLASS, null, null);
    }
}

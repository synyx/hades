package org.synyx.hades.dao.test;

import java.util.ArrayList;
import java.util.List;

import org.springframework.test.jpa.AbstractJpaTests;
import org.synyx.hades.dao.UserExtendedDao;
import org.synyx.hades.domain.User;


/**
 * Integration test for {@code GenericHibernateJpaDao}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class GenericHibernateJpaDaoIntegrationTest extends AbstractJpaTests {

    private UserExtendedDao userExtendedDao;
    private List<User> referenceUsers;


    /**
     * @param extendedUserDao the userDao to set
     */
    public void setUserExtendedDao(UserExtendedDao extendedUserDao) {

        this.userExtendedDao = extendedUserDao;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.test.AbstractSingleSpringContextTests#getConfigLocations()
     */
    @Override
    protected String[] getConfigLocations() {

        return new String[] { "namespace-applicationContext.xml" };
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.test.AbstractTransactionalSpringContextTests#onSetUpInTransaction()
     */
    @Override
    protected void onSetUpInTransaction() throws Exception {

        referenceUsers = new ArrayList<User>();

        for (int i = 0; i < 10; i++) {

            User user = new User("Firstname" + i, "Lastname" + i, "foo@bar.de"
                    + i);
            referenceUsers.add(userExtendedDao.save(user));

        }

        assertEquals(10, userExtendedDao.count().longValue());
    }


    /**
     * Tests looking up a user by a given example.
     * 
     * @throws Exception
     */
    public void testFindByCriteria() throws Exception {

        User user = new User();
        user.setFirstname("Firstname5");

        List<User> users = userExtendedDao.readByExample(user);

        assertNotNull(users);
        assertEquals(1, users.size());

        User reference = referenceUsers.get(5);
        assertEquals(reference, users.get(0));
    }

}

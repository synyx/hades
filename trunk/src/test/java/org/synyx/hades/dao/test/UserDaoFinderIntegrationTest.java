package org.synyx.hades.dao.test;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.AbstractTransactionalSpringContextTests;
import org.synyx.hades.dao.UserDao;


/**
 * @author Oliver Gierke - gierke@synyx.de
 */
public class UserDaoFinderIntegrationTest extends
        AbstractTransactionalSpringContextTests {

    private UserDao userDao;


    /**
     * @param userDao the userDao to set
     */
    public void setUserDao(UserDao userDao) {

        this.userDao = userDao;
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


    public void testComplexFinder() throws Exception {

        try {
            userDao.findByEmailAddressAndLastname("foo", "bar");
            fail("Expected EmptyResultDataAccessException!");
        } catch (EmptyResultDataAccessException e) {

        }
    }


    public void testAndOrFinder() {

        try {
            userDao.findByEmailAddressAndLastnameOrFirstname("foo", "bar",
                    "foobar");
            fail("Expected EmptyResultDataAccessException!");
        } catch (EmptyResultDataAccessException e) {

        }
    }
}

/**
 * 
 */
package org.synyx.hades.dao.test;

import org.springframework.test.jpa.AbstractJpaTests;
import org.synyx.hades.dao.AuditableUserDao;
import org.synyx.hades.domain.auditable.AuditableUser;


/**
 * @author Oliver Gierke - gierke@synyx.de
 */
public class AuditableUserDaoTest extends AbstractJpaTests {

    private AuditableUser user;
    private AuditableUserDao userDao;


    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.test.AbstractSingleSpringContextTests#getConfigLocations()
     */
    @Override
    protected String[] getConfigLocations() {

        return new String[] { "audition-application-context.xml" };
    }


    /**
     * @param userDao the userDao to set
     */
    public void setUserDao(AuditableUserDao userDao) {

        this.userDao = userDao;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.test.AbstractTransactionalSpringContextTests#onSetUpBeforeTransaction()
     */
    @Override
    protected void onSetUpBeforeTransaction() throws Exception {

        user = new AuditableUser();
        user.setFirstname("Firstname");
    }


    public void testUserCreation() throws Exception {

        userDao.save(user);
    }
}

package org.synyx.dao.test;

import org.easymock.classextension.EasyMock;
import org.springframework.test.jpa.AbstractJpaTests;
import org.synyx.dao.AuditableUserDao;
import org.synyx.domain.AuditableUser;
import org.synyx.domain.support.CurrentUserAware;

import com.synyx.utils.test.easymock.EasyMockUtils;


/**
 * Integration test for the {@code AuditionAdvice}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class AuditionAdviceIntegrationTest extends AbstractJpaTests {

    private CurrentUserAware<AuditableUser, Long> currentUserAware;
    private AuditableUserDao userDao;


    /**
     * Setter to inject the current user provider
     * 
     * @param currentUserAware the currentUserAware to set
     */
    public void setCurrentUserAware(
            CurrentUserAware<AuditableUser, Long> currentUserAware) {

        this.currentUserAware = currentUserAware;
    }


    /**
     * Setter to inject the user dao.
     * 
     * @param userDao the userDao to set
     */
    public void setUserDao(AuditableUserDao userDao) {

        this.userDao = userDao;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.test.AbstractSingleSpringContextTests#getConfigLocations()
     */
    @Override
    protected String[] getConfigLocations() {

        return new String[] { "audition-application-context.xml",
                "audition-advice-context.xml" };
    }


    /**
     * Tests, that the auditioning advice got applied by checking if the
     * entities have correct date and user values set.
     * 
     * @throws Exception
     * @throws Exception
     */
    public void testAuditionAdviceGetsApplied() throws Exception {

        AuditableUser user = new AuditableUser();

        // Configure mock
        CurrentUserAware<AuditableUser, Long> mock = EasyMockUtils
                .unwrap(currentUserAware);
        EasyMock.expect(mock.getCurrentUser()).andReturn(user).once();
        EasyMock.replay(mock);

        userDao.save(user);

        EasyMockUtils.verify(mock);

        // Assert dates set
        assertNotNull(user.getCreatedDate());
        assertNotNull(user.getLastModifiedDate());

        // Assert users set
        assertEquals(user, user.getCreatedBy());
        assertEquals(user, user.getLastModifiedBy());
    }
}

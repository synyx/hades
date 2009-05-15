package org.synyx.hades.dao.config;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.synyx.hades.dao.AuditableUserDao;
import org.synyx.hades.dao.RoleDao;
import org.synyx.hades.dao.UserDao;


/**
 * Abstract base class for integration test for namespace configuration.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public abstract class AbstractDaoConfigIntegrationTest extends
        AbstractDependencyInjectionSpringContextTests {

    protected UserDao userDao;
    protected RoleDao roleDao;
    protected AuditableUserDao auditableUserDao;


    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.test.AbstractDependencyInjectionSpringContextTests#prepareTestInstance()
     */
    @Override
    protected void prepareTestInstance() throws Exception {

        setPopulateProtectedVariables(true);
        super.prepareTestInstance();
    }


    /**
     * Asserts that context creation detects 3 DAO beans.
     */
    public void testContextCreation() {

        assertNotNull(userDao);
        assertNotNull(roleDao);
        assertNotNull(auditableUserDao);
    }
}

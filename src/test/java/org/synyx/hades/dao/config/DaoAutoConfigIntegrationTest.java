package org.synyx.hades.dao.config;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.synyx.hades.dao.AuditableUserDao;
import org.synyx.hades.dao.RoleDao;
import org.synyx.hades.dao.UserDao;


/**
 * Integration test to test DAO auto configuration.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class DaoAutoConfigIntegrationTest extends
        AbstractDependencyInjectionSpringContextTests {

    protected UserDao userDao;
    protected RoleDao roleDao;
    protected AuditableUserDao auditableUserDao;


    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.test.AbstractSingleSpringContextTests#getConfigLocations()
     */
    @Override
    protected String[] getConfigLocations() {

        setPopulateProtectedVariables(true);
        return new String[] { "namespace-autoconfig-context.xml" };
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

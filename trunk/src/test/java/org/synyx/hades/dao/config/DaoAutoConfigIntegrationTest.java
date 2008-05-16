package org.synyx.hades.dao.config;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.synyx.hades.dao.RoleDao;
import org.synyx.hades.dao.UserDao;


/**
 * @author Oliver Gierke - gierke@synyx.de
 */
public class DaoAutoConfigIntegrationTest extends
        AbstractDependencyInjectionSpringContextTests {

    protected UserDao userDao;
    protected RoleDao roleDao;


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


    public void testContextCreation() {

        assertNotNull(userDao);
        assertNotNull(roleDao);
    }
}

package org.synyx.hades.dao.config;

import static org.junit.Assert.*;

import org.springframework.test.context.ContextConfiguration;


/**
 * Integration test to test
 * {@link org.springframework.core.type.filter.TypeFilter} integration into
 * namespace.
 * 
 * @author Oliver Gierke
 */
@ContextConfiguration(locations = "classpath:namespace-autoconfig-typefilter-context.xml")
public class TypeFilterConfigIntegrationTest extends
        AbstractDaoConfigIntegrationTest {

    /*
     * (non-Javadoc)
     * 
     * @seeorg.synyx.hades.dao.config.AbstractDaoConfigIntegrationTest#
     * testContextCreation()
     */
    @Override
    public void testContextCreation() {

        assertNotNull(userDao);
        assertNotNull(roleDao);
        assertNull(auditableUserDao);
    }
}

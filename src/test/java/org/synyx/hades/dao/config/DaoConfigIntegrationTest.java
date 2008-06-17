package org.synyx.hades.dao.config;

/**
 * Integration test for DAO namespace configuration.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class DaoConfigIntegrationTest extends AbstractDaoConfigIntegrationTest {

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.test.AbstractSingleSpringContextTests#getConfigLocations()
     */
    @Override
    protected String[] getConfigLocations() {

        return new String[] { "namespace-applicationContext.xml" };
    }
}

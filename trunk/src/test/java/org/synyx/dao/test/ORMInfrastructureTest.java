package org.synyx.dao.test;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;


/**
 * @author Oliver Gierke - gierke@synyx.de
 */
public class ORMInfrastructureTest extends
        AbstractDependencyInjectionSpringContextTests {

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.test.AbstractSingleSpringContextTests#getConfigLocations()
     */
    @Override
    protected String[] getConfigLocations() {

        return new String[] { "infrastructure.xml" };
    }


    public void testFooBar() throws Exception {

        assertNotNull(getApplicationContext());
    }
}

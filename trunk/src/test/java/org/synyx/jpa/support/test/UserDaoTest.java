package org.synyx.jpa.support.test;

/**
 * Integration test class for <code>PersonDao</code> using standard Spring
 * beans configuration.
 * 
 * @author Eberhard Wolff
 * @author Oliver Gierke
 */
public class UserDaoTest extends AbstractUserDaoTest {

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.test.AbstractSingleSpringContextTests#getConfigLocations()
     */
    @Override
    protected String[] getConfigLocations() {

        return new String[] { "applicationContext.xml" };
    }
}

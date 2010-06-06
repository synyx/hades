package org.synyx.hades.dao;

import org.synyx.hades.domain.User;


/**
 * Dummy implementation to allow check for invoking a custom implementation.
 * 
 * @author Oliver Gierke
 */
public class UserDaoImpl implements UserDaoCustom {

    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.UserDao#someOtherMethod(org.synyx.hades.domain.User)
     */
    public void someCustomMethod(final User u) {

        System.out.println("Some custom method was invoked!");
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.UserDaoCustom#findFooMethod()
     */
    public void findByOverrridingMethod() {

        System.out.println("A mthod overriding a finder was invoked!");
    }
}

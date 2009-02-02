package org.synyx.hades.dao.config;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.synyx.hades.dao.AuditableUserDao;
import org.synyx.hades.dao.RoleDao;
import org.synyx.hades.dao.UserDao;


/**
 * Abstract base class for integration test for namespace configuration.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractDaoConfigIntegrationTest {

    @Autowired(required = false)
    protected UserDao userDao;

    @Autowired(required = false)
    protected RoleDao roleDao;

    @Autowired(required = false)
    protected AuditableUserDao auditableUserDao;


    /**
     * Asserts that context creation detects 3 DAO beans.
     */
    @Test
    public void testContextCreation() {

        assertNotNull(userDao);
        assertNotNull(roleDao);
        assertNotNull(auditableUserDao);
    }
}

package org.synyx.hades.domain.support;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.synyx.hades.dao.AuditableUserDao;
import org.synyx.hades.domain.AuditableUser;


/**
 * @author Oliver Gierke - gierke@synyx.de
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:auditing-integration-test-context.xml" })
public class AuditingAdviceIntegrationTest {

    @Autowired
    private AuditableUserDao userDao;

    private AuditableUser firstUser;
    private AuditableUser secondUser;


    public void setUp() {

        assertNotNull(userDao);

        firstUser = new AuditableUser();
        secondUser = new AuditableUser();
    }


    @Test
    public void touchesSingleEntityOnSave() {

        userDao.save(firstUser);

        assertNotNull(firstUser.getCreatedDate());
        assertNotNull(firstUser.getLastModifiedDate());
    }
}

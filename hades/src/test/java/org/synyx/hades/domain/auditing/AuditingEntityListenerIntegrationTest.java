package org.synyx.hades.domain.auditing;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.synyx.hades.dao.AuditableUserDao;


/**
 * @author Oliver Gierke
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:auditing-entity-listener.xml")
public class AuditingEntityListenerIntegrationTest {

    @Autowired
    AuditableUserDao dao;


    @Test
    public void testname() throws Exception {

        AuditableUser user = new AuditableUser();
        dao.save(user);
    }
}

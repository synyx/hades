package org.synyx.hades.sample.dao;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.sample.auditing.AuditableUser;
import org.synyx.hades.sample.auditing.AuditorAwareImpl;


/**
 * @author Oliver Gierke - gierke@synyx.de
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:auditing-context.xml" })
@Transactional
public class AuditableUserSample {

    @Autowired
    private GenericDao<AuditableUser, Long> dao;

    @Autowired
    private AuditorAwareImpl auditorAware;


    @Test
    public void testname() throws Exception {

        AuditableUser user = new AuditableUser();
        user.setUsername("username");

        auditorAware.setAuditor(user);

        user = dao.save(user);

        user = dao.save(user);

        assertEquals(user, user.getCreatedBy());
        assertEquals(user, user.getLastModifiedBy());
    }
}

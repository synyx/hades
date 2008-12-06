package org.synyx.hades.domain.support;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.synyx.hades.dao.AuditableUserDao;
import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.domain.AuditableUser;


/**
 * Integration test for {@link AuditingAdvice}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:auditing-integration-test-context.xml" })
public class AuditingAdviceIntegrationTest {

    @Autowired
    private AuditableUserDao userDao;

    private AuditableUser firstUser;
    private AuditableUser secondUser;


    @Before
    public void setUp() throws Exception {

        assertNotNull(userDao);

        firstUser = new AuditableUser();
        secondUser = new AuditableUser();
    }


    /**
     * Checks that the advice intercepts calls to
     * {@link GenericDao#save(org.synyx.hades.domain.Persistable)} correctly.
     */
    @Test
    public void touchesSingleEntityOnSave() {

        firstUser = userDao.save(firstUser);

        assertNotNull(firstUser.getCreatedDate());
        assertNotNull(firstUser.getLastModifiedDate());
    }


    /**
     * Checks that the advice intercepts calls to
     * {@link GenericDao#saveAll(List)} correctly.
     */
    @Test
    public void touchesManyEntitiesOnSave() {

        List<AuditableUser> users =
                userDao.saveAll(Arrays.asList(firstUser, secondUser));

        for (AuditableUser user : users) {

            assertNotNull(user.getCreatedDate());
            assertNotNull(user.getLastModifiedDate());
        }
    }
}

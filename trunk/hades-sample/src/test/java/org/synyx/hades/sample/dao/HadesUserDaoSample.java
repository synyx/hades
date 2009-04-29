package org.synyx.hades.sample.dao;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.synyx.hades.sample.domain.User;


/**
 * Intergration test showing the basic usage of {@link UserDao}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:dao-context.xml")
@Transactional
public class HadesUserDaoSample {

    @Autowired
    private UserDao userDao;


    /**
     * Tests inserting a user and asserts it can be loaded again.
     */
    @Test
    public void testInsert() {

        User user = new User();
        user.setUsername("username");

        user = userDao.save(user);

        assertEquals(user, userDao.readByPrimaryKey(user.getId()));
    }


    @Test
    public void foo() {

        User user = new User();
        user.setUsername("foobar");
        user.setLastname("lastname");

        user = userDao.save(user);

        List<User> users = userDao.findByLastname("lastname");

        assertNotNull(users);
        assertTrue(users.contains(user));

        User reference = userDao.findByTheUsersName("foobar");
        assertEquals(user, reference);
    }


    /**
     * Test invocation of custom method.
     */
    // @Test
    public void testCustomMethod() {

        User user = new User();
        user.setUsername("username");

        user = userDao.save(user);

        List<User> users = userDao.myCustomBatchOperation();

        assertNotNull(users);
        assertTrue(users.contains(user));
    }
}

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

        userDao.save(user);

        assertEquals(user, userDao.readByPrimaryKey(user.getId()));
    }


    @Test
    public void foo() throws Exception {

        User user = new User();
        user.setUsername("foobar");

        userDao.save(user);

        List<User> users = userDao.findByUsername("foobar");

        assertNotNull(users);
        assertTrue(users.contains(user));
    }


    /**
     * Test invocation of custom method.
     */
    // @Test
    public void testCustomMethod() {

        User user = new User();
        user.setUsername("username");

        userDao.save(user);

        List<User> users = userDao.myCustomBatchOperation();

        assertNotNull(users);
        assertTrue(users.contains(user));
    }
}

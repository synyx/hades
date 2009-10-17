package org.synyx.hades.sample.dao;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.synyx.hades.sample.dao.simple.SimpleUserDao;
import org.synyx.hades.sample.domain.User;


/**
 * Intergration test showing the basic usage of {@link SimpleUserDao}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:simple-dao-context.xml")
@Transactional
public class HadesSimpleUserDaoSample {

    @Autowired
    private SimpleUserDao userDao;

    private User user;


    @Before
    public void setUp() {

        user = new User();
        user.setUsername("foobar");
        user.setFirstname("firstname");
        user.setLastname("lastname");
    }


    /**
     * Tests inserting a user and asserts it can be loaded again.
     */
    @Test
    public void testInsert() {

        user = userDao.save(user);

        assertEquals(user, userDao.readByPrimaryKey(user.getId()));
    }


    @Test
    public void foo() throws Exception {

        user = userDao.save(user);

        List<User> users = userDao.findByLastname("lastname");

        assertNotNull(users);
        assertTrue(users.contains(user));
    }


    @Test
    public void testname() throws Exception {

        user = userDao.save(user);

        List<User> users = userDao.findByFirstnameOrLastname("lastname");

        assertTrue(users.contains(user));
    }
}

package org.synyx.hades.sample.dao;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.synyx.hades.dao.orm.GenericDaoFactory;
import org.synyx.hades.sample.dao.simple.SimpleUserDao;
import org.synyx.hades.sample.domain.User;


/**
 * Test case showing how to use the basic {@link GenericDaoFactory}
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class BasicFactorySetup {

    private static final EntityManagerFactory factory =
            Persistence.createEntityManagerFactory("hades.sample.jpa");

    private SimpleUserDao userDao;
    private EntityManager em;

    private User user;


    /**
     * Creates a {@link SimpleUserDao} instance.
     * 
     * @throws Exception
     */
    @Before
    public void setUp() {

        em = factory.createEntityManager();

        userDao = GenericDaoFactory.create(em).getDao(SimpleUserDao.class);

        em.getTransaction().begin();

        user = new User();
        user.setUsername("username");
        user.setFirstname("firstname");
        user.setLastname("lastname");

        user = userDao.save(user);

    }


    /**
     * Rollback transaction.
     */
    @After
    public void tearDown() {

        em.getTransaction().rollback();
    }


    /**
     * Showing invocation of finder method.
     */
    @Test
    public void executingFinders() {

        assertEquals(user, userDao.findByTheUsersName("username"));
        assertEquals(user, userDao.findByLastname("lastname").get(0));
        assertEquals(user, userDao.findByFirstname("firstname").get(0));
    }
}

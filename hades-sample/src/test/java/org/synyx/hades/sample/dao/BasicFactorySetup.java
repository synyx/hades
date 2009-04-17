package org.synyx.hades.sample.dao;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

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


    /**
     * Creates a {@link UserDao} instance.
     * 
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {

        em = factory.createEntityManager();

        userDao = GenericDaoFactory.create(em).getDao(SimpleUserDao.class);
    }


    @Test
    public void savingUsers() throws Exception {

        em.getTransaction().begin();

        User user = new User();
        user.setUsername("username");

        user = userDao.saveAndFlush(user);

        em.getTransaction().commit();

        assertEquals(user, userDao.findByUsername("username").get(0));
    }

}

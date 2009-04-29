package org.synyx.hades.sample.dao;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Before;
import org.junit.Test;
import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.dao.orm.GenericJpaDao;
import org.synyx.hades.sample.domain.User;


/**
 * This unit tests shows plain usage of {@link GenericJpaDao}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class BasicSample {

    private GenericDao<User, Long> userDao;
    private EntityManager em;


    /**
     * Sets up a {@link GenericJpaDao} instance.
     */
    @Before
    public void setUp() {

        EntityManagerFactory factory =
                Persistence.createEntityManagerFactory("hades.sample.jpa");
        em = factory.createEntityManager();

        userDao = GenericJpaDao.create(em, User.class);
    }


    /**
     * Tests saving users. Don't mimic transactionality shown here. It seriously
     * lacks resource cleanup in case of an exception. Simplification serves
     * descriptivness.
     */
    @Test
    public void savingUsers() {

        em.getTransaction().begin();

        User user = new User();
        user.setUsername("username");

        user = userDao.save(user);

        em.getTransaction().commit();

        assertEquals(user, userDao.readByPrimaryKey(user.getId()));
    }
}

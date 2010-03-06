package org.synyx.hades.sample.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;

import org.synyx.hades.sample.dao.UserDaoCustom;
import org.synyx.hades.sample.domain.User;


/**
 * Implementation fo the custom DAo functionality declared in
 * {@link UserDaoCustom} based on JPA. To use this implementation in combination
 * with Hades you can either register it programatically:
 * 
 * <pre>
 * EntityManager em = ... // Obtain EntityManager
 * 
 * UserDaoCustom custom = new UserDaoImpl();
 * custom.setEntityManager(em);
 * 
 * GenericDaoFactory factory = GenericDaoFactory.create(em);
 * UserDao dao = factory.getDao(UserDao.class, custom);
 * </pre>
 * 
 * Using the Spring namespace the implementation will just get picked up due to
 * the classpath scanning for implementations with the {@code Impl} postfix.
 * 
 * <pre>
 * &lt;hades:dao-config base-package=&quot;org.synyx.hades.sample.dao&quot; /&gt;
 * </pre>
 * 
 * If you need to manually configure the custom instance see
 * {@link UserDaoJdbcImpl} for an example.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class UserDaoImpl implements UserDaoCustom {

    @PersistenceContext
    private EntityManager em;


    /**
     * Configure the entity manager to be used.
     * 
     * @param em the {@link EntityManager} to set.
     */
    public void setEntityManager(EntityManager em) {

        this.em = em;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.sample.dao.UserDaoCustom#myCustomBatchOperation()
     */
    public List<User> myCustomBatchOperation() {

        CriteriaQuery<User> criteriaQuery =
                em.getCriteriaBuilder().createQuery(User.class);

        return em.createQuery(criteriaQuery).getResultList();
    }

}

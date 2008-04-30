package org.synyx.hades.dao.orm;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Example;
import org.hibernate.ejb.EntityManagerImpl;
import org.springframework.beans.factory.InitializingBean;
import org.synyx.hades.dao.ExtendedGenericDao;
import org.synyx.hades.domain.Entity;
import org.synyx.hades.domain.Pageable;


/**
 * Implements extended generic dao functionality with Hibernate.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class GenericHibernateJpaDao<T extends Entity<PK>, PK extends Serializable>
        extends GenericJpaDao<T, PK> implements ExtendedGenericDao<T, PK>,
        InitializingBean {

    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.hades.dao.ExtendedGenericDao#readByExample(org.synyx.hades.hades.domain.Identifyable)
     */

    public List<T> readByExample(T... examples) {

        return readByExample(null, examples);
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.hades.dao.ExtendedGenericDao#readbyExample(java.awt.print.Pageable,
     *      T[])
     */
    @SuppressWarnings("unchecked")
    public List<T> readByExample(Pageable pageable, T... examples) {

        // Prevent null examples to cause trouble
        if (null == examples || examples.length == 0) {
            return readAll(pageable);
        }

        Criteria criteria = prepareCriteria(examples);

        // Apply pagination
        if (null != pageable) {
            criteria.setFirstResult(pageable.getFirstItem());
            criteria.setMaxResults(pageable.getNumberOfItems());
        }

        return criteria.list();
    }


    /**
     * Prepares a {@code Criteria} for the given examples.
     * 
     * @param examples
     * @return
     */
    private Criteria prepareCriteria(T... examples) {

        // Create criteria from hibernate entity manager
        EntityManagerImpl hibernateEntityManager = (EntityManagerImpl) getEntityManager();
        Criteria criteria = hibernateEntityManager.getSession().createCriteria(
                getDomainClass());

        // Add examples
        for (T example : examples) {

            Example criteriaExample = Example.create(example);
            criteria.add(criteriaExample);
        }

        return criteria;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {

        // Assure we really have an hibernate EntityManager
        if (!(getEntityManager() instanceof EntityManagerImpl)) {
            throw new IllegalStateException(
                    getClass().getSimpleName()
                            + " can only be used with Hibernate EntityManager implementation! Please check configuration or use "
                            + GenericJpaDao.class.getSimpleName()
                            + " instead!");
        }
    }
}

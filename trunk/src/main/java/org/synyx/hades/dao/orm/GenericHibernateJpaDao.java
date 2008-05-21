package org.synyx.hades.dao.orm;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.hibernate.Criteria;
import org.hibernate.criterion.Example;
import org.hibernate.ejb.HibernateEntityManager;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.util.Assert;
import org.synyx.hades.dao.ExtendedGenericDao;
import org.synyx.hades.domain.Persistable;
import org.synyx.hades.domain.page.Page;
import org.synyx.hades.domain.page.PageImpl;
import org.synyx.hades.domain.page.Pageable;


/**
 * Implements extended generic dao functionality with Hibernate.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class GenericHibernateJpaDao<T extends Persistable<PK>, PK extends Serializable>
        extends GenericJpaDao<T, PK> implements ExtendedGenericDao<T, PK> {

    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.hades.dao.ExtendedGenericDao#readByExample(org.synyx.hades.hades.domain.Identifyable)
     */
    @SuppressWarnings("unchecked")
    public List<T> readByExample(final T... examples) {

        Criteria criteria = prepareCriteria(examples);

        return criteria.list();
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.hades.dao.ExtendedGenericDao#readbyExample(java.awt.print.Pageable,
     *      T[])
     */
    @SuppressWarnings("unchecked")
    public Page<T> readByExample(final Pageable pageable, final T... examples) {

        Assert.notNull(pageable, "Pageable must not be null!");

        // Prevent null examples to cause trouble
        if (null == examples || examples.length == 0) {
            return readAll(pageable);
        }

        Criteria criteria = prepareCriteria(examples);

        // Apply pagination
        if (null != pageable) {
            criteria.setFirstResult(pageable.getPage()
                    * pageable.getNumberOfItems());
            criteria.setMaxResults(pageable.getNumberOfItems());
        }

        return new PageImpl(criteria.list(), pageable.getPage(), pageable
                .getNumberOfItems(), count());
    }


    /**
     * Prepares a {@code Criteria} for the given examples.
     * 
     * @param examples
     * @return
     */
    private Criteria prepareCriteria(final T... examples) {

        // Create criteria from hibernate entity manager
        return (Criteria) getJpaTemplate().execute(new JpaCallback() {

            /*
             * (non-Javadoc)
             * 
             * @see org.springframework.orm.jpa.JpaCallback#doInJpa(javax.persistence.EntityManager)
             */
            public Criteria doInJpa(final EntityManager em)
                    throws PersistenceException {

                HibernateEntityManager hibernateEntityManager = (HibernateEntityManager) em;
                Criteria criteria = hibernateEntityManager.getSession()
                        .createCriteria(getDomainClass());

                // Add examples
                for (T example : examples) {

                    Example criteriaExample = Example.create(example);
                    criteria.add(criteriaExample);
                }

                return criteria;
            }
        }, true);
    }


    /**
     * Checks, that a {@code HibernateEntityManager} was set and rejects
     * configuration otherwise.
     * 
     * @see org.synyx.hades.dao.orm.AbstractJpaFinder#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        super.afterPropertiesSet();

        EntityManager em = (EntityManager) getJpaTemplate().execute(
                new JpaCallback() {

                    /*
                     * (non-Javadoc)
                     * 
                     * @see org.springframework.orm.jpa.JpaCallback#doInJpa(javax.persistence.EntityManager)
                     */
                    public EntityManager doInJpa(final EntityManager em)
                            throws PersistenceException {

                        return em;
                    }
                }, true);

        Assert.isInstanceOf(HibernateEntityManager.class, em, getClass()
                .getSimpleName()
                + " can only be used with Hibernate EntityManager "
                + "implementation! Please check configuration or use "
                + GenericJpaDao.class.getSimpleName() + " instead!");
    }
}

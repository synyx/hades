package org.synyx.hades.daocustom;

import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.dao.orm.GenericDaoFactoryBean;
import org.synyx.hades.dao.orm.GenericJpaDao;


/**
 * {@link GenericDaoFactoryBean} to return a custom DAO base class.
 * 
 * @author Gil Markham
 * @author Oliver Gierke - gierke@synyx.de
 */
public class CustomGenericDaoFactoryBean<T extends GenericDao<?, ?>> extends
        GenericDaoFactoryBean<T> {

    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.orm.GenericDaoFactory#getDaoClass()
     */
    @Override
    @SuppressWarnings("unchecked")
    protected Class<? extends GenericJpaDao> getDaoClass() {

        return CustomGenericJpaDao.class;
    }
}

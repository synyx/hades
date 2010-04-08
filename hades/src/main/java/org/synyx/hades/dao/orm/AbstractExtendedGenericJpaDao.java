package org.synyx.hades.dao.orm;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.synyx.hades.dao.ExtendedGenericDao;
import org.synyx.hades.domain.Page;
import org.synyx.hades.domain.Pageable;
import org.synyx.hades.domain.Sort;


/**
 * Base class for implementations of {@link ExtendedGenericDao} that simply
 * routes varargs parameterized methods to the {@link Collection} based ones.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public abstract class AbstractExtendedGenericJpaDao<T, PK extends Serializable>
        extends GenericJpaDao<T, PK> implements ExtendedGenericDao<T, PK> {

    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.ExtendedGenericDao#deleteByExample(T[])
     */
    public void deleteByExample(T... examples) {

        deleteByExample(Arrays.asList(examples));
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.ExtendedGenericDao#readByExample(T[])
     */
    public List<T> readByExample(T... examples) {

        return readByExample(Arrays.asList(examples));
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.synyx.hades.dao.ExtendedGenericDao#readByExample(org.synyx.hades.
     * domain.Sort, T[])
     */
    public List<T> readByExample(Sort sort, T... examples) {

        return readByExample(sort, Arrays.asList(examples));
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.synyx.hades.dao.ExtendedGenericDao#readByExample(org.synyx.hades.
     * domain.Pageable, T[])
     */
    public Page<T> readByExample(Pageable pageable, T... examples) {

        return readByExample(pageable, Arrays.asList(examples));
    }
}

package org.synyx.hades.sample.dao;

import java.util.List;

import org.synyx.hades.sample.domain.User;


/**
 * Interface for DAO functionality that ought to be implemented manually.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public interface UserDaoCustom {

    /**
     * Custom DAO operation.
     * 
     * @return
     */
    public List<User> myCustomBatchOperation();
}

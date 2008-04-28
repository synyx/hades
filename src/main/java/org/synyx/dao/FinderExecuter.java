package org.synyx.dao;

import java.util.List;


/**
 * Interface for finder executers to retrieve a list of objects of type T.
 * Normally clients do not need to work with this interface.
 * 
 * @author Eberhard Wolff
 * @author Oliver Gierke - gierke@synyx.de
 */
public interface FinderExecuter<T> {

    /**
     * Executes a finder and returns the result list.
     * 
     * @param methodName
     * @param queryArgs
     * @return a list of objects meeting the finder's criterias
     */
    public List<T> executeFinder(String methodName, Object... queryArgs);
}

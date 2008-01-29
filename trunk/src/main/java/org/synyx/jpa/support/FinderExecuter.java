package org.synyx.jpa.support;

import java.util.List;


/**
 * Interface for finder executers to retrieve a list of objects of type T.
 * Normally clients do not need to work with this interface.
 * 
 * @author Eberhard Wolff
 * @author Oliver Gierke
 */
public interface FinderExecuter<T> {

    /**
     * Executes a finder and returns the result list.
     * 
     * @param methodName
     * @param queryArgs
     * @return
     */
    public List<T> executeFinder(String methodName, final Object... queryArgs);
}

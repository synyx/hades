/*
 * Copyright 2002-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.synyx.hades.dao;

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
    List<T> executeFinder(final String methodName, final Object... queryArgs);


    /**
     * Executes a finder for a single result.
     * 
     * @param methodName
     * @param queryArgs
     * @return a single result returned by the named query
     * @throws EntityNotFoundException if no entity was found
     * @throws NonUniqueResultException if more than one entity was found
     */
    T executeObjectFinder(final String methodName, final Object... queryArgs);
}

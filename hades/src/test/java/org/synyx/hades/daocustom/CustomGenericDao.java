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

package org.synyx.hades.daocustom;

import java.io.Serializable;

import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.domain.Persistable;


/**
 * Extension of {@link GenericDao} to be added on a custom DAO base class. This
 * tests the facility to implement custom base class functionality for all DAO
 * instances derived from this interface and implementation base class.
 * 
 * @author Gil Markham
 * @author Oliver Gierke - gierke@synyx.de
 */
public interface CustomGenericDao<T extends Persistable<PK>, PK extends Serializable>
        extends GenericDao<T, PK> {

    /**
     * Custom sample method.
     * 
     * @param primaryKey
     * @return
     */
    T customMethod(final PK primaryKey);
}

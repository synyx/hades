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

import org.synyx.hades.dao.orm.GenericJpaDao;
import org.synyx.hades.domain.Persistable;


/**
 * Sample custom DAO base class implementing common custom functionality for all
 * derived DAO instances.
 * 
 * @author Gil Markham
 * @author Oliver Gierke - gierke@synyx.de
 */
public class CustomGenericJpaDao<T extends Persistable<PK>, PK extends Serializable>
        extends GenericJpaDao<T, PK> implements CustomGenericDao<T, PK> {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.synyx.hades.customimpl.CustomExtendedGenericDao#customMethod(java
     * .io.Serializable)
     */
    public T customMethod(PK primaryKey) {

        throw new UnsupportedOperationException(
                "Forced exception for testing purposes.");
    }
}

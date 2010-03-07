/*
 * Copyright 2008-2009 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.synyx.hades.dao.query;

import javax.persistence.Query;


/**
 * Interface for a query abstraction.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
interface HadesQuery {

    /**
     * Creates a JPA {@link Query} with the given {@link ParameterBinder} from
     * the {@link HadesQuery}.
     * 
     * @param binder
     * @return
     */
    Query createJpaQuery(ParameterBinder binder);


    /**
     * Creates a JPA {@link Query} to count the instances of the
     * {@link HadesQuery} to be returned.
     * 
     * @param binder
     * @return
     */
    Query createCountQuery(ParameterBinder binder);
}
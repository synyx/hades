/*
 * Copyright 2008-2010 the original author or authors.
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

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.synyx.hades.domain.User;


/**
 * Custom Extended DAO interface for a {@code User}. This relies on the custom
 * intermediate DAO interface {@link CustomGenericDao}.
 * 
 * @author Oliver Gierke
 */
public interface UserCustomExtendedDao extends CustomGenericDao<User, Integer> {

    /**
     * Sample method to test reconfiguring transactions on CRUD methods in
     * combination with custom factory.
     * 
     * @see #421
     */
    @Transactional(readOnly = false, timeout = 10)
    List<User> readAll();


    @Transactional(readOnly = false, timeout = 10)
    User readByPrimaryKey(Integer primaryKey);
}
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

import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.dao.orm.GenericDaoFactoryBean;
import org.synyx.hades.dao.orm.GenericJpaDao;


/**
 * {@link GenericDaoFactoryBean} to return a custom DAO base class.
 * 
 * @author Gil Markham
 * @author Oliver Gierke
 */
public class CustomGenericDaoFactoryBean<T extends GenericDao<?, ?>> extends
        GenericDaoFactoryBean<T> {

    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.orm.GenericDaoFactory#getDaoClass()
     */
    @Override
    @SuppressWarnings("rawtypes")
    protected Class<? extends GenericJpaDao> getDaoClass() {

        return CustomGenericJpaDao.class;
    }
}

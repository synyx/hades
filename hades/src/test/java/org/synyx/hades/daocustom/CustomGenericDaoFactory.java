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

import javax.persistence.EntityManager;

import org.synyx.hades.dao.orm.GenericDaoFactory;
import org.synyx.hades.dao.orm.GenericJpaDao;


/**
 * Sample implementation of a custom {@link GenericDaoFactory} to use a custom
 * DAO base class.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class CustomGenericDaoFactory extends GenericDaoFactory {

    /**
     * Factory method to create a custom {@link GenericDaoFactory} instance.
     * 
     * @param em
     * @return
     */
    public static GenericDaoFactory create(EntityManager em) {

        GenericDaoFactory factory = new CustomGenericDaoFactory();
        factory.setEntityManager(em);

        return factory;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.orm.GenericDaoFactory#getDaoClass()
     */
    @Override
    @SuppressWarnings("unchecked")
    protected Class<? extends GenericJpaDao> getDaoClass() {

        return CustomGenericJpaDao.class;
    }
}

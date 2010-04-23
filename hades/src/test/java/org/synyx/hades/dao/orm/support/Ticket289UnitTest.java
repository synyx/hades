/*
 * Copyright 2008-2010 the original author or authors.
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
package org.synyx.hades.dao.orm.support;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.dao.orm.GenericDaoFactory;
import org.synyx.hades.dao.orm.GenericJpaDao;
import org.synyx.hades.domain.User;


/**
 * Unit test to reproduce #289.
 * 
 * @author Oliver Gierke
 */
@RunWith(MockitoJUnitRunner.class)
public class Ticket289UnitTest {

    @Mock
    private EntityManager entityManager;


    @Test
    public void invokesMethodOnGenericDaoBaseClassIfCustomImplementationProvided()
            throws Exception {

        GenericDaoFactory factory = new CustomGenericDaoFactory();
        factory.setEntityManager(entityManager);

        SampleDao dao =
                factory.getDao(SampleDao.class, new SampleCustomDaoImpl());
        dao.generalCustomMethod();
    }

    // DAO interface

    private interface SampleDao extends SampleCustomDao,
            CustomGenericDao<User, Long> {

    }

    // Custom DAO and implementation

    private interface SampleCustomDao {

        void sampleCustomMethod();
    }

    private class SampleCustomDaoImpl implements SampleCustomDao {

        public void sampleCustomMethod() {

        }
    }

    // Intermediate DAO interface

    private static interface CustomGenericDao<T, PK extends Serializable>
            extends GenericDao<T, PK> {

        void generalCustomMethod();
    }

    // Custom factory and base class

    public static class CustomDaoBaseClass<T, PK extends Serializable> extends
            GenericJpaDao<T, PK> implements CustomGenericDao<T, PK> {

        public void generalCustomMethod() {

        }
    }

    private static class CustomGenericDaoFactory extends GenericDaoFactory {

        @Override
        @SuppressWarnings("unchecked")
        protected Class<? extends GenericJpaDao> getDaoClass() {

            return CustomDaoBaseClass.class;
        }
    }
}

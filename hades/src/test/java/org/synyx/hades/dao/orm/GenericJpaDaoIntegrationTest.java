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
package org.synyx.hades.dao.orm;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.domain.SampleEntity;
import org.synyx.hades.domain.SampleEntityPK;


/**
 * Integration test for {@link GenericJpaDao}.
 * 
 * @author Oliver Gierke
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:infrastructure.xml" })
@Transactional
public class GenericJpaDaoIntegrationTest {

    @PersistenceContext
    EntityManager em;

    GenericDao<SampleEntity, SampleEntityPK> dao;


    @Before
    public void setUp() {

        dao = GenericDaoFactory.create(em).getDao(SampleEntityDao.class);
    }


    @Test
    public void testCrudOperationsForCompoundKeyEntity() throws Exception {

        SampleEntity entity = new SampleEntity("foo", "bar");
        dao.saveAndFlush(entity);
        assertThat(dao.count(), is(1L));
        assertThat(dao.readByPrimaryKey(new SampleEntityPK("foo", "bar")),
                is(entity));

        dao.delete(Arrays.asList(entity));
        dao.flush();
        assertThat(dao.count(), is(0L));
    }

    private static interface SampleEntityDao extends
            GenericDao<SampleEntity, SampleEntityPK> {

    }
}

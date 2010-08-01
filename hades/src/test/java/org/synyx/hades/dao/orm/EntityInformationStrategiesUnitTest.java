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

import javax.persistence.EmbeddedId;
import javax.persistence.Id;

import org.junit.Test;
import org.synyx.hades.dao.orm.GenericDaoSupport.IdAware;
import org.synyx.hades.dao.orm.GenericDaoSupport.IsNewAware;
import org.synyx.hades.dao.orm.GenericDaoSupport.PersistableEntityInformation;
import org.synyx.hades.dao.orm.GenericDaoSupport.ReflectiveEntityInformation;
import org.synyx.hades.domain.AbstractPersistable;


/**
 * Unit test for various implementations of {@link IsNewAware}.
 * 
 * @author Oliver Gierke
 */
public class EntityInformationStrategiesUnitTest {

    @Test
    public void detectsPersistableCorrectly() throws Exception {

        PersistableEntityInformation info = new PersistableEntityInformation();

        assertNewAndNoId(info, new PersistableEntity(null));
        assertNotNewAndId(info, new PersistableEntity(1L), 1L);
    }


    @Test
    public void detectsFieldAnnotatedIdCorrectly() throws Exception {

        ReflectiveEntityInformation info =
                new ReflectiveEntityInformation(FieldAnnotatedEntity.class);

        assertNewAndNoId(info, new FieldAnnotatedEntity(null));
        assertNotNewAndId(info, new FieldAnnotatedEntity(1L), 1L);
    }


    @Test
    public void detectsEmbeddedIdFieldAnnotatedIdCorrectly() throws Exception {

        ReflectiveEntityInformation info =
                new ReflectiveEntityInformation(
                        EmbeddedIdFieldAnnotatedEntity.class);

        assertNewAndNoId(info, new EmbeddedIdFieldAnnotatedEntity(null));
        assertNotNewAndId(info, new EmbeddedIdFieldAnnotatedEntity(1L), 1L);
    }


    @Test
    public void detectsMethodAnnotatedIdCorrectly() throws Exception {

        ReflectiveEntityInformation info =
                new ReflectiveEntityInformation(MethodAnnotatedEntity.class);

        assertNewAndNoId(info, new MethodAnnotatedEntity());

        MethodAnnotatedEntity entity = new MethodAnnotatedEntity();
        entity.id = 1L;
        assertNotNewAndId(info, entity, 1L);
    }


    @Test
    public void detectsEmbeddedIdMethodAnnotatedIdCorrectly() throws Exception {

        ReflectiveEntityInformation strategy =
                new ReflectiveEntityInformation(
                        EmbeddedIdMethodAnnotatedEntity.class);

        assertNewAndNoId(strategy, new EmbeddedIdMethodAnnotatedEntity());

        EmbeddedIdMethodAnnotatedEntity entity =
                new EmbeddedIdMethodAnnotatedEntity();
        entity.id = 1L;
        assertNotNewAndId(strategy, entity, 1L);
    }


    private <T extends IdAware & IsNewAware> void assertNewAndNoId(T info,
            Object entity) {

        assertThat(info.isNew(entity), is(true));
        assertThat(info.getId(entity), is(nullValue()));
    }


    private <T extends IdAware & IsNewAware> void assertNotNewAndId(T info,
            Object entity, Object id) {

        assertThat(info.isNew(entity), is(false));
        assertThat(info.getId(entity), is(id));
    }

    static class PersistableEntity extends AbstractPersistable<Long> {

        private static final long serialVersionUID = -5898780128204716452L;


        public PersistableEntity(Long id) {

            setId(id);
        }
    }

    static class FieldAnnotatedEntity {

        @Id
        Long id;


        public FieldAnnotatedEntity(Long id) {

            this.id = id;
        }
    }

    static class EmbeddedIdFieldAnnotatedEntity {

        @EmbeddedId
        Long id;


        public EmbeddedIdFieldAnnotatedEntity(Long id) {

            this.id = id;
        }
    }

    static class MethodAnnotatedEntity {

        private Long id;


        @Id
        public Long getId() {

            return id;
        }
    }

    static class EmbeddedIdMethodAnnotatedEntity {

        private Long id;


        @EmbeddedId
        public Long getId() {

            return id;
        }
    }
}

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
import javax.persistence.Entity;
import javax.persistence.Id;

import org.junit.Test;
import org.synyx.hades.dao.orm.GenericDaoSupport.IsNewStrategy;
import org.synyx.hades.domain.AbstractPersistable;


/**
 * Unit test for various implementations of {@link IsNewStrategy}.
 * 
 * @author Oliver Gierke
 */
public class IsNewStrategiesUnitTest {

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNullAsDomainClass() throws Exception {

        new ReflectiveEntityInformation(null);
    }


    @Test(expected = IllegalArgumentException.class)
    public void rejectsNonEntityClasses() throws Exception {

        new ReflectiveEntityInformation(NotAnnotatedEntity.class);
    }


    @Test(expected = IllegalArgumentException.class)
    public void rejectsEntityWithMissingIdAnnotation() throws Exception {

        new ReflectiveEntityInformation(EntityWithOutIdAnnotation.class);
    }


    @Test
    public void detectsPersistableCorrectly() throws Exception {

        IsNewStrategy strategy =
                new GenericDaoSupport.PersistableIsNewStrategy();

        PersistableEntity entity = new PersistableEntity();
        assertThat(strategy.isNew(entity), is(true));

        entity.setId(1L);
        assertThat(strategy.isNew(entity), is(false));
    }


    @Test
    public void detectsFieldAnnotatedIdCorrectly() throws Exception {

        IsNewStrategy strategy =
                new GenericDaoSupport.ReflectiveIsNewStrategy(
                        FieldAnnotatedEntity.class);

        FieldAnnotatedEntity entity = new FieldAnnotatedEntity(null);
        assertThat(strategy.isNew(entity), is(true));

        entity = new FieldAnnotatedEntity(1L);
        assertThat(strategy.isNew(entity), is(false));
    }


    @Test
    public void detectsEmbeddedIdFieldAnnotatedIdCorrectly() throws Exception {

        IsNewStrategy strategy =
                new GenericDaoSupport.ReflectiveIsNewStrategy(
                        EmbeddedIdFieldAnnotatedEntity.class);

        EmbeddedIdFieldAnnotatedEntity entity =
                new EmbeddedIdFieldAnnotatedEntity(null);
        assertThat(strategy.isNew(entity), is(true));

        entity = new EmbeddedIdFieldAnnotatedEntity(1L);
        assertThat(strategy.isNew(entity), is(false));
    }


    @Test
    public void detectsMethodAnnotatedIdCorrectly() throws Exception {

        IsNewStrategy strategy =
                new GenericDaoSupport.ReflectiveIsNewStrategy(
                        MethodAnnotatedEntity.class);

        MethodAnnotatedEntity entity = new MethodAnnotatedEntity();
        assertThat(strategy.isNew(entity), is(true));

        entity = new MethodAnnotatedEntity();
        entity.id = 1L;
        assertThat(strategy.isNew(entity), is(false));
    }


    @Test
    public void detectsEmbeddedIdMethodAnnotatedIdCorrectly() throws Exception {

        IsNewStrategy strategy =
                new GenericDaoSupport.ReflectiveIsNewStrategy(
                        EmbeddedIdMethodAnnotatedEntity.class);

        EmbeddedIdMethodAnnotatedEntity entity =
                new EmbeddedIdMethodAnnotatedEntity();
        assertThat(strategy.isNew(entity), is(true));

        entity = new EmbeddedIdMethodAnnotatedEntity();
        entity.id = 1L;
        assertThat(strategy.isNew(entity), is(false));
    }

    static class PersistableEntity extends AbstractPersistable<Long> {

        private static final long serialVersionUID = -5898780128204716452L;
    }

    @Entity
    static class FieldAnnotatedEntity {

        @Id
        Long id;


        public FieldAnnotatedEntity(Long id) {

            this.id = id;
        }
    }

    @Entity
    static class EmbeddedIdFieldAnnotatedEntity {

        @EmbeddedId
        Long id;


        public EmbeddedIdFieldAnnotatedEntity(Long id) {

            this.id = id;
        }
    }

    @Entity
    static class MethodAnnotatedEntity {

        private Long id;


        @Id
        public Long getId() {

            return id;
        }
    }

    @Entity
    static class EmbeddedIdMethodAnnotatedEntity {

        private Long id;


        @EmbeddedId
        public Long getId() {

            return id;
        }
    }

    static class NotAnnotatedEntity {

        @Id
        public Long id;
    }

    @Entity
    static class EntityWithOutIdAnnotation {

    }
}

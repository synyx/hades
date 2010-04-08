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

    static class PersistableEntity extends AbstractPersistable<Long> {

        private static final long serialVersionUID = -5898780128204716452L;
    }

    static class FieldAnnotatedEntity {

        @Id
        Long id;


        public FieldAnnotatedEntity(Long id) {

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
}

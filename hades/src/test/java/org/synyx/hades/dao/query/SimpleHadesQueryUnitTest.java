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
package org.synyx.hades.dao.query;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.QueryHint;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.synyx.hades.dao.QueryHints;
import org.synyx.hades.dao.UserDao;
import org.synyx.hades.domain.User;


/**
 * Unit test for {@link SimpleHadesQuery}.
 * 
 * @author Oliver Gierke
 */
@RunWith(MockitoJUnitRunner.class)
public class SimpleHadesQueryUnitTest {

    @Mock
    private QueryMethod method;

    @Mock
    private EntityManager em;
    @Mock
    private QueryExtractor extractor;
    @Mock
    private Query query;


    @Before
    @QueryHints(@QueryHint(name = "foo", value = "bar"))
    public void setUp() throws SecurityException, NoSuchMethodException {

        when(em.createQuery(anyString())).thenReturn(query);

        Method setUp = UserDao.class.getMethod("findByLastname", String.class);
        method = new QueryMethod(setUp, User.class, extractor);
    }


    @Test
    public void appliesHintsCorrectly() throws Exception {

        SimpleHadesQuery hadesQuery =
                new SimpleHadesQuery(method, em, "foobar");
        hadesQuery.createQuery(em, new ParameterBinder(method.getParameters(),
                "gierke"));

        verify(query).setHint("foo", "bar");
    }
}

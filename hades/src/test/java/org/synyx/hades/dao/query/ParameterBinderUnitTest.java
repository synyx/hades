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

import static org.easymock.EasyMock.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.lang.reflect.Method;

import javax.persistence.Query;

import org.junit.Before;
import org.junit.Test;
import org.synyx.hades.dao.query.ParametersUnitTest.SampleDao;
import org.synyx.hades.domain.Pageable;
import org.synyx.hades.domain.Sort;


/**
 * Unit test for {@link ParameterBinder}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class ParameterBinderUnitTest {

    private Method valid;

    private Query query;
    private Method useIndexedParameters;


    @Before
    public void setUp() throws SecurityException, NoSuchMethodException {

        valid = SampleDao.class.getMethod("valid", String.class);

        useIndexedParameters =
                SampleDao.class.getMethod("useIndexedParameters", String.class);

        query = createMock(Query.class);
    }


    @Test(expected = IllegalArgumentException.class)
    public void rejectsToManyParameters() throws Exception {

        new ParameterBinder(new Parameters(valid), "foo", "bar");
    }


    @Test(expected = IllegalArgumentException.class)
    public void rejectsNullParameters() throws Exception {

        new ParameterBinder(new Parameters(valid), (Object[]) null);
    }


    @Test(expected = IllegalArgumentException.class)
    public void rejectsToLittleParameters() throws SecurityException,
            NoSuchMethodException {

        Parameters parameters = new Parameters(valid);
        new ParameterBinder(parameters);
    }


    @Test
    public void returnsNullIfNoPageableWasProvided() throws SecurityException,
            NoSuchMethodException {

        Method method =
                SampleDao.class.getMethod("validWithPageable", String.class,
                        Pageable.class);

        Parameters parameters = new Parameters(method);
        ParameterBinder binder = new ParameterBinder(parameters, "foo", null);

        assertThat(binder.getPageable(), is(nullValue()));
    }


    @Test
    public void bindWorksWithNullForSort() throws Exception {

        Method validWithSort =
                SampleDao.class.getMethod("validWithSort", String.class,
                        Sort.class);

        expect(query.setParameter(eq("username"), eq("foo"))).andReturn(query)
                .once();
        executeAndVerifyMethod(validWithSort, "foo", null);
    }


    @Test
    public void bindWorksWithNullForPageable() throws Exception {

        Method validWithPageable =
                SampleDao.class.getMethod("validWithPageable", String.class,
                        Pageable.class);

        expect(query.setParameter(eq("username"), eq("foo"))).andReturn(query)
                .once();
        executeAndVerifyMethod(validWithPageable, "foo", null);
    }


    @Test
    public void usesIndexedParametersIfNoParamAnnotationPresent()
            throws Exception {

        expect(query.setParameter(eq(1), anyObject())).andReturn(query).once();
        executeAndVerifyMethod(useIndexedParameters, "foo");
    }


    @Test
    public void usesParameterNameIfAnnotated() throws Exception {

        expect(query.setParameter(eq("username"), anyObject()))
                .andReturn(query).once();
        executeAndVerifyMethod(valid, "foo");
    }


    private void executeAndVerifyMethod(Method method, Object... values) {

        replay(query);
        new ParameterBinder(new Parameters(method), values).bind(query);
        verify(query);
    }
}

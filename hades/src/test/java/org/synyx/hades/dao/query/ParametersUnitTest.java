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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.synyx.hades.dao.Param;
import org.synyx.hades.domain.Pageable;
import org.synyx.hades.domain.Sort;
import org.synyx.hades.domain.User;


/**
 * Unit test for {@link Parameters}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class ParametersUnitTest {

    private Method valid;


    @Before
    public void setUp() throws SecurityException, NoSuchMethodException {

        valid = SampleDao.class.getMethod("valid", String.class);
    }


    @Test
    public void checksValidMethodCorrectly() throws Exception {

        Method validWithPageable =
                SampleDao.class.getMethod("validWithPageable", String.class,
                        Pageable.class);
        Method validWithSort =
                SampleDao.class.getMethod("validWithSort", String.class,
                        Sort.class);

        new Parameters(valid);
        new Parameters(validWithPageable);
        new Parameters(validWithSort);
    }


    @Test(expected = IllegalArgumentException.class)
    public void rejectsInvalidMethodWithParamMissing() throws Exception {

        Method method =
                SampleDao.class.getMethod("invalidParamMissing", String.class,
                        String.class);
        new Parameters(method);
    }


    @Test(expected = IllegalArgumentException.class)
    public void rejectsInvalidMethodWithPageableAnnotated() throws Exception {

        Method method =
                SampleDao.class.getMethod("invalidPageable", String.class,
                        Pageable.class);
        new Parameters(method);
    }


    @Test(expected = IllegalArgumentException.class)
    public void rejectsInvalidMethodWithSortAnnotated() throws Exception {

        Method method =
                SampleDao.class.getMethod("invalidSort", String.class,
                        Sort.class);
        new Parameters(method);
    }


    @Test(expected = IllegalArgumentException.class)
    public void rejectsNullMethod() throws Exception {

        new Parameters(null);
    }


    @Test
    public void detectsNamedParameterCorrectly() throws Exception {

        Method method =
                SampleDao.class.getMethod("validWithSort", String.class,
                        Sort.class);

        Parameters parameters = new Parameters(method);

        assertThat(parameters.isNamedParameter(0), is(true));
        assertThat(parameters.getPlaceholder(0), is(":username"));

        assertThat(parameters.isNamedParameter(1), is(false));
        assertThat(parameters.isSpecialParameter(1), is(true));
        assertThat(parameters.getPlaceholder(1), is("?"));
    }

    static interface SampleDao {

        User useIndexedParameters(String lastname);


        User valid(@Param("username") String username);


        User invalidParamMissing(@Param("username") String username,
                String lastname);


        User validWithPageable(@Param("username") String username,
                Pageable pageable);


        User validWithSort(@Param("username") String username, Sort sort);


        User invalidPageable(@Param("username") String username,
                @Param("foo") Pageable pageable);


        User invalidSort(@Param("username") String username,
                @Param("foo") Sort sort);

    }
}

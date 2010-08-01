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
package org.synyx.hades.extensions.conversion;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.extensions.converter.GenericDaoConverter;


/**
 * Unit test for {@link GenericDaoConverter}.
 * 
 * @author Oliver Gierke
 */
@RunWith(MockitoJUnitRunner.class)
public class GenericDaoConverterUnitTest {

    private static final User USER = new User();

    private GenericDaoConverter converter;

    @Mock
    private ApplicationContext context;
    @Mock
    private UserDao dao;
    @Mock
    private ConversionService service;

    private Map<String, GenericDao> daos;
    private TypeDescriptor sourceDescriptor;
    private TypeDescriptor targetDescriptor;


    @Before
    public void setUp() {

        converter = new GenericDaoConverter(service);
        daos = new HashMap<String, GenericDao>();

        sourceDescriptor = TypeDescriptor.valueOf(String.class);
        targetDescriptor = TypeDescriptor.valueOf(User.class);

        when(context.getBeansOfType(GenericDao.class)).thenReturn(daos);

    }


    @Test
    public void matchFailsIfNoDaoAvailable() throws Exception {

        converter.setApplicationContext(context);
        assertMatches(false);
    }


    @Test
    public void matchesIfConversionInBetweenIsPossible() throws Exception {

        daos.put("user", dao);
        converter.setApplicationContext(context);

        when(service.canConvert(String.class, Long.class)).thenReturn(true);

        assertMatches(true);
    }


    @Test
    public void matchFailsIfNoIntermediateConversionIsPossible()
            throws Exception {

        daos.put("user", dao);
        converter.setApplicationContext(context);

        when(service.canConvert(String.class, Long.class)).thenReturn(false);

        assertMatches(false);
    }


    private void assertMatches(boolean matchExpected) {

        assertThat(converter.matches(sourceDescriptor, targetDescriptor),
                is(matchExpected));
    }


    @Test
    public void convertsStringToUserCorrectly() throws Exception {

        daos.put("user", dao);
        converter.setApplicationContext(context);

        when(service.canConvert(String.class, Long.class)).thenReturn(true);
        when(service.convert(anyString(), eq(Long.class))).thenReturn(1L);
        when(dao.readByPrimaryKey(1L)).thenReturn(USER);

        Object user =
                converter.convert("1", sourceDescriptor, targetDescriptor);
        assertThat(user, is(instanceOf(User.class)));
        assertThat(user, is((Object) USER));
    }

    private static class User {

    }

    private static interface UserDao extends GenericDao<User, Long> {

    }
}

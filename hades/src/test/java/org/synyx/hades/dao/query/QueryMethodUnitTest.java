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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.QueryHint;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.dao.Modifying;
import org.synyx.hades.dao.UserDao;
import org.synyx.hades.domain.Page;
import org.synyx.hades.domain.Pageable;
import org.synyx.hades.domain.Sort;
import org.synyx.hades.domain.User;


/**
 * Unit test for {@link QueryMethod}.
 * 
 * @author Oliver Gierke
 */
@RunWith(MockitoJUnitRunner.class)
public class QueryMethodUnitTest {

    static final Class<?> DOMAIN_CLASS = User.class;

    private Method daoMethod;

    @Mock
    private QueryExtractor extractor;

    private static final String METHOD_NAME = "findByFirstname";
    private Method invalidReturnType;
    private Method pageableAndSort;
    private Method pageableTwice;
    private Method sortableTwice;
    private Method modifyingMethod;


    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {

        daoMethod = UserDao.class.getMethod("findByLastname", String.class);

        invalidReturnType =
                InvalidDao.class.getMethod(METHOD_NAME, String.class,
                        Pageable.class);
        pageableAndSort =
                InvalidDao.class.getMethod(METHOD_NAME, String.class,
                        Pageable.class, Sort.class);
        pageableTwice =
                InvalidDao.class.getMethod(METHOD_NAME, String.class,
                        Pageable.class, Pageable.class);

        sortableTwice =
                InvalidDao.class.getMethod(METHOD_NAME, String.class,
                        Sort.class, Sort.class);
        modifyingMethod =
                UserDao.class.getMethod("renameAllUsersTo", String.class);
    }


    @Test
    public void testname() throws Exception {

        QueryMethod method =
                new QueryMethod(daoMethod, DOMAIN_CLASS, extractor);

        assertEquals("User.findByLastname", method.getNamedQueryName());
        assertTrue(method.isCollectionQuery());
        assertEquals("select x from User x where x.lastname = ?1",
                new QueryCreator(method).constructQuery());
    }


    @Test(expected = IllegalArgumentException.class)
    public void preventsNullDaoMethod() throws Exception {

        new QueryMethod(null, DOMAIN_CLASS, extractor);
    }


    @Test(expected = IllegalArgumentException.class)
    public void preventsNullDomainClass() throws Exception {

        new QueryMethod(daoMethod, null, extractor);
    }


    @Test(expected = IllegalArgumentException.class)
    public void preventsNullQueryExtractor() throws Exception {

        new QueryMethod(daoMethod, DOMAIN_CLASS, null);
    }


    @Test
    public void returnsCorrectName() {

        QueryMethod method =
                new QueryMethod(daoMethod, DOMAIN_CLASS, extractor);

        assertEquals(daoMethod.getName(), method.getName());
    }


    @Test
    public void determinesValidFieldsCorrectly() {

        QueryMethod method =
                new QueryMethod(daoMethod, DOMAIN_CLASS, extractor);

        assertTrue(method.isValidField("firstname"));
        assertTrue(method.isValidField("Firstname"));
        assertFalse(method.isValidField("address"));
    }


    @Test
    public void returnsQueryAnnotationIfAvailable() throws SecurityException,
            NoSuchMethodException {

        QueryMethod method =
                new QueryMethod(daoMethod, DOMAIN_CLASS, extractor);

        assertNull(method.getQueryAnnotation());

        Method daoMethod =
                UserDao.class.getMethod("findByHadesQuery", String.class);

        assertNotNull(new QueryMethod(daoMethod, DOMAIN_CLASS, extractor)
                .getQueryAnnotation());
    }


    @Test
    public void returnsCorrectDomainClassName() {

        QueryMethod method =
                new QueryMethod(daoMethod, DOMAIN_CLASS, extractor);

        assertEquals(DOMAIN_CLASS.getSimpleName(), method.getDomainClassName());
    }


    @Test
    public void returnsCorrectNumberOfParameters() {

        QueryMethod method =
                new QueryMethod(daoMethod, DOMAIN_CLASS, extractor);

        assertTrue(method.isCorrectNumberOfParameters(daoMethod
                .getParameterTypes().length));
    }


    @Test(expected = IllegalStateException.class)
    public void rejectsInvalidReturntypeOnPagebleFinder() {

        new QueryMethod(invalidReturnType, DOMAIN_CLASS, extractor);
    }


    @Test(expected = IllegalStateException.class)
    public void rejectsPageableAndSortInFinderMethod() {

        new QueryMethod(pageableAndSort, DOMAIN_CLASS, extractor);
    }


    @Test(expected = IllegalStateException.class)
    public void rejectsTwoPageableParameters() {

        new QueryMethod(pageableTwice, DOMAIN_CLASS, extractor);
    }


    @Test(expected = IllegalStateException.class)
    public void rejectsTwoSortableParameters() {

        new QueryMethod(sortableTwice, DOMAIN_CLASS, extractor);
    }


    @Test(expected = IllegalArgumentException.class)
    public void rejectsPageablesOnPersistenceProvidersNotExtractingQueries()
            throws Exception {

        Method method =
                UserDao.class.getMethod("findByFirstname", Pageable.class,
                        String.class);

        Query query = mock(Query.class);

        when(extractor.canExtractQuery()).thenReturn(false);

        new QueryMethod(method, DOMAIN_CLASS, extractor);
    }


    @Test
    public void recognizesModifyingMethod() throws Exception {

        QueryMethod method =
                new QueryMethod(modifyingMethod, DOMAIN_CLASS, extractor);
        assertTrue(method.isModifyingQuery());
    }


    @Test(expected = IllegalArgumentException.class)
    public void rejectsModifyingMethodWithPageable() throws Exception {

        Method method =
                InvalidDao.class.getMethod("updateMethod", String.class,
                        Pageable.class);

        new QueryMethod(method, DOMAIN_CLASS, extractor);
    }


    @Test(expected = IllegalArgumentException.class)
    public void rejectsModifyingMethodWithSort() throws Exception {

        Method method =
                InvalidDao.class.getMethod("updateMethod", String.class,
                        Sort.class);

        new QueryMethod(method, DOMAIN_CLASS, extractor);
    }


    @Test
    public void allowsPropertiesOfSubclasses() throws Exception {

        Method method =
                SubUserDao.class.getMethod("findByProperty", String.class);
        QueryMethod queryMethod =
                new QueryMethod(method, DOMAIN_CLASS, extractor);
        assertTrue(queryMethod.isValidField("property"));
    }


    @Test
    public void discoversHintsCorrectly() throws Exception {

        QueryMethod method =
                new QueryMethod(daoMethod, DOMAIN_CLASS, extractor);
        List<QueryHint> hints = method.getHints();

        assertNotNull(hints);
        assertThat(hints.get(0).name(), is("foo"));
        assertThat(hints.get(0).value(), is("bar"));
    }

    /**
     * Interface to define invalid DAO methods for testing.
     * 
     * @author Oliver Gierke
     */
    static interface InvalidDao {

        // Invalid return type
        User findByFirstname(String firstname, Pageable pageable);


        // Should not use Pageable *and* Sort
        Page<User> findByFirstname(String firstname, Pageable pageable,
                Sort sort);


        // Must not use two Pageables
        Page<User> findByFirstname(String firstname, Pageable first,
                Pageable second);


        // Must not use two Pageables
        Page<User> findByFirstname(String firstname, Sort first, Sort second);


        // Not backed by a named query or @Query annotation
        @Modifying
        void updateMethod(String firstname);


        // Modifying and Pageable is not allowed
        @Modifying
        Page<String> updateMethod(String firstname, Pageable pageable);


        // Modifying and Sort is not allowed
        @Modifying
        void updateMethod(String firstname, Sort sort);
    }

    /**
     * Sample domain class to simulate inheritance.
     * 
     * @author Oliver Gierke
     */
    @SuppressWarnings({ "unused", "serial" })
    private static class SubUser extends User {

        private String property;
    }

    /**
     * Interface handling the common superclass but allow queries for derived
     * entities.
     * 
     * @author Oliver Gierke
     */
    private static interface SubUserDao extends GenericDao<User, Integer> {

        SubUser findByProperty(String property);
    }
}

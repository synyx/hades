package org.synyx.hades.dao.query;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static org.synyx.hades.dao.query.QueryLookupStrategy.*;

import java.lang.reflect.Method;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.Before;
import org.junit.Test;
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
 * @author Oliver Gierke - gierke@synyx.de
 */
public class QueryMethodUnitTest {

    private static final Class<?> DOMAIN_CLASS = User.class;

    private Method daoMethod;
    private EntityManager em;
    private QueryExtractor extractor;

    private static final String METHOD_NAME = "findByFirstname";
    private Method invalidReturnType;
    private Method pageableAndSort;
    private Method pageableTwice;
    private Method sortableTwice;
    private Method modifyingMethod;
    private Method invalidModifyingMethod;


    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {

        daoMethod = UserDao.class.getMethod("findByLastname", String.class);
        em = createNiceMock(EntityManager.class);
        extractor = createNiceMock(QueryExtractor.class);

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
        invalidModifyingMethod =
                InvalidDao.class.getMethod("updateMethod", String.class);
    }


    @Test
    public void testname() throws Exception {

        QueryMethod method =
                new QueryMethod(daoMethod, DOMAIN_CLASS, em, extractor, CREATE);

        assertEquals("User.findByLastname", method.getNamedQueryName());
        assertTrue(method.isCollectionQuery());
        assertEquals("select x from User x where x.lastname = ?",
                new QueryCreator(method).constructQuery());
    }


    @Test(expected = IllegalArgumentException.class)
    public void preventsNullDaoMethod() throws Exception {

        new QueryMethod(null, DOMAIN_CLASS, em, extractor);
    }


    @Test(expected = IllegalArgumentException.class)
    public void preventsNullDomainClass() throws Exception {

        new QueryMethod(daoMethod, null, em, extractor, null);
    }


    @Test(expected = IllegalArgumentException.class)
    public void preventsNullEntityManager() throws Exception {

        new QueryMethod(daoMethod, DOMAIN_CLASS, null, extractor);
    }


    @Test(expected = IllegalArgumentException.class)
    public void preventsNullQueryExtractor() throws Exception {

        new QueryMethod(daoMethod, DOMAIN_CLASS, em, null);
    }


    @Test
    public void returnsCorrectName() {

        QueryMethod method =
                new QueryMethod(daoMethod, DOMAIN_CLASS, em, extractor, CREATE);

        assertEquals(daoMethod.getName(), method.getName());
    }


    @Test
    public void determinesValidFieldsCorrectly() {

        QueryMethod method =
                new QueryMethod(daoMethod, DOMAIN_CLASS, em, extractor, CREATE);

        assertTrue(method.isValidField("firstname"));
        assertTrue(method.isValidField("Firstname"));
        assertFalse(method.isValidField("address"));
    }


    @Test
    public void returnsQueryAnnotationIfAvailable() throws SecurityException,
            NoSuchMethodException {

        QueryMethod method =
                new QueryMethod(daoMethod, DOMAIN_CLASS, em, extractor, CREATE);

        assertNull(method.getQueryAnnotation());

        Method daoMethod =
                UserDao.class.getMethod("findByHadesQuery", String.class);

        assertNotNull(new QueryMethod(daoMethod, DOMAIN_CLASS, em, extractor)
                .getQueryAnnotation());
    }


    @Test
    public void returnsCorrectDomainClassName() {

        QueryMethod method =
                new QueryMethod(daoMethod, DOMAIN_CLASS, em, extractor, CREATE);

        assertEquals(DOMAIN_CLASS.getSimpleName(), method.getDomainClassName());
    }


    @Test
    public void returnsCorrectNumberOfParameters() {

        QueryMethod method =
                new QueryMethod(daoMethod, DOMAIN_CLASS, em, extractor, CREATE);

        assertTrue(method.isCorrectNumberOfParameters(daoMethod
                .getParameterTypes().length));
    }


    @Test(expected = IllegalStateException.class)
    public void rejectsInvalidReturntypeOnPagebleFinder() {

        new QueryMethod(invalidReturnType, DOMAIN_CLASS, em, extractor);
    }


    @Test(expected = IllegalStateException.class)
    public void rejectsPageableAndSortInFinderMethod() {

        new QueryMethod(pageableAndSort, DOMAIN_CLASS, em, extractor);
    }


    @Test(expected = IllegalStateException.class)
    public void rejectsTwoPageableParameters() {

        new QueryMethod(pageableTwice, DOMAIN_CLASS, em, extractor);
    }


    @Test(expected = IllegalStateException.class)
    public void rejectsTwoSortableParameters() {

        new QueryMethod(sortableTwice, DOMAIN_CLASS, em, extractor);
    }


    @Test(expected = IllegalStateException.class)
    public void rejectsModifyingMethodWithoutBacking() {

        expect(em.createNamedQuery((String) anyObject())).andThrow(
                new IllegalArgumentException());
        replay(em);

        new QueryMethod(invalidModifyingMethod, DOMAIN_CLASS, em, extractor,
                QueryLookupStrategy.USE_DECLARED_QUERY);
    }


    @Test(expected = QueryCreationException.class)
    public void rejectsPageablesOnPersistenceProvidersNotExtractingQueries()
            throws Exception {

        Method method =
                UserDao.class.getMethod("findByFirstname", Pageable.class,
                        String.class);

        Query query = createNiceMock(Query.class);

        expect(em.createNamedQuery((String) anyObject())).andReturn(query)
                .anyTimes();
        expect(extractor.canExtractQuery()).andReturn(false).anyTimes();
        replay(em, extractor, query);

        new QueryMethod(method, DOMAIN_CLASS, em, extractor);
    }


    @Test
    public void recognizesModifyingMettod() throws Exception {

        QueryMethod method =
                new QueryMethod(modifyingMethod, DOMAIN_CLASS, em, extractor);
        assertTrue(method.isModifyingQuery());
    }


    @Test
    public void allowsPropertiesOfSubclasses() throws Exception {

        Method method =
                SubUserDao.class.getMethod("findByProperty", String.class);
        QueryMethod queryMethod =
                new QueryMethod(method, DOMAIN_CLASS, em, extractor);
        assertTrue(queryMethod.isValidField("property"));
    }

    /**
     * Interface to define invalid DAO methods for testing.
     * 
     * @author Oliver Gierke - gierke@synyx.de
     */
    private static interface InvalidDao {

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
    }

    /**
     * Sample domain class to simulate inheritance.
     * 
     * @author Oliver Gierke - gierke@synyx.de
     */
    @SuppressWarnings( { "unused", "serial" })
    private static class SubUser extends User {

        private String property;
    }

    /**
     * Interface handling the common superclass but allow queries for derived
     * entities.
     * 
     * @author Oliver Gierke - gierke@synyx.de
     */
    private static interface SubUserDao extends GenericDao<User, Integer> {

        SubUser findByProperty(String property);
    }
}

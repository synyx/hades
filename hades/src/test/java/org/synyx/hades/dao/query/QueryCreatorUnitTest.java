package org.synyx.hades.dao.query;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.Date;

import javax.persistence.Embeddable;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.synyx.hades.dao.query.QueryMethodUnitTest.InvalidDao;
import org.synyx.hades.domain.User;


/**
 * Unit test for {@link QueryCreator}.
 * 
 * @author Oliver Gierke
 */
@RunWith(MockitoJUnitRunner.class)
public class QueryCreatorUnitTest {

    private Method method;

    @Mock
    private QueryExtractor extractor;


    @Before
    public void setup() throws SecurityException, NoSuchMethodException {

        method =
                QueryCreatorUnitTest.class.getMethod(
                        "findByFirstnameAndMethod", String.class);
    }


    @Test(expected = QueryCreationException.class)
    public void rejectsInvalidProperty() throws Exception {

        QueryMethod finderMethod =
                new QueryMethod(method, User.class, extractor);

        new QueryCreator(finderMethod).constructQuery();
    }


    @Test
    public void splitsKeywordsCorrectly() throws SecurityException,
            NoSuchMethodException {

        method =
                QueryCreatorUnitTest.class.getMethod(
                        "findByNameOrOrganization", String.class, String.class);

        assertCreatesQueryForMethod("where x.name = ?1 or x.organization = ?2",
                method);
    }


    /**
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @see #265
     * @throws Exception
     */
    @Test
    public void createsQueryWithEmbeddableCorrectly() throws SecurityException,
            NoSuchMethodException {

        method =
                getClass()
                        .getMethod("findByEmbeddable", SampleEmbeddable.class);

        assertCreatesQueryForMethod("where x.embeddable = ?1", method);
    }


    @Test
    public void createsQueryWithBetweenKeywordCorrectly() throws Exception {

        method =
                getClass().getMethod("findByStartDateBetweenAndName",
                        Date.class, Date.class, String.class);

        assertCreatesQueryForMethod(
                "where x.startDate between ?1 and ?2 and x.name = ?3", method);
    }


    @Test
    public void createsQueryWithLessThanKeywordCorrectly() throws Exception {

        method = getClass().getMethod("findByAgeLessThan", int.class);

        assertCreatesQueryForMethod("where x.age < ?1", method);
    }


    @Test
    public void createsQueryWithGreaterThanKeywordCorrectly() throws Exception {

        method = getClass().getMethod("findByAgeGreaterThan", int.class);

        assertCreatesQueryForMethod("where x.age > ?1", method);
    }


    @Test(expected = IllegalArgumentException.class)
    public void rejectsModifyingMethodWithoutBacking()
            throws SecurityException, NoSuchMethodException {

        Method invalidModifyingMethod =
                InvalidDao.class.getMethod("updateMethod", String.class);

        QueryMethod method =
                new QueryMethod(invalidModifyingMethod,
                        QueryMethodUnitTest.DOMAIN_CLASS, extractor);

        new QueryCreator(method);
    }


    /**
     * Asserts that the query created for the given {@link Method} results in a
     * query ending with the given {@link String}.
     * 
     * @param queryEnd
     * @param method
     */
    private void assertCreatesQueryForMethod(String queryEnd, Method method) {

        QueryMethod queryMethod =
                new QueryMethod(method, method.getReturnType(), extractor);

        String result = new QueryCreator(queryMethod).constructQuery();
        assertTrue(result.endsWith(queryEnd));
    }


    /**
     * Sample method to test failing query creation.
     * 
     * @param firstname
     * @return
     */
    public User findByFirstnameAndMethod(String firstname) {

        return null;
    }


    /**
     * A method to check that query keyowrds are considered correctly. The
     * {@link QueryCreator} must not detect the {@code Or} in {@code
     * Organization} as keyword.
     * 
     * @param name
     * @param organization
     * @return
     */
    public SampleEntity findByNameOrOrganization(String name,
            String organization) {

        return null;
    }


    /**
     * Sample method to create a finder query for that references an
     * {@link Embeddable}.
     * 
     * @see #265
     * @param embeddable
     * @return
     */
    public SampleEntity findByEmbeddable(SampleEmbeddable embeddable) {

        return null;
    }


    public SampleEntity findByStartDateBetweenAndName(Date first, Date second,
            String name) {

        return null;
    }


    public SampleEntity findByAgeLessThan(int age) {

        return null;
    }


    public SampleEntity findByAgeGreaterThan(int age) {

        return null;
    }

    /**
     * Sample class for keyword split check.
     * 
     * @author Oliver Gierke
     */
    @SuppressWarnings("unused")
    static class SampleEntity {

        private String organization;
        private String name;

        private Date startDate;
        private int age;

        private SampleEmbeddable embeddable;
    }

    @Embeddable
    @SuppressWarnings("unused")
    static class SampleEmbeddable {

        private String foo;
        private String bar;
    }
}

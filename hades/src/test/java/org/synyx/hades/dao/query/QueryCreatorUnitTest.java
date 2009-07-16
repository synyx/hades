package org.synyx.hades.dao.query;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.lang.reflect.Method;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.synyx.hades.domain.User;


/**
 * Unit test for {@link QueryCreator}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class QueryCreatorUnitTest {

    private Method method;
    private EntityManager em;


    @Before
    public void setup() throws SecurityException, NoSuchMethodException {

        method =
                QueryCreatorUnitTest.class.getMethod(
                        "findByFirstnameAndMethod", String.class);

        em = createNiceMock(EntityManager.class);
    }


    @Test(expected = QueryCreationException.class)
    public void rejectsInvalidProperty() throws Exception {

        FinderMethod finderMethod =
                new FinderMethod(method, User.class, em, QueryLookupStrategy.CREATE_IF_NOT_FOUND);

        new QueryCreator(finderMethod).constructQuery();
    }


    @Test
    public void splitsKeywordsCorrectly() throws SecurityException,
            NoSuchMethodException {

        method =
                QueryCreatorUnitTest.class.getMethod(
                        "findByNameOrOrganization", String.class, String.class);

        FinderMethod finderMethod =
                new FinderMethod(method, SampleEntity.class, em, QueryLookupStrategy.CREATE_IF_NOT_FOUND);

        String query = new QueryCreator(finderMethod).constructQuery();
        assertTrue(query.endsWith("where x.name = ? or x.organization = ?"));
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
     * Sample class for keyword split check.
     * 
     * @author Oliver Gierke - gierke@synyx.de
     */
    @SuppressWarnings("unused")
    private class SampleEntity {

        private String organization;
        private String name;
    }
}

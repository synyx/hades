package org.synyx.hades.dao.query;

import static org.easymock.EasyMock.*;

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
                new FinderMethod(method, "findBy", User.class, em,
                        QueryLookupStrategy.CREATE_IF_NOT_FOUND);

        new QueryCreator(finderMethod).constructQuery();
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
}

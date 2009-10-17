package org.synyx.hades.dao.query;

import static org.easymock.EasyMock.*;

import java.lang.reflect.Method;

import javax.persistence.Query;

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
    private Method useIndexedParameters;

    private Query query;


    @Before
    public void setUp() throws SecurityException, NoSuchMethodException {

        valid = SampleDao.class.getMethod("valid", String.class);

        useIndexedParameters =
                SampleDao.class.getMethod("useIndexedParameters", String.class);

        query = createNiceMock(Query.class);
    }


    @Test
    public void usesParameterNameIfAnnotated() throws Exception {

        expect(query.setParameter(eq("username"), anyObject()))
                .andReturn(query).once();
        executeAndVerifyMethod(valid);
    }


    @Test
    public void usesIndexedParametersIfNoParamAnnotationPresent()
            throws Exception {

        expect(query.setParameter(eq(1), anyObject())).andReturn(query).once();
        executeAndVerifyMethod(useIndexedParameters);
    }


    @Test
    public void checksValidMethodCorrectly() throws Exception {

        Method validMethod = SampleDao.class.getMethod("valid", String.class);
        Method validWithPageable =
                SampleDao.class.getMethod("validWithPageable", String.class,
                        Pageable.class);
        Method validWithSort =
                SampleDao.class.getMethod("validWithSort", String.class,
                        Sort.class);

        new Parameters(validMethod);
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


    private void executeAndVerifyMethod(Method method) {

        replay(query);
        new Parameters(method, "foo").bind(query);
        verify(query);
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

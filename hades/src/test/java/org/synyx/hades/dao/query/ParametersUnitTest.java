package org.synyx.hades.dao.query;

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

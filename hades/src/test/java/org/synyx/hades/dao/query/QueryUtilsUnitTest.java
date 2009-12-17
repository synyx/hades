package org.synyx.hades.dao.query;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.hamcrest.Matcher;
import org.junit.Test;


/**
 * Unit test for {@link QueryUtils}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class QueryUtilsUnitTest {

    private static final String QUERY = "select u from User u";
    private static final String SIMPLE_QUERY = "from User u";
    private static final String COUNT_QUERY = "select count(*) from User u";

    private static final String QUERY_WITH_AS =
            "select u from User as u where u.username = ?";

    private static final Matcher<String> IS_U = is("u");


    @Test
    public void createsCountQueryCorrectly() throws Exception {

        assertThat(QueryUtils.createCountQueryFor(QUERY), is(COUNT_QUERY));
    }


    @Test
    public void allowsShortJpaSyntax() throws Exception {

        assertThat(QueryUtils.createCountQueryFor(SIMPLE_QUERY),
                is(COUNT_QUERY));
    }


    @Test
    public void detectsAliasCorrectly() throws Exception {

        assertThat(QueryUtils.detectAlias(QUERY), IS_U);
        assertThat(QueryUtils.detectAlias(SIMPLE_QUERY), IS_U);
        assertThat(QueryUtils.detectAlias(COUNT_QUERY), IS_U);
        assertThat(QueryUtils.detectAlias(QUERY_WITH_AS), IS_U);
    }
}

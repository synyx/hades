package org.synyx.hades.dao.query;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.synyx.hades.dao.query.QueryUtils.*;

import org.hamcrest.Matcher;
import org.junit.Test;


/**
 * Unit test for {@link QueryUtils}.
 * 
 * @author Oliver Gierke
 */
public class QueryUtilsUnitTest {

    private static final String QUERY = "select u from User u";
    private static final String SIMPLE_QUERY = "from User u";
    private static final String COUNT_QUERY = "select count(u) from User u";

    private static final String QUERY_WITH_AS =
            "select u from User as u where u.username = ?";

    private static final Matcher<String> IS_U = is("u");


    @Test
    public void createsCountQueryCorrectly() throws Exception {

        assertThat(createCountQueryFor(QUERY), is(COUNT_QUERY));

    }


    /**
     * @see #303
     */
    @Test
    public void createsCountQueriesCorrectlyForCapitalLetterJPQL() {

        assertThat(createCountQueryFor("FROM User u WHERE u.foo.bar = ?"),
                is("select count(u) FROM User u WHERE u.foo.bar = ?"));

        assertThat(
                createCountQueryFor("SELECT u FROM User u where u.foo.bar = ?"),
                is("select count(u) FROM User u where u.foo.bar = ?"));
    }


    /**
     * @see #351
     */
    @Test
    public void createsCountQueryForDistinctQueries() throws Exception {

        assertThat(
                createCountQueryFor("select distinct u from User u where u.foo = ?"),
                is("select count(distinct u) from User u where u.foo = ?"));
    }


    @Test
    public void createsCountQueryForConstructorQueries() throws Exception {

        assertThat(
                createCountQueryFor("select distinct new User(u.name) from User u where u.foo = ?"),
                is("select count(distinct u) from User u where u.foo = ?"));
    }


    @Test
    public void allowsShortJpaSyntax() throws Exception {

        assertThat(createCountQueryFor(SIMPLE_QUERY), is(COUNT_QUERY));
    }


    @Test
    public void detectsAliasCorrectly() throws Exception {

        assertThat(detectAlias(QUERY), IS_U);
        assertThat(detectAlias(SIMPLE_QUERY), IS_U);
        assertThat(detectAlias(COUNT_QUERY), IS_U);
        assertThat(detectAlias(QUERY_WITH_AS), IS_U);
        assertThat(detectAlias("SELECT FROM USER U"), is("U"));
        assertThat(detectAlias("select u from  User u"), IS_U);
        assertThat(detectAlias("select u from  com.acme.User u"), IS_U);
    }
}

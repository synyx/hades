package org.synyx.hades.dao.query;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

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


    @Test
    public void createsCountQueryCorrectly() throws Exception {

        assertThat(QueryUtils.createCountQueryFor(QUERY), is(COUNT_QUERY));
    }


    @Test
    public void allowsShortJpaSyntax() throws Exception {

        assertThat(QueryUtils.createCountQueryFor(SIMPLE_QUERY),
                is(COUNT_QUERY));
    }
}

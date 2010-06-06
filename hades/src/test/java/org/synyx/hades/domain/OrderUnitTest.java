package org.synyx.hades.domain;

import static org.junit.Assert.*;

import org.junit.Test;


/**
 * Unit test for {@link Order}.
 * 
 * @author Oliver Gierke
 */
public class OrderUnitTest {

    @Test
    public void jpaValueMapping() throws Exception {

        assertEquals(Order.ASCENDING, Order.fromJpaValue("asc"));
        assertEquals(Order.DESCENDING, Order.fromJpaValue("desc"));
    }


    @Test(expected = IllegalArgumentException.class)
    public void rejectsInvalidString() throws Exception {

        Order.fromJpaValue("foo");
    }
}

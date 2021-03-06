/*
 * Copyright 2008-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.synyx.hades.dao.query;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.synyx.hades.dao.query.QueryCreator.OrderBySource;


/**
 * Unit test for {@link OrderBySource}.
 * 
 * @author Oliver Gierke
 */
public class OrderBySourceUnitTest {

    @Test
    public void createsOrderBySourceCorrectly() throws Exception {

        OrderBySource orderBySource =
                new QueryCreator.OrderBySource("UsernameDesc");
        assertThat(orderBySource.getClause(), is("order by x.username desc"));
    }


    @Test
    public void handlesCamelCasePropertyCorrecty() throws Exception {

        assertThat(new OrderBySource("LastnameUsernameDesc").getClause(),
                is("order by x.lastnameUsername desc"));
    }


    @Test
    public void handlesMultipleDirectionsCorrectly() throws Exception {

        OrderBySource orderBySource =
                new OrderBySource("LastnameAscUsernameDesc");
        assertThat(orderBySource.getClause(),
                is("order by x.lastname asc, x.username desc"));
    }


    @Test(expected = IllegalArgumentException.class)
    public void rejectsMissingProperty() throws Exception {

        new OrderBySource("Desc");
    }
}

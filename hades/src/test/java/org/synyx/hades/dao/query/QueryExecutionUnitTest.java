/*
 * Copyright 2008-2010 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.synyx.hades.dao.query;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.synyx.hades.dao.query.QueryExecution.ModifyingExecution;


/**
 * Unit test for {@link QueryExecution}.
 * 
 * @author Oliver Gierke
 */
@RunWith(MockitoJUnitRunner.class)
public class QueryExecutionUnitTest {

    @Mock
    EntityManager em;
    @Mock
    AbstractHadesQuery hadesQuery;
    @Mock
    ParameterBinder binder;
    @Mock
    Query query;


    @Test(expected = IllegalArgumentException.class)
    public void rejectsNullQuery() throws Exception {

        new StubQueryExecution().execute(null, binder);
    }


    @Test(expected = IllegalArgumentException.class)
    public void rejectsNullBinder() throws Exception {

        new StubQueryExecution().execute(hadesQuery, null);
    }


    @Test
    public void modifyingExecutionClearsEntityManagerIfSet() throws Exception {

        Query param = any();
        when(binder.bind(param)).thenReturn(query);
        when(query.executeUpdate()).thenReturn(0);

        ModifyingExecution execution =
                new QueryExecution.ModifyingExecution(em);
        execution.execute(hadesQuery, binder);

        verify(em, times(1)).clear();
    }

    static class StubQueryExecution extends QueryExecution {

        @Override
        protected Object doExecute(AbstractHadesQuery query,
                ParameterBinder binder) {

            return null;
        }
    }
}

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
package org.synyx.hades.dao.config;

import static org.junit.Assert.*;
import static org.springframework.test.util.ReflectionTestUtils.*;
import static org.synyx.hades.dao.query.QueryLookupStrategy.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.synyx.hades.dao.orm.GenericDaoFactoryBean;
import org.synyx.hades.dao.query.QueryLookupStrategy;


/**
 * Integration test for XML configuration of {@link QueryLookupStrategy}s.
 * 
 * @author Oliver Gierke
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:namespace/lookup-strategies-context.xml")
public class QueryLookupStrategyIntegrationTest {

    @Autowired
    private ApplicationContext context;


    /**
     * Assert that {@link QueryLookupStrategy#USE_DECLARED_QUERY} is being set
     * on the factory if configured.
     */
    @Test
    public void assertUseDeclaredQuery() {

        GenericDaoFactoryBean<?> factory =
                context.getBean("&roleDao", GenericDaoFactoryBean.class);

        assertEquals(USE_DECLARED_QUERY, getField(factory,
                "queryLookupStrategy"));
    }
}

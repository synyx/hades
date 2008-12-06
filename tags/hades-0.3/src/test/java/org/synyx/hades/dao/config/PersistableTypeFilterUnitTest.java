/*
 * Copyright 2002-2008 the original author or authors.
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

import java.util.Set;

import junit.framework.TestCase;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.synyx.hades.dao.config.DaoConfigDefinitionParser.PersistableTypeFilter;


/**
 * Unit test for {@code DaoConfigDefinitionParser.PersistableTypeFilter}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class PersistableTypeFilterUnitTest extends TestCase {

    private PersistableTypeFilter filter;


    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {

        filter = new DaoConfigDefinitionParser.PersistableTypeFilter();
    }


    /**
     * Tests that the filter finds the 3 persistable annotated domain classes.
     * 
     * @throws Exception
     */
    public void testFindsPersistableTypes() throws Exception {

        // Create scanner and apply filter
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(
                false);
        provider.addIncludeFilter(filter);

        Set<BeanDefinition> beanDefinitions = provider
                .findCandidateComponents("org.synyx.hades.domain");

        assertEquals(3, beanDefinitions.size());
    }
}

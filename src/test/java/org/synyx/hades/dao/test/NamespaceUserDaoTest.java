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

package org.synyx.hades.dao.test;

import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;


/**
 * Use namespace context to run tests.
 * 
 * @author Eberhard Wolff
 * @author Oliver Gierke - gierke@synyx.de
 */
public class NamespaceUserDaoTest extends AbstractUserDaoTest {

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.test.AbstractSingleSpringContextTests#getConfigLocations()
     */
    @Override
    protected String[] getConfigLocations() {

        setAutowireMode(AUTOWIRE_BY_NAME);
        return new String[] { "namespace-applicationContext.xml" };
    }


    /**
     * Tests, that PostProcessor beans are available as expected.
     */
    public void testCreationOfPostProcessors() {

        getApplicationContext().getBean(
                PersistenceAnnotationBeanPostProcessor.class.getName());

        getApplicationContext().getBean(
                PersistenceExceptionTranslationPostProcessor.class.getName());
    }
}

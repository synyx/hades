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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.springframework.test.context.ContextConfiguration;


/**
 * Use namespace context to run tests. Checks for existence of required
 * PostProcessors, too.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 * @author Eberhard Wolff
 */
@ContextConfiguration(locations = "classpath:namespace-applicationContext.xml", inheritLocations = false)
public class NamespaceUserDaoTest extends UserDaoTest implements
        BeanFactoryAware {

    private BeanFactory beanFactory;


    /**
     * Tests, that PostProcessor beans are available as expected.
     */
    public void testCreationOfPostProcessors() {

        beanFactory.getBean(PersistenceAnnotationBeanPostProcessor.class
                .getName());

        beanFactory.getBean(PersistenceExceptionTranslationPostProcessor.class
                .getName());
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org
     * .springframework.beans.factory.BeanFactory)
     */
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {

        this.beanFactory = beanFactory;
    }
}

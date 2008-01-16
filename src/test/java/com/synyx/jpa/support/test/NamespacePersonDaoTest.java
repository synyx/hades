package com.synyx.jpa.support.test;

import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;


/**
 * Use namespace context to run tests.
 * 
 * @author Eberhard Wolff
 * @author Oliver Gierke
 */
public class NamespacePersonDaoTest extends AbstractPersonDaoTest {

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.test.AbstractSingleSpringContextTests#getConfigLocations()
     */
    @Override
    protected String[] getConfigLocations() {

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

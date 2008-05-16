package org.synyx.hades.dao.config;

import java.util.Set;

import junit.framework.TestCase;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.synyx.hades.dao.config.DaoConfigDefinitionParser.PersistableTypeFilter;


/**
 * Unit test for {@link DaoConfigDefinitionParser.PersistableTypeFilter}.
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

        assertEquals(2, beanDefinitions.size());
    }
}

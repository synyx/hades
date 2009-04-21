package org.synyx.hades.dao.config;

import static org.junit.Assert.*;
import static org.synyx.hades.dao.query.QueryLookupStrategy.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.synyx.hades.dao.UserDao;
import org.synyx.hades.dao.orm.GenericDaoFactoryBean;
import org.synyx.hades.dao.query.QueryLookupStrategy;


/**
 * Integration test for XML configuration of {@link QueryLookupStrategy}s.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:namespace/lookup-strategies-context.xml")
public class QueryLookupStrategyIntegrationtest {

    @Autowired
    private GenericDaoFactoryBean<UserDao> factory;


    /**
     * Assert that {@link QueryLookupStrategy#USE_DECLARED_QUERY} is being set
     * on the factory if configured.
     */
    @Test
    public void assertUseDeclaredQuery() {

        assertEquals(USE_DECLARED_QUERY, factory.getQueryLookupStrategy());
    }
}

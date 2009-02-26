package org.synyx.hades.dao.config;

import org.springframework.test.context.ContextConfiguration;


/**
 * Integration test for DAO namespace configuration.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
@ContextConfiguration(locations = "classpath:namespace-applicationContext.xml")
public class DaoConfigIntegrationTest extends AbstractDaoConfigIntegrationTest {
}

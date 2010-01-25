/*
 * Copyright 2008-2009 the original author or authors.
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

package org.synyx.hades.dao.test;

import org.junit.Ignore;
import org.springframework.test.context.ContextConfiguration;


/**
 * Integration test for EclipseLink implementation of {@code ExtendedGenericDao}
 * . Not implemented to be run yet, due to configuration problems. I don't want
 * to break the test suite right now.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
@ContextConfiguration(locations = { "classpath:eclipselink.xml" })
@Ignore
public class GenericEclipseLinkDaoIntegrationTest extends
        ExtendedGenericDaoIntegrationTest {

}

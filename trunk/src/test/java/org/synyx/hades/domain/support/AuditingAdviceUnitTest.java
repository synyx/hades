/*
 * Copyright 2002-2008 the original author or authors.
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

package org.synyx.hades.domain.support;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.synyx.hades.domain.AuditableUser;
import org.synyx.hades.domain.support.AuditingAdvice;
import org.synyx.hades.domain.support.AuditorAware;


/**
 * Unit test for {@code AuditionAdvice}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
@SuppressWarnings("unchecked")
public class AuditingAdviceUnitTest extends TestCase {

    private AuditingAdvice auditionAdvice;
    private AuditorAware<AuditableUser, Long> auditorAware;

    private AuditableUser user;


    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {

        auditionAdvice = new AuditingAdvice();

        user = new AuditableUser();

        auditorAware = EasyMock.createNiceMock(AuditorAware.class);
        EasyMock.expect(auditorAware.getCurrentAuditor()).andReturn(user)
                .once();
    }


    /**
     * Checks that the advice does not set auditor on the target entity if no
     * {@code AuditorAware} was configured.
     * 
     * @throws Exception
     */
    public void testDoesNotSetAuditorIfNotConfigured() throws Exception {

        auditionAdvice.touch(user);

        assertNotNull(user.getCreatedDate());
        assertNotNull(user.getLastModifiedDate());

        assertNull(user.getCreatedBy());
        assertNull(user.getLastModifiedBy());
    }


    /**
     * Checks that the advice sets the auditor on the target entity if an
     * {@code AuditorAware} was configured.
     * 
     * @throws Exception
     */
    public void testSetsAuditorIfConfigured() {

        auditionAdvice.setAuditorAware(auditorAware);
        EasyMock.replay(auditorAware);

        auditionAdvice.touch(user);

        assertNotNull(user.getCreatedDate());
        assertNotNull(user.getLastModifiedDate());

        assertNotNull(user.getCreatedBy());
        assertNotNull(user.getLastModifiedBy());

        EasyMock.verify(auditorAware);
    }


    /**
     * Checks that the advice does not set modification information on creation
     * if the falg is set to {@code false}.
     * 
     * @throws Exception
     */
    public void testHonoursModifiedOnCreationFlag() throws Exception {

        auditionAdvice.setAuditorAware(auditorAware);
        EasyMock.replay(auditorAware);

        auditionAdvice.setModifyOnCreation(false);
        auditionAdvice.touch(user);

        assertNotNull(user.getCreatedDate());
        assertNotNull(user.getCreatedBy());

        assertNull(user.getLastModifiedBy());
        assertNull(user.getLastModifiedDate());

        EasyMock.verify(auditorAware);
    }


    /**
     * Tests that the advice only sets modification data if a not-new entity is
     * handled.
     */
    public void testOnlySetsModificationDataOnNotNewEntities() {

        user.setId(1L);

        auditionAdvice.setAuditorAware(auditorAware);
        EasyMock.replay(auditorAware);

        auditionAdvice.touch(user);

        assertNull(user.getCreatedBy());
        assertNull(user.getCreatedDate());

        assertNotNull(user.getLastModifiedBy());
        assertNotNull(user.getLastModifiedDate());

        EasyMock.verify(auditorAware);
    }
}

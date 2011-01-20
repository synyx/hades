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
package org.synyx.hades.domain.auditing;

import org.springframework.util.Assert;
import org.synyx.hades.dao.AuditableUserDao;


/**
 * Stub implementation for {@link AuditorAware}. Returns {@literal null} for the
 * current auditor.
 * 
 * @author Oliver Gierke
 */
public class AuditorAwareStub implements AuditorAware<AuditableUser> {

    private final AuditableUserDao userDao;
    private AuditableUser user;


    public AuditorAwareStub(AuditableUserDao userDao) {

        Assert.notNull(userDao);
        this.userDao = userDao;
    }


    /**
     * @param user the user to set
     */
    public void setUser(AuditableUser user) {

        this.user = user;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.domain.auditing.AuditorAware#getCurrentAuditor()
     */
    public AuditableUser getCurrentAuditor() {

        return user;
    }
}

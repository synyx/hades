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

package org.synyx.hades.domain;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;

import org.synyx.hades.domain.support.AbstractAuditable;


/**
 * Sample auditable user to demonstrate working with
 * {@code AbstractAuditableEntity}. No declaration of an ID is necessary.
 * Furthermore alle auditioning information has to be declared explicitly.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
@Entity
@NamedQuery(name = "AuditableUser.findByFirstname", query = "SELECT u FROM AuditableUser u WHERE u.firstname = ?1")
public class AuditableUser extends AbstractAuditable<AuditableUser, Long> {

    private static final long serialVersionUID = 7409344446795693011L;

    private String firstname;


    /**
     * Returns the firstname.
     * 
     * @return the firstname
     */
    public String getFirstname() {

        return firstname;
    }


    /**
     * Sets the firstname.
     * 
     * @param firstname the firstname to set
     */
    public void setFirstname(final String firstname) {

        this.firstname = firstname;
    }
}

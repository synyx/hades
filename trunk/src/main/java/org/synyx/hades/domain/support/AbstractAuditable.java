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

import java.io.Serializable;
import java.util.Date;

import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.synyx.hades.domain.AbstractPersistable;
import org.synyx.hades.domain.Persistable;


/**
 * Abstract base class for auditable entities. Stores the audition values in
 * persistent fields.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 * @param <U> the auditing type. Typically some kind of user.
 * @param <PK> the type of the auditing type's idenifier
 */
@MappedSuperclass
public abstract class AbstractAuditable<U extends Persistable<PK>, PK extends Serializable>
        extends AbstractPersistable<PK> implements Auditable<U, PK> {

    private static final long serialVersionUID = 141481953116476081L;

    @OneToOne
    private U createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @OneToOne
    private U lastModifiedBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.hades.domain.Auditable#getCreatedBy()
     */
    public U getCreatedBy() {

        return createdBy;
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.synyx.hades.hades.domain.Auditable#setCreatedBy(org.synyx.hades.hades
     * .domain.Identifyable)
     */
    public void setCreatedBy(final U createdBy) {

        this.createdBy = createdBy;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.hades.domain.Auditable#getCreatedDate()
     */
    public Date getCreatedDate() {

        return createdDate;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.hades.domain.Auditable#setCreated(java.util.Date)
     */
    public void setCreated(final Date createdDate) {

        this.createdDate = createdDate;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.hades.domain.Auditable#getLastModifiedBy()
     */
    public U getLastModifiedBy() {

        return lastModifiedBy;
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.synyx.hades.hades.domain.Auditable#setLastModifiedBy(org.synyx.hades
     * .hades.domain.Identifyable)
     */
    public void setLastModifiedBy(final U lastModifiedBy) {

        this.lastModifiedBy = lastModifiedBy;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.hades.domain.Auditable#getLastModifiedDate()
     */
    public Date getLastModifiedDate() {

        return lastModifiedDate;
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.synyx.hades.hades.domain.Auditable#setLastModified(java.util.Date)
     */
    public void setLastModified(final Date lastModifiedDate) {

        this.lastModifiedDate = lastModifiedDate;
    }
}

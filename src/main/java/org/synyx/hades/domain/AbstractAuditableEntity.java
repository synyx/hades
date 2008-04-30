package org.synyx.hades.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * Abstract base class for auditable entities. Stores the audition values in
 * persistent fields
 * 
 * @author Oliver Gierke
 * @version $Id$
 */
@MappedSuperclass
public abstract class AbstractAuditableEntity<U extends Entity<PK>, PK extends Serializable>
        extends AbstractEntity<PK> implements Auditable<U, PK> {

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
     * @see org.synyx.hades.hades.domain.Auditable#setCreatedBy(org.synyx.hades.hades.domain.Identifyable)
     */
    public void setCreatedBy(U createdBy) {

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
    public void setCreated(Date createdDate) {

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
     * @see org.synyx.hades.hades.domain.Auditable#setLastModifiedBy(org.synyx.hades.hades.domain.Identifyable)
     */
    public void setLastModifiedBy(U lastModifiedBy) {

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
     * @see org.synyx.hades.hades.domain.Auditable#setLastModified(java.util.Date)
     */
    public void setLastModified(Date lastModifiedDate) {

        this.lastModifiedDate = lastModifiedDate;
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        return "Entity of type " + this.getClass().getName() + " with id: "
                + getId();
    }
}

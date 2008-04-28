package org.synyx.domain;

import java.io.Serializable;
import java.util.Date;


/**
 * Interface for auditable entities. Allows storing and retrieving creation and
 * modification information. The changing instance (typically some user) is to
 * be defined by a generics definition.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public interface Auditable<U extends Identifyable<PK>, PK extends Serializable>
        extends Identifyable<PK> {

    /**
     * Returns the user who created this entity.
     * 
     * @return the createdBy
     */
    public abstract U getCreatedBy();


    /**
     * Sets the user who created this entity.
     * 
     * @param createdBy the creating entity to set
     */
    public abstract void setCreatedBy(U createdBy);


    /**
     * Returns the creation date of the entity.
     * 
     * @return the createdDate
     */
    public abstract Date getCreatedDate();


    /**
     * Sets the creation date of the entity.
     * 
     * @param creationDate the creation date to set
     */
    public abstract void setCreated(Date creationDate);


    /**
     * Returns the user who modified the entity lastly.
     * 
     * @return the lastModifiedBy
     */
    public abstract U getLastModifiedBy();


    /**
     * Sets the user who modified the entity lastly.
     * 
     * @param lastModifiedBy the last modifying entity to set
     */
    public abstract void setLastModifiedBy(U lastModifiedBy);


    /**
     * Returns the date of the last modification.
     * 
     * @return the lastModifiedDate
     */
    public abstract Date getLastModifiedDate();


    /**
     * Sets the date of the last modification.
     * 
     * @param lastModifiedDate the date of the last modification to set
     */
    public abstract void setLastModified(Date lastModifiedDate);
}

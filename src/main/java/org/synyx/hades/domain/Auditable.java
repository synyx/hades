package org.synyx.hades.domain;

import java.io.Serializable;
import java.util.Date;


/**
 * Interface for auditable entities. Allows storing and retrieving creation and
 * modification information. The changing instance (typically some user) is to
 * be defined by a generics definition.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 * @param <U> the auditing type. Typically some kind of user.
 * @param <PK> the type of the auditing type's idenifier
 */
public interface Auditable<U extends Persistable<PK>, PK extends Serializable>
        extends Persistable<PK> {

    /**
     * Returns the user who created this entity.
     * 
     * @return the createdBy
     */
    U getCreatedBy();


    /**
     * Sets the user who created this entity.
     * 
     * @param createdBy the creating entity to set
     */
    void setCreatedBy(final U createdBy);


    /**
     * Returns the creation date of the entity.
     * 
     * @return the createdDate
     */
    Date getCreatedDate();


    /**
     * Sets the creation date of the entity.
     * 
     * @param creationDate the creation date to set
     */
    void setCreated(final Date creationDate);


    /**
     * Returns the user who modified the entity lastly.
     * 
     * @return the lastModifiedBy
     */
    U getLastModifiedBy();


    /**
     * Sets the user who modified the entity lastly.
     * 
     * @param lastModifiedBy the last modifying entity to set
     */
    void setLastModifiedBy(final U lastModifiedBy);


    /**
     * Returns the date of the last modification.
     * 
     * @return the lastModifiedDate
     */
    Date getLastModifiedDate();


    /**
     * Sets the date of the last modification.
     * 
     * @param lastModifiedDate the date of the last modification to set
     */
    void setLastModified(final Date lastModifiedDate);
}

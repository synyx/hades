package org.synyx.hades.domain;

import java.io.Serializable;


/**
 * Simple interface for entities.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 * @param <PK> the type of the identifier
 */
public interface Persistable<PK extends Serializable> extends Serializable {

    /**
     * Returns the id of the entity.
     * 
     * @return the id
     */
    PK getId();


    /**
     * Returns if the identifyable is new or was persisted already.
     * 
     * @return if the object is new
     */
    boolean isNew();
}

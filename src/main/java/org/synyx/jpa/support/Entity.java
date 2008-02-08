package org.synyx.jpa.support;

import java.io.Serializable;


/**
 * Simple interface for entities.
 * 
 * @author Oliver Gierke
 */
public interface Entity<PK extends Serializable> {

    /**
     * Returns the id of the entity.
     * 
     * @return
     */
    public PK getId();
}

package org.synyx.hades.domain.support;

import java.io.Serializable;

import org.synyx.hades.domain.Persistable;


/**
 * Interface for components that are aware of the application's current auditor.
 * This will be some kind of user mostly.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 * @param <T> the type of the auditing instance
 * @param <PK> the type of the auditing instance's identifier
 */
public interface AuditorAware<T extends Persistable<PK>, PK extends Serializable> {

    /**
     * Returns the current auditor of the application.
     * 
     * @return the current auditor
     */
    T getCurrentAuditor();
}

package org.synyx.hades.domain.support;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Required;
import org.synyx.hades.domain.Auditable;
import org.synyx.hades.domain.Entity;


/**
 * Aspect touching entities being saved. Sets modification date and user.
 * 
 * @author Oliver Gierke
 */
@Aspect
public class AuditionAdvice<T extends Entity<PK>, PK extends Serializable> {

    private static final Log log = LogFactory.getLog(AuditionAdvice.class);

    private CurrentUserAware<T, PK> currentUserAware;


    /**
     * Setter to inject a {@code CurrentUserAware} component to retireve the
     * current user.
     * 
     * @param currentUserAware the userDao to set
     */
    @Required
    public void setCurrentUserAware(CurrentUserAware<T, PK> currentUserAware) {

        this.currentUserAware = currentUserAware;
    }


    /**
     * Sets modification date and user on an auditable entity.
     * 
     * @param entity
     */
    @Before("execution(* org.synyx.hades.dao.GenericDao+.save*(..)) && args(entity)")
    public void touch(Auditable<Entity<PK>, PK> entity) {

        // Retrieve values to set
        Entity<PK> user = currentUserAware.getCurrentUser();
        Date currentDate = new Date();

        // Set modification
        entity.setLastModifiedBy(user);
        entity.setLastModified(currentDate);

        // Set creation values if entity is about to be created
        if (entity.isNew()) {
            entity.setCreated(currentDate);
            entity.setCreatedBy(user);
        }

        // Log touching
        if (log.isDebugEnabled()) {

            StringBuffer buffer = new StringBuffer("Touched "
                    + entity.toString() + " - Last modification on "
                    + currentDate);

            if (null != user) {
                buffer.append(" by " + user.toString());
            }

            log.debug(buffer.toString());
        }
    }
}

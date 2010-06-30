package org.synyx.hades.domain.auditing;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;


/**
 * @author Oliver Gierke
 */
public class AuditingEntityListener<T> implements InitializingBean {

    private static final Log LOG =
            LogFactory.getLog(AuditingEntityListener.class);
    private AuditorAware<T> auditorAware;


    public AuditingEntityListener() {

        System.out.println("Created!");
    }


    /**
     * @param auditorAware the auditorAware to set
     */
    public void setAuditorAware(AuditorAware<T> auditorAware) {

        this.auditorAware = auditorAware;
    }


    @PrePersist
    public void markCreated(Object object) {

        System.out.println("created" + object.toString() + auditorAware);
    }


    @PreUpdate
    public void markModified(Object object) {

        System.out.println("modified" + object.toString() + auditorAware);
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {

        if (auditorAware == null) {
            LOG.warn("No AuditorAware set! Auditing will not be applied!");
        }
    }
}

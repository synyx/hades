package org.synyx.hades.domain.support;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Required;
import org.synyx.hades.domain.Auditable;
import org.synyx.hades.domain.Persistable;


/**
 * Aspect touching entities being saved. Sets modification date and auditor. if
 * no {@code AuditorAware} is set only modification and creation date will be
 * set.
 * <p>
 * The advice sets both modification and creation of
 * 
 * @author Oliver Gierke - gierke@synyx.de
 * @param <T> the type of the auditing instance
 * @param <PK> the type of the auditing instance's identifier
 */
@Aspect
public class AuditionAdvice<T extends Persistable<PK>, PK extends Serializable> {

    private static final Log log = LogFactory.getLog(AuditionAdvice.class);

    private AuditorAware<T, PK> auditorAware;
    private boolean modifyOnCreation = true;


    /**
     * Setter to inject a {@code AuditorAware} component to retrieve the current
     * auditor.
     * 
     * @param auditorAware the auditorAware to set
     */
    @Required
    public void setAuditorAware(final AuditorAware<T, PK> auditorAware) {

        this.auditorAware = auditorAware;
    }


    /**
     * Set this to false if you want to treat entity creation as modification
     * and thus set the current date as modification date, too. Defaults to
     * {@code true}.
     * 
     * @param modifyOnCreation if modification information shall be set on
     *                creation, too
     */
    public void setModifyOnCreation(boolean modifyOnCreation) {

        this.modifyOnCreation = modifyOnCreation;
    }


    /**
     * Sets modification date and user on an auditable entity.
     * 
     * @param auditable
     */
    @Before("execution(* org.synyx.hades.dao.GenericDao+.save*(..)) && args(auditable)")
    public void touch(final Auditable<Persistable<PK>, PK> auditable) {

        T auditor = touchAuditor(auditable);
        Date now = touchDate(auditable);

        // Log touching
        if (log.isDebugEnabled()) {

            StringBuffer buffer = new StringBuffer("Touched "
                    + auditable.toString() + " - Last modification on " + now);

            if (null != auditor) {
                buffer.append(" by " + auditor.toString());
            }

            log.debug(buffer.toString());
        }
    }


    /**
     * Sets modifying and creating auditioner. Creating auditioner is only set
     * on new auditables.
     * 
     * @param auditable
     * @return
     */
    private T touchAuditor(final Auditable<Persistable<PK>, PK> auditable) {

        T auditor = null;

        if (null == auditorAware) {
            return null;
        }

        auditor = auditorAware.getCurrentAuditor();

        if (auditable.isNew()) {

            auditable.setCreatedBy(auditor);

            if (!modifyOnCreation) {
                return auditor;
            }
        }

        auditable.setLastModifiedBy(auditor);

        return auditor;
    }


    /**
     * Touches the auditable regarding modification and creation date. Creation
     * date is only set on new auditables.
     * 
     * @param auditable
     * @return
     */
    private Date touchDate(final Auditable<Persistable<PK>, PK> auditable) {

        Date now = new Date();

        if (auditable.isNew()) {
            auditable.setCreated(now);

            if (!modifyOnCreation) {
                return now;
            }
        }

        auditable.setLastModified(now);

        return now;
    }
}

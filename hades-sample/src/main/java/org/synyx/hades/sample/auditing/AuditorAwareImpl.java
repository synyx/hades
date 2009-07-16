package org.synyx.hades.sample.auditing;

import org.synyx.hades.domain.auditing.AuditorAware;


/**
 * Dummy implementation of {@link AuditorAware}. It will return the configured
 * {@link AuditableUser} as auditor on every call to
 * {@link #getCurrentAuditor()}. Normally you would access the applications
 * security subsystem to return the current user.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class AuditorAwareImpl implements AuditorAware<AuditableUser> {

    private AuditableUser auditor;


    /**
     * @param auditor the auditor to set
     */
    public void setAuditor(AuditableUser auditor) {

        this.auditor = auditor;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.domain.AuditorAware#getCurrentAuditor()
     */
    public AuditableUser getCurrentAuditor() {

        return auditor;
    }

}

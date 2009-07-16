package org.synyx.hades.sample.auditing;

import javax.persistence.Entity;

import org.synyx.hades.domain.auditing.AbstractAuditable;


/**
 * User domain class that uses auditing functionality of Hades that can either
 * be aquired implementing {@link org.synyx.hades.domain.Auditable} or extend
 * {@link AbstractAuditable}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
@Entity
public class AuditableUser extends AbstractAuditable<AuditableUser, Long> {

    private static final long serialVersionUID = 1L;

    private String username;


    /**
     * Set's the user's name.
     * 
     * @param username the username to set
     */
    public void setUsername(String username) {

        this.username = username;
    }


    /**
     * Returns the user's name.
     * 
     * @return the username
     */
    public String getUsername() {

        return username;
    }
}

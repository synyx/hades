package org.synyx.hades.sample.domain;

import org.synyx.hades.domain.support.AbstractAuditable;


/**
 * @author Oliver Gierke - gierke@synyx.de
 */
public class AuditableUser extends AbstractAuditable<AuditableUser, Long> {

    private static final long serialVersionUID = 1L;

    private String username;


    /**
     * @param username the username to set
     */
    public void setUsername(String username) {

        this.username = username;
    }


    /**
     * @return the username
     */
    public String getUsername() {

        return username;
    }

}

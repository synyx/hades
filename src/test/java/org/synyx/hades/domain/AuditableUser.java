package org.synyx.hades.domain;

import javax.persistence.Entity;

import org.synyx.hades.domain.AbstractAuditableEntity;


/**
 * Sample auditable user to demonstrate working with
 * {@code AbstractAuditableEntity}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
@Entity
public class AuditableUser extends AbstractAuditableEntity<AuditableUser, Long> {

    private String firstname;


    /**
     * Returns the firstname.
     * 
     * @return the firstname
     */
    public String getFirstname() {

        return firstname;
    }


    /**
     * Sets the firstname.
     * 
     * @param firstname the firstname to set
     */
    public void setFirstname(String firstname) {

        this.firstname = firstname;
    }
}

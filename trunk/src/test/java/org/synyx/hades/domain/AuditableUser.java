package org.synyx.hades.domain;

import javax.persistence.Entity;


/**
 * Sample auditable user to demonstrate working with
 * {@code AbstractAuditableEntity}. No declaration of an ID is necessary.
 * Furthermore alle auditioning information has to be declared explicitly.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
@Entity
public class AuditableUser extends AbstractAuditable<AuditableUser, Long> {

    private static final long serialVersionUID = 7409344446795693011L;

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

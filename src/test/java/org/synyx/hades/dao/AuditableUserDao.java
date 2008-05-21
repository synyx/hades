package org.synyx.hades.dao;

import java.util.List;

import org.synyx.hades.domain.AuditableUser;


/**
 * DAO interface for {@code AuditableUser}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public interface AuditableUserDao extends GenericDao<AuditableUser, Long> {

    /**
     * Returns all users with the given firstname.
     * 
     * @param firstname
     * @return all users with the given firstname.
     */
    public List<AuditableUser> findByFirstname(final String firstname);
}

package org.synyx.hades.sample.domain;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;

import org.synyx.hades.domain.support.AbstractPersistable;


/**
 * Sample user class.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
@Entity
@NamedQuery(name = "User.findByUserblame", query = "from User u where u.username = ?")
public class User extends AbstractPersistable<Long> {

    private static final long serialVersionUID = -2952735933715107252L;

    private String username;


    public User() {

    }


    /**
     * Returns the username.
     * 
     * @return
     */
    public String getUsername() {

        return username;
    }


    /**
     * @param username the username to set
     */
    public void setUsername(String username) {

        this.username = username;
    }
}

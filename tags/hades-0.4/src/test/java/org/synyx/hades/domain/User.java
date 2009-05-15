/*
 * Copyright 2002-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.synyx.hades.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;

import org.synyx.hades.domain.support.AbstractPersistable;


/**
 * Domain class representing a person emphasizing the use of
 * {@code AbstractEntity}. No declaration of an id is required. The id is typed
 * by the parameterizable superclass.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
@Entity
@NamedQuery(name = "User.findByEmailAddress", query = "SELECT u FROM User u WHERE u.emailAddress = ?1")
public class User extends AbstractPersistable<Integer> {

    private static final long serialVersionUID = 8653688953355455933L;

    private String firstname;
    private String lastname;

    @Column(nullable = false, unique = true)
    private String emailAddress;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private Set<User> colleagues;

    @ManyToMany
    private Set<Role> roles;


    /**
     * Creates a new empty instance of {@code User}.
     */
    public User() {

        this.roles = new HashSet<Role>();
        this.colleagues = new HashSet<User>();
    }


    /**
     * Creates a new instance of {@code User} with preinitialized values for
     * firstname, lastname and email address.
     * 
     * @param firstname
     * @param lastname
     * @param emailAddress
     */
    public User(final String firstname, final String lastname,
            final String emailAddress) {

        this();
        this.firstname = firstname;
        this.lastname = lastname;
        this.emailAddress = emailAddress;
    }


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
    public void setFirstname(final String firstname) {

        this.firstname = firstname;
    }


    /**
     * Returns the lastname.
     * 
     * @return the lastname
     */
    public String getLastname() {

        return lastname;
    }


    /**
     * Sets the lastname.
     * 
     * @param lastname the lastname to set
     */
    public void setLastname(String lastname) {

        this.lastname = lastname;
    }


    /**
     * Returns the email address.
     * 
     * @return the emailAddress
     */
    public String getEmailAddress() {

        return emailAddress;
    }


    /**
     * Sets the email address.
     * 
     * @param emailAddress the emailAddress to set
     */
    public void setEmailAddress(String emailAddress) {

        this.emailAddress = emailAddress;
    }


    /**
     * Returns the user's roles.
     * 
     * @return the role
     */
    public Set<Role> getRole() {

        return roles;
    }


    /**
     * Gives the user a role. Adding a role the user already owns is a no-op.
     */
    public void addRole(Role role) {

        roles.add(role);
    }


    /**
     * Revokes a role from a user.
     * 
     * @param role
     */
    public void removeRole(Role role) {

        roles.remove(role);
    }


    /**
     * Returns the colleagues of the user.
     * 
     * @return the colleagues
     */
    public Set<User> getColleagues() {

        return colleagues;
    }


    /**
     * Adds a new colleague to the user. Adding the user himself as colleague is
     * a no-op.
     * 
     * @param collegue
     */
    public void addColleague(User collegue) {

        // Prevent from adding the user himself as colleague.
        if (this.equals(collegue)) {
            return;
        }

        colleagues.add(collegue);
        collegue.getColleagues().add(this);
    }


    /**
     * Removes a colleague from the list of colleagues.
     * 
     * @param colleague
     */
    public void removeColleague(User colleague) {

        colleagues.remove(colleague);
        colleague.getColleagues().remove(this);
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof User)) {
            return false;
        }

        User that = (User) obj;

        if (null == this.getId() || null == that.getId()) {
            return false;
        }

        return this.getId().equals(that.getId());
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        return "User: " + getId() + ", " + getFirstname() + " " + getLastname()
                + ", " + getEmailAddress();
    }
}

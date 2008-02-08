package org.synyx.jpa.support.test.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;


/**
 * Domain class representing a person.
 * 
 * @author Eberhard Wolff
 * @author Oliver Gierke
 */
@Entity
public class User implements org.synyx.jpa.support.Entity<Integer> {

    private static final long serialVersionUID = 8653688953355455933L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String firstname;
    private String lastname;

    @Column(nullable = false, unique = true)
    private String emailAddress;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<User> colleagues;

    @ManyToMany
    private List<Role> roles;


    /**
     * 
     */
    public User() {

        this.roles = new ArrayList<Role>();
        this.colleagues = new ArrayList<User>();
    }


    /**
     * @param firstname
     * @param lastname
     * @param emailAddress
     */
    public User(String firstname, String lastname, String emailAddress) {

        this();
        this.firstname = firstname;
        this.lastname = lastname;
        this.emailAddress = emailAddress;
    }


    /**
     * @return the id
     */
    public Integer getId() {

        return id;
    }


    /**
     * @param id the id to set
     */
    public void setId(Integer id) {

        this.id = id;
    }


    /**
     * @return the firstname
     */
    public String getFirstname() {

        return firstname;
    }


    /**
     * @param firstname the firstname to set
     */
    public void setFirstname(String firstname) {

        this.firstname = firstname;
    }


    /**
     * @return the lastname
     */
    public String getLastname() {

        return lastname;
    }


    /**
     * @param lastname the lastname to set
     */
    public void setLastname(String lastname) {

        this.lastname = lastname;
    }


    /**
     * @return the emailAddress
     */
    public String getEmailAddress() {

        return emailAddress;
    }


    /**
     * @param emailAddress the emailAddress to set
     */
    public void setEmailAddress(String emailAddress) {

        this.emailAddress = emailAddress;
    }


    /**
     * @return the role
     */
    public List<Role> getRole() {

        return roles;
    }


    /**
     * 
     */
    public void addRole(Role role) {

        roles.add(role);
    }


    public void removeRole(Role role) {

        roles.remove(role);
    }


    /**
     * @return the colleagues
     */
    public List<User> getColleagues() {

        return colleagues;
    }


    public void addColleague(User collegue) {

        colleagues.add(collegue);
        collegue.getColleagues().add(this);
    }


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

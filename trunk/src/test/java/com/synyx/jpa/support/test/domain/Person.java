package com.synyx.jpa.support.test.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


/**
 * Domain class representing a person.
 * 
 * @author Eberhard Wolff
 * @author Oliver Gierke
 */
@Entity
public class Person implements Serializable {

    private static final long serialVersionUID = 8653688953355455933L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private Integer weight;


    /**
     * Empty constructor required by JPA.
     */
    public Person() {

    }


    /**
     * Constructor to correctly setup object.
     * 
     * @param name
     * @param weight
     */
    public Person(String name, Integer weight) {

        this.name = name;
        this.weight = weight;
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
     * @return the name
     */
    public String getName() {

        return name;
    }


    /**
     * @param name the name to set
     */
    public void setName(String name) {

        this.name = name;
    }


    /**
     * @return the weight
     */
    public Integer getWeight() {

        return weight;
    }


    /**
     * @param weight the weight to set
     */
    public void setWeight(Integer weight) {

        this.weight = weight;
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof Person)) {
            return false;
        }

        Person that = (Person) obj;

        return this.getId().equals(that.getId());
    }
}

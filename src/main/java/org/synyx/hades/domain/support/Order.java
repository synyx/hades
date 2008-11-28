package org.synyx.hades.domain.support;

/**
 * Enumeration for sort orders.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public enum Order {

    ASCENDING("asc"), DESCENDING("desc");

    private String jpaValue;


    /**
     * Creates a new instance of {@code Order}.
     * 
     * @param jpaValue
     */
    private Order(String jpaValue) {

        this.jpaValue = jpaValue;
    }


    /**
     * Returns the JPA specific value.
     * 
     * @return the JPA specific value
     */
    public String getJpaValue() {

        return jpaValue;
    }
}

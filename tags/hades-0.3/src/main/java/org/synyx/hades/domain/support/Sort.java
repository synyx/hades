package org.synyx.hades.domain.support;

/**
 * Sort option for queries.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class Sort {

    private String[] properties;
    private Order order;


    /**
     * Creates a new instance of {@code Sort}.
     * 
     * @param order
     * @param properties
     */
    public Sort(Order order, String... properties) {

        this.properties = properties;
        this.order = order;
    }


    /**
     * Returns sort properties.
     * 
     * @return the property
     */
    public String[] getProperties() {

        return properties;
    }


    /**
     * Returns the sort order.
     * 
     * @return the order
     */
    public Order getOrder() {

        return order;
    }


    /**
     * Returns whether the sorting should be ascending.
     * 
     * @return whether the sorting should be ascending
     */
    public boolean isAscending() {

        return Order.ASCENDING.equals(order);
    }
}

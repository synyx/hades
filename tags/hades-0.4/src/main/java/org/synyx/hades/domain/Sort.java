/*
 * Copyright 2002-2008 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.synyx.hades.domain;

import java.util.Arrays;


/**
 * Sort option for queries. You have to provide at least a list of properties to
 * sort for that must not include {@code null} or empty strings. The order
 * defaults to {@value Order#ASCENDING}.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class Sort {

    public static final Order DEFAULT_ORDER = Order.ASCENDING;

    private String[] properties;
    private Order order;


    /**
     * Creates a new {@link Sort} instance. Order defaults to
     * {@value Order#ASCENDING}.
     * 
     * @param properties must not be {@code null} or contain {@code null} or
     *            empty strings
     */
    public Sort(String... properties) {

        this(DEFAULT_ORDER, properties);
    }


    /**
     * Creates a new {@link Sort} instance.
     * 
     * @param order defaults to {@value Order#ASCENDING} (for {@code null}
     *            cases, too)
     * @param properties must not be {@code null} or contain {@code null} or
     *            empty strings
     */
    public Sort(Order order, String... properties) {

        if (null == properties || 0 == properties.length) {
            throw new IllegalArgumentException(
                    "You have to provide at least one property to sort by!");
        }

        for (String property : properties) {

            if (null == property || "".equals(property.trim())) {
                throw new IllegalArgumentException(
                        "Properies must not contain empty or null values");
            }
        }

        this.properties = properties.clone();
        this.order = null == order ? DEFAULT_ORDER : order;
    }


    /**
     * Returns sort properties.
     * 
     * @return the property
     */
    public String[] getProperties() {

        return properties.clone();
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


    /**
     * Returns a new {@link Sort} instance with the given order and the same
     * properties.
     * 
     * @param order
     * @return
     */
    public Sort with(Order order) {

        return new Sort(order, getProperties());
    }


    /**
     * Returns a new {@link Sort} instance with the given properties and the
     * same order.
     * 
     * @param properties
     * @return
     */
    public Sort withProperties(String... properties) {

        return new Sort(getOrder(), properties);
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Sort)) {
            return false;
        }

        Sort that = (Sort) obj;

        boolean orderEqual = this.order.equals(that.order);
        boolean propertiesEqual =
                Arrays.equals(this.properties, that.properties);

        return orderEqual && propertiesEqual;
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        int result = 17;

        result = 31 * result + order.hashCode();
        result = 31 * result + Arrays.hashCode(properties);

        return result;
    }
}

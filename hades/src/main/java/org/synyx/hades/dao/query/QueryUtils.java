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
package org.synyx.hades.dao.query;

/**
 * Simple utility class to create JPA queries.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public abstract class QueryUtils {

    public static final String COUNT_QUERY_STRING = "select count(x) from %s x";

    public static final String DELETE_ALL_QUERY_STRING = "delete from %s x";
    public static final String READ_ALL_QUERY = "select x from %s x";


    /**
     * Private constructor to prevent instantiation.
     */
    private QueryUtils() {

    }


    /**
     * Returns the query string for the given class.
     * 
     * @return
     */
    public static String getQueryString(String template, Class<?> clazz) {

        if (null == clazz) {
            throw new IllegalArgumentException("Class must not be null!");
        }

        return getQueryString(template, clazz.getSimpleName());
    }


    /**
     * Returns the query string for the given class name.
     * 
     * @param template
     * @param clazzName
     * @return
     */
    public static String getQueryString(String template, String clazzName) {

        if (null == clazzName || "".equals(clazzName.trim())) {
            throw new IllegalArgumentException("Classname must not be null!");
        }

        return String.format(template, clazzName);
    }
}

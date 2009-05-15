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

package org.synyx.hades.core;

/**
 * Query lookup strategy to execute finders.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public enum QueryLookupStrategy {

    /**
     * Always creates the query from the given method.
     */
    CREATE,

    /**
     * Uses a named query named {@literal $DomainClass.$DaoMethodName}.
     */
    USE_NAMED_QUERY,

    /**
     * Tries to lookup a named query but creates a query from the method name if
     * no named query found.
     */
    CREATE_IF_NOT_FOUND;

    /**
     * Returns a strategy from the given XML value.
     * 
     * @param xml
     * @return a strategy from the given XML value
     */
    public static QueryLookupStrategy fromXml(String xml) {

        if (null == xml) {
            return getDefault();
        }

        return valueOf(xml.toUpperCase().replace("-", "_"));
    }


    /**
     * Returns the default strategy to be used.
     * 
     * @return
     */
    public static QueryLookupStrategy getDefault() {

        return CREATE_IF_NOT_FOUND;
    }
}
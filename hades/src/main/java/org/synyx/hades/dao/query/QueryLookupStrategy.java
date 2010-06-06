/*
 * Copyright 2008-2010 the original author or authors.
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

import java.util.Locale;


/**
 * Query lookup strategy to execute finders.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public enum QueryLookupStrategy {

    /**
     * Always creates the query from the given method.
     */
    CREATE {

        @Override
        public HadesQuery resolveQuery(QueryMethod method) {

            return SimpleHadesQuery.construct(method);
        }

    },

    /**
     * Uses a named query named {@literal $DomainClass.$DaoMethodName}.
     */
    USE_DECLARED_QUERY {

        @Override
        public HadesQuery resolveQuery(QueryMethod method) {

            HadesQuery query = SimpleHadesQuery.fromHadesAnnotation(method);

            if (null != query) {
                return query;
            }

            query = NamedHadesQuery.lookupFrom(method);

            if (null != query) {
                return query;
            }

            throw new IllegalStateException(
                    String
                            .format(
                                    "Did neither find a NamedQuery nor a Hades Query for method %s!",
                                    method));
        }
    },

    /**
     * Tries to lookup a declared query (either named query or an annotated
     * {@link org.synyx.hades.dao.Query} but creates a query from the method
     * name if no declared query found.
     */
    CREATE_IF_NOT_FOUND {

        @Override
        public HadesQuery resolveQuery(QueryMethod method) {

            try {
                return USE_DECLARED_QUERY.resolveQuery(method);
            } catch (IllegalStateException e) {
                return SimpleHadesQuery.construct(method);
            }
        }
    };

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

        return valueOf(xml.toUpperCase(Locale.US).replace("-", "_"));
    }


    /**
     * Returns the default strategy to be used.
     * 
     * @return
     */
    public static QueryLookupStrategy getDefault() {

        return CREATE_IF_NOT_FOUND;
    }


    /**
     * Resolves a {@link HadesQuery} from the given {@link QueryMethod} that can
     * be executed afterwards.
     * 
     * @param method
     * @return
     */
    public abstract HadesQuery resolveQuery(QueryMethod method);
}
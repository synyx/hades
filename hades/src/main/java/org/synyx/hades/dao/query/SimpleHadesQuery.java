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

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * {@link HadesQuery} implementation that inspects a {@link FinderMethod} for
 * the existanve of an {@link org.synyx.hades.dao.Query} annotation and creates
 * a JPA {@link Query} from it.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
final class SimpleHadesQuery extends AbstractHadesQuery {

    private static final Log LOG = LogFactory.getLog(SimpleHadesQuery.class);

    private String queryString;


    /**
     * Creates a new {@link SimpleHadesQuery} that encapsulates a simple query
     * string.
     */
    private SimpleHadesQuery(FinderMethod method, String queryString) {

        super(method);
        this.queryString = queryString;
    }


    /**
     * Creates a new {@link SimpleHadesQuery} that constructs the query from the
     * given {@link FinderMethod}.
     * 
     * @param method
     */
    private SimpleHadesQuery(FinderMethod method) {

        super(method);
        this.queryString = new QueryCreator(method).constructQuery();
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.orm.FinderMethod.AbstractHadesQuery#getQuery(
     * javax.persistence.EntityManager)
     */
    @Override
    protected Query getQuery(EntityManager em) {

        return em.createQuery(queryString);
    }


    /**
     * Creates a {@link HadesQuery} from the given {@link FinderMethod} that is
     * potentially annotated with {@link org.synyx.hades.dao.Query}.
     * 
     * @param finderMethod
     * @return the {@link HadesQuery} derived from the annotation or {@code
     *         null} if no annotation found.
     */
    public static HadesQuery fromHadesAnnotation(FinderMethod finderMethod) {

        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Looking up Hades query for method %s",
                    finderMethod.getName()));
        }

        org.synyx.hades.dao.Query annotation =
                finderMethod.getQueryAnnotation();

        return null == annotation ? null : new SimpleHadesQuery(finderMethod,
                annotation.value());
    }


    /**
     * Constructs a {@link HadesQuery} from the given {@link FinderMethod}.
     * 
     * @param finderMethod
     * @return
     */
    public static HadesQuery construct(FinderMethod finderMethod) {

        return new SimpleHadesQuery(finderMethod);
    }
}
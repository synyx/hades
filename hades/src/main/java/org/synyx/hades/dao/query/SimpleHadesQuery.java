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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.QueryHint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * {@link HadesQuery} implementation that inspects a {@link QueryMethod} for the
 * existanve of an {@link org.synyx.hades.dao.Query} annotation and creates a
 * JPA {@link Query} from it.
 * 
 * @author Oliver Gierke
 */
final class SimpleHadesQuery extends AbstractHadesQuery {

    private static final Log LOG = LogFactory.getLog(SimpleHadesQuery.class);

    private final String queryString;
    private final String alias;
    private final List<QueryHint> hints;


    /**
     * Creates a new {@link SimpleHadesQuery} that encapsulates a simple query
     * string.
     */
    SimpleHadesQuery(QueryMethod method, EntityManager em, String queryString) {

        super(method, em);
        this.queryString = queryString;
        this.alias = QueryUtils.detectAlias(queryString);
        this.hints = method.getHints();
    }


    /**
     * Creates a new {@link SimpleHadesQuery} that constructs the query from the
     * given {@link QueryMethod}.
     * 
     * @param method
     * @param em
     */
    SimpleHadesQuery(QueryMethod method, EntityManager em) {

        this(method, em, new QueryCreator(method).constructQuery());
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.synyx.hades.dao.query.AbstractHadesQuery#createQuery(javax.persistence
     * .EntityManager, org.synyx.hades.dao.query.ParameterBinder)
     */
    @Override
    protected Query createQuery(EntityManager em, ParameterBinder binder) {

        String query =
                QueryUtils.applySorting(queryString, binder.getSort(), alias);

        return applyHints(em.createQuery(query));
    }


    /*
     * (non-Javadoc)
     * 
     * @seeorg.synyx.hades.dao.query.AbstractHadesQuery#createCountQuery(javax.
     * persistence.EntityManager, org.synyx.hades.dao.query.ParameterBinder)
     */
    @Override
    protected Query createCountQuery(EntityManager em, ParameterBinder binder) {

        String query = QueryUtils.createCountQueryFor(queryString);
        return applyHints(em.createQuery(query));
    }


    /**
     * Applies the declared query hints to the given query.
     * 
     * @param query
     * @return
     */
    private Query applyHints(Query query) {

        for (QueryHint hint : hints) {
            query.setHint(hint.name(), hint.value());
        }

        return query;
    }


    /**
     * Creates a {@link HadesQuery} from the given {@link QueryMethod} that is
     * potentially annotated with {@link org.synyx.hades.dao.Query}.
     * 
     * @param finderMethod
     * @param em
     * @return the {@link HadesQuery} derived from the annotation or
     *         {@code null} if no annotation found.
     */
    public static HadesQuery fromHadesAnnotation(QueryMethod finderMethod,
            EntityManager em) {

        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Looking up Hades query for method %s",
                    finderMethod.getName()));
        }

        org.synyx.hades.dao.Query annotation =
                finderMethod.getQueryAnnotation();

        return null == annotation ? null : new SimpleHadesQuery(finderMethod,
                em, annotation.value());
    }


    /**
     * Constructs a {@link HadesQuery} from the given {@link QueryMethod}.
     * 
     * @param queryMethod
     * @param em
     * @return
     */
    public static HadesQuery construct(QueryMethod queryMethod, EntityManager em) {

        if (queryMethod.isModifyingQuery()) {
            throw QueryCreationException
                    .create(queryMethod,
                            "Cannot create query from method name "
                                    + "for modifying query. Use @Query or @NamedQuery to "
                                    + "declare the query to execute. Do not use CREATE as "
                                    + "strategy to lookup queries!");
        }

        return new SimpleHadesQuery(queryMethod, em);
    }
}
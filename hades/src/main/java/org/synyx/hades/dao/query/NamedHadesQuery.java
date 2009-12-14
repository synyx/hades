/*
 * Copyright 2008-2009 the original author or authors.
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
 * Implementation of {@link HadesQuery} based on
 * {@link javax.persistence.NamedQuery}s.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
final class NamedHadesQuery extends AbstractHadesQuery {

    private static final Log LOG = LogFactory.getLog(NamedHadesQuery.class);

    private String queryName;
    private QueryExtractor extractor;


    /**
     * Creates a new {@link NamedHadesQuery}.
     */
    private NamedHadesQuery(QueryMethod method) {

        super(method);

        this.queryName = method.getNamedQueryName();
        this.extractor = method.getQueryExtractor();
        method.getEntityManager().createNamedQuery(queryName);
    }


    /**
     * Looks up a named query for the given {@link QueryMethod}.
     * 
     * @param method
     * @return
     */
    public static HadesQuery lookupFrom(QueryMethod method) {

        final String queryName = method.getNamedQueryName();

        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Looking up named query %s", queryName));
        }

        try {

            HadesQuery query = new NamedHadesQuery(method);
            Parameters parameters = method.getParameters();

            if (parameters.hasSortParameter()) {
                throw new IllegalStateException(
                        String
                                .format(
                                        "Finder method %s is backed "
                                                + "by a NamedQuery and must "
                                                + "not contain a sort parameter as we "
                                                + "cannot modify the query! Use @Query instead!",
                                        method));
            }

            boolean isPaging = parameters.hasPageableParameter();
            boolean cannotExtractQuery =
                    !method.getQueryExtractor().canExtractQuery();

            if (isPaging && cannotExtractQuery) {
                throw QueryCreationException
                        .create(
                                method,
                                "Cannot use Pageable parameter in query methods with your persistence provider!");
            }

            if (parameters.hasPageableParameter()) {
                LOG
                        .info(String
                                .format(
                                        "Finder method %s is backed by a NamedQuery"
                                                + " but contains a Pageble parameter! Sorting deliviered "
                                                + "via this Pageable will not be applied!",
                                        method));

            }

            return query;
        } catch (IllegalArgumentException e) {
            return null;
        }
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

        return em.createNamedQuery(queryName);
    }


    /*
     * (non-Javadoc)
     * 
     * @seeorg.synyx.hades.dao.query.AbstractHadesQuery#createCountQuery(javax.
     * persistence.EntityManager, org.synyx.hades.dao.query.ParameterBinder)
     */
    @Override
    protected Query createCountQuery(EntityManager em, ParameterBinder binder) {

        Query query = createQuery(em, binder);
        String queryString = extractor.extractQueryString(query);

        return em.createQuery(QueryUtils.createCountQueryFor(queryString));
    }
}
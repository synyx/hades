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


/**
 * Implementation of {@link HadesQuery} based on
 * {@link javax.persistence.NamedQuery}s.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
class NamedHadesQuery extends AbstractHadesQuery {

    private String queryName;


    /**
     * Creates a new {@link NamedHadesQuery}.
     */
    private NamedHadesQuery(FinderMethod method) {

        super(method);

        this.queryName = method.getNamedQueryName();
        method.getEntityManager().createNamedQuery(queryName);
    }


    /**
     * Looks up a named query for the given {@link FinderMethod}.
     * 
     * @param finderMethod
     * @return
     */
    public static HadesQuery lookupFrom(FinderMethod finderMethod) {

        final String queryName = finderMethod.getNamedQueryName();

        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Looking up named query %s", queryName));
        }

        try {
            return new NamedHadesQuery(finderMethod);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.synyx.hades.dao.orm.FinderMethod.AbstractHadesQuery#getQuery(
     * javax.persistence.EntityManager)
     */
    @Override
    protected Query getQuery(EntityManager em) {

        return em.createNamedQuery(queryName);
    }
}
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

import static org.synyx.hades.dao.query.QueryUtils.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;


/**
 * Abstraction of a method that is designated to execute a finder query.
 * Enriches the standard {@link Method} interface with Hades specific
 * information that is necessary to construct {@link HadesQuery}s for the
 * method.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public class FinderMethod {

    static final Log LOG = LogFactory.getLog(FinderMethod.class);

    private Method method;
    private String prefix;
    private Class<?> domainClass;

    private HadesQuery hadesQuery;
    private EntityManager em;


    /**
     * Creates a new {@link FinderMethod} from the given parameters. Looks up
     * the correct query to use for following invocations of the method given.
     * 
     * @param method
     * @param prefix
     * @param domainClass
     * @param strategy
     * @param em
     */
    public FinderMethod(Method method, String prefix, Class<?> domainClass,
            QueryLookupStrategy strategy, EntityManager em) {

        Assert.notNull(method, "Method must not be null!");
        Assert.hasText(prefix, "Prefix must not be emtpy!");
        Assert.notNull(domainClass, "Domain class must not be null!");
        Assert.notNull(em, "EntityManager must not be null!");

        if (!method.getName().startsWith(prefix)) {
            throw new IllegalArgumentException(String.format(
                    "Cannot construct query for non finder method! "
                            + "Make sure the method starts with '%s'", prefix));
        }

        this.method = method;
        this.prefix = prefix;
        this.domainClass = domainClass;
        this.em = em;

        strategy =
                null == strategy ? QueryLookupStrategy.getDefault() : strategy;

        this.hadesQuery = strategy.resolveQuery(this);
    }


    /**
     * Returns the method this {@link FinderMethod} was created for.
     * 
     * @return
     */
    Method getMethod() {

        return method;
    }


    /**
     * Returns the {@link EntityManager}.
     * 
     * @return
     */
    EntityManager getEntityManager() {

        return this.em;
    }


    /**
     * Returns whether the finder will actually return a collection of entities
     * or a single one.
     * 
     * @return
     */
    boolean isCollectionFinder() {

        Class<?> returnType = method.getReturnType();
        return ClassUtils.isAssignable(List.class, returnType);
    }


    /**
     * Returns the name of the {@link javax.persistence.NamedQuery} this method
     * belongs to.
     * 
     * @return
     */
    String getNamedQueryName() {

        return domainClass.getSimpleName() + "." + method.getName();
    }


    /**
     * Executes the {@link Query} backing the {@link FinderMethod} with the
     * given parameters.
     * 
     * @param em
     * @param parameters
     * @return
     */
    public Object executeQuery(Object... parameters) {

        return hadesQuery.execute(parameters);
    }


    /**
     * Constructs a query from the given method. The method has to start with
     * {@code #FINDER_PREFIX}.
     * 
     * @return the query string
     */
    String constructQuery() {

        final String AND = "And";
        final String OR = "Or";

        String methodName = method.getName();
        int numberOfBlocks = 0;

        // Remove prefix
        methodName = methodName.substring(prefix.length(), methodName.length());

        StringBuilder queryBuilder =
                new StringBuilder(getQueryString(READ_ALL_QUERY, domainClass)
                        + " where ");

        // Split OR
        String[] orParts =
                StringUtils.delimitedListToStringArray(methodName, OR);

        for (String orPart : Arrays.asList(orParts)) {

            // Split AND
            String[] andParts =
                    StringUtils.delimitedListToStringArray(orPart, AND);

            StringBuilder andBuilder = new StringBuilder();

            for (String andPart : Arrays.asList(andParts)) {

                andBuilder.append("x.");
                andBuilder.append(StringUtils.uncapitalize(andPart));
                andBuilder.append(" = ?");
                andBuilder.append(" and ");

                numberOfBlocks++;
            }

            andBuilder.delete(andBuilder.length() - 5, andBuilder.length());

            queryBuilder.append(andBuilder);
            queryBuilder.append(" or ");
        }

        // Assert correct number of parameters
        if (numberOfBlocks != method.getParameterTypes().length) {
            throw new IllegalArgumentException(
                    "You have to provide method arguments for each query "
                            + "criteria to construct the query correctly!");
        }

        queryBuilder.delete(queryBuilder.length() - 4, queryBuilder.length());

        String query = queryBuilder.toString();

        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Created query '%s' from method %s", query,
                    method.getName()));
        }

        return query;
    }


    /**
     * Prepares a named query by resolving it against entity mapping queries.
     * Queries have to be named as follows: T.methodName where methodName has to
     * start with find.
     * 
     * @param method
     * @param parameters
     * @return
     */
    Query prepareQuery(final Query query, final Object... parameters) {

        if (parameters != null) {

            for (int i = 0; i < parameters.length; i++) {
                query.setParameter(i + 1, parameters[i]);
            }
        }

        return query;
    }
}

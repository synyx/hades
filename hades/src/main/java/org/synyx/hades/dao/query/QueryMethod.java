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

import static org.synyx.hades.dao.query.QueryExecution.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.synyx.hades.domain.Page;
import org.synyx.hades.domain.Pageable;
import org.synyx.hades.domain.Sort;
import org.synyx.hades.util.ClassUtils;


/**
 * Abstraction of a method that is designated to execute a finder query.
 * Enriches the standard {@link Method} interface with Hades specific
 * information that is necessary to construct {@link HadesQuery}s for the
 * method.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */

public class QueryMethod {

    @SuppressWarnings("unchecked")
    private static final Collection<Class<?>> MODIFY_TYPES =
            Arrays.asList(Void.class, void.class, Integer.class, int.class);

    private Method method;
    private Class<?> domainClass;

    private HadesQuery hadesQuery;
    private EntityManager em;


    /**
     * Creates a new {@link QueryMethod} from the given parameters. Looks up the
     * correct query to use for following invocations of the method given.
     * 
     * @param method
     * @param domainClass
     * @param em
     * @param strategy
     */
    public QueryMethod(Method method, Class<?> domainClass, EntityManager em,
            QueryLookupStrategy strategy) {

        Assert.notNull(method, "Method must not be null!");
        Assert.notNull(domainClass, "Domain class must not be null!");
        Assert.notNull(em, "EntityManager must not be null!");

        for (Class<?> type : Parameters.TYPES) {
            if (ClassUtils.getNumberOfOccurences(method, type) > 1) {
                throw new IllegalStateException(String.format(
                        "Method must only one argument of type %s!", type
                                .getSimpleName()));
            }
        }

        if (ClassUtils.hasParameterOfType(method, Pageable.class)) {
            ClassUtils.assertReturnType(method, Page.class, List.class);
            if (ClassUtils.hasParameterOfType(method, Sort.class)) {
                throw new IllegalStateException(
                        "Method must not have Pageable *and* Sort parameter. "
                                + "Use sorting capabilities on Pageble instead!");
            }
        }

        this.method = method;
        this.domainClass = domainClass;
        this.em = em;

        QueryLookupStrategy strategyToUse =
                null == strategy ? QueryLookupStrategy.getDefault() : strategy;

        this.hadesQuery = strategyToUse.resolveQuery(this);
    }


    /**
     * Creates a new {@link QueryMethod}. Assumes applying default
     * {@link QueryLookupStrategy} by handing {@code null}.
     * 
     * @param method
     * @param domainClass
     * @param em
     */
    public QueryMethod(Method method, Class<?> domainClass, EntityManager em) {

        this(method, domainClass, em, null);
    }


    /**
     * Returns the method's name.
     * 
     * @return
     */
    String getName() {

        return method.getName();
    }


    /**
     * Returns whether the given
     * 
     * @param number
     * @return
     */
    boolean isCorrectNumberOfParameters(int number) {

        return number == getParameterTypes().size();
    }


    /**
     * Returns all actual parameter types. Thus, leaves out types declared in
     * {@link Parameters#TYPES}.
     * 
     * @return
     */
    private List<Class<?>> getParameterTypes() {

        List<Class<?>> result = new ArrayList<Class<?>>();

        for (Class<?> type : method.getParameterTypes()) {
            if (!Parameters.TYPES.contains(type)) {
                result.add(type);
            }
        }

        return result;
    }


    /**
     * Returns whether the given field is valid field name and thus a persistent
     * field to the underlying domain class.
     * 
     * @param fieldName
     * @return
     */
    boolean isValidField(String fieldName) {

        if (null != ReflectionUtils.findMethod(domainClass, "get" + fieldName)) {
            return true;
        }

        return null != ReflectionUtils.findField(domainClass, StringUtils
                .uncapitalize(fieldName));
    }


    /**
     * Returns the name of the domain class the finder belongs to.
     * 
     * @return
     */
    String getDomainClassName() {

        return domainClass.getSimpleName();
    }


    /**
     * Returns the {@link org.synyx.hades.dao.Query} annotation that is applied
     * to the method or {@code null} if none available.
     * 
     * @return
     */
    org.synyx.hades.dao.Query getQueryAnnotation() {

        return method.getAnnotation(org.synyx.hades.dao.Query.class);
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
    boolean isCollectionQuery() {

        Class<?> returnType = method.getReturnType();
        return org.springframework.util.ClassUtils.isAssignable(List.class,
                returnType);
    }


    /**
     * Returns whether the finder will return a {@link Page} of results.
     * 
     * @return
     */
    boolean isPageQuery() {

        Class<?> returnType = method.getReturnType();
        return org.springframework.util.ClassUtils.isAssignable(Page.class,
                returnType);
    }


    /**
     * Returns whether the finder is a modifying one.
     * 
     * @return
     */
    boolean isModifyingQuery() {

        return MODIFY_TYPES.contains(method.getReturnType());
    }


    /**
     * Returns whether the finder contains a Sort parameter.
     * 
     * @return
     */
    boolean hasSortParameter() {

        return Arrays.asList(method.getParameterTypes()).contains(Sort.class);
    }


    /**
     * Returns whether the finder contains a pageable parameter.
     * 
     * @return
     */
    boolean hasPageableParameter() {

        return Arrays.asList(method.getParameterTypes()).contains(
                Pageable.class);
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
     * Executes the {@link javax.persistence.Query} backing the
     * {@link QueryMethod} with the given parameters.
     * 
     * @param em
     * @param parameters
     * @return
     */
    public Object executeQuery(Object... methodParameters) {

        Parameters parameters = new Parameters(method, methodParameters);

        if (isCollectionQuery()) {
            return COLLECTION.execute(hadesQuery, parameters);
        }

        if (isPageQuery()) {
            return PAGE.execute(hadesQuery, parameters);
        }

        if (isModifyingQuery()) {
            return MODIFY.execute(hadesQuery, parameters);
        }

        return SINGLE_ENTITY.execute(hadesQuery, parameters);
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        return method.toString();
    }
}

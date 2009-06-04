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

import java.lang.reflect.Method;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
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
     * @param em
     * @param strategy
     */
    public FinderMethod(Method method, String prefix, Class<?> domainClass,
            EntityManager em, QueryLookupStrategy strategy) {

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

        QueryLookupStrategy strategyToUse =
                null == strategy ? QueryLookupStrategy.getDefault() : strategy;

        this.hadesQuery = strategyToUse.resolveQuery(this);
    }


    public FinderMethod(Method method, String prefix, Class<?> domainClass,
            EntityManager em) {

        this(method, prefix, domainClass, em, null);
    }


    public String getName() {

        return method.getName();
    }


    public String getUnprefixedMethodName() {

        String methodName = method.getName();

        return methodName.substring(prefix.length(), methodName.length());
    }


    /**
     * Returns whether the given
     * 
     * @param number
     * @return
     */
    public boolean isCorrectNumberOfParameters(int number) {

        return number == method.getParameterTypes().length;
    }


    /**
     * Returns whether the given field is valid field name and thus a persistent
     * field to the underlying domain class.
     * 
     * @param fieldName
     * @return
     */
    public boolean isValidField(String fieldName) {

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
    public String getDomainClassName() {

        return domainClass.getSimpleName();
    }


    /**
     * Returns the {@link org.synyx.hades.dao.Query} annotation that is applied
     * to the method or {@code null} if none available.
     * 
     * @return
     */
    public org.synyx.hades.dao.Query getQueryAnnotation() {

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

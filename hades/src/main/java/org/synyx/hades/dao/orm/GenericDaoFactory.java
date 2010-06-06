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

package org.synyx.hades.dao.orm;

import static org.springframework.util.ReflectionUtils.*;
import static org.synyx.hades.util.ClassUtils.*;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.util.Assert;
import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.dao.query.HadesQuery;
import org.synyx.hades.dao.query.QueryExtractor;
import org.synyx.hades.dao.query.QueryLookupStrategy;
import org.synyx.hades.dao.query.QueryMethod;
import org.synyx.hades.util.ClassUtils;


/**
 * Factory bean to create instances of a given DAO interface. Creates a proxy
 * implementing the configured DAO interface and apply an advice handing the
 * control to the {@code FinderExecuterMethodInterceptor} when a method
 * beginning with the configured finder prefix is called. This defaults to
 * {@value GenericDaoSupport#DEFAULT_FINDER_PREFIX}. Furthermore finder
 * resolution can be configured by setting {@link #queryLookupStrategy} which
 * defaults to {@link QueryLookupStrategy#getDefault()}
 * 
 * @author Oliver Gierke
 */
public class GenericDaoFactory {

    private EntityManager entityManager;
    private QueryLookupStrategy queryLookupStrategy =
            QueryLookupStrategy.getDefault();

    private Map<Method, Method> methodCache =
            new ConcurrentHashMap<Method, Method>();


    /**
     * Protected constructor to prevent simple construction from clients.
     */
    protected GenericDaoFactory() {

    }


    /**
     * Creates a new {@link GenericDaoFactory} with the given
     * {@link EntityManager}.
     * 
     * @param entityManager
     * @return
     */
    public static GenericDaoFactory create(EntityManager entityManager) {

        if (null == entityManager) {

            throw new IllegalArgumentException(
                    "EntityManager must not be null!");
        }

        GenericDaoFactory factory = new GenericDaoFactory();
        factory.setEntityManager(entityManager);

        return factory;
    }


    /**
     * Returns the {@link EntityManager}.
     * 
     * @return the entityManager
     */
    protected EntityManager getEntityManager() {

        return entityManager;
    }


    /**
     * Setter to inject entity manager.
     * 
     * @param entityManager the {@link EntityManager} to set
     */
    @PersistenceContext
    public void setEntityManager(final EntityManager entityManager) {

        this.entityManager = entityManager;
    }


    /**
     * Returns the configured {@link QueryLookupStrategy}.
     * 
     * @return the queryLookupStrategy
     */
    QueryLookupStrategy getQueryLookupStrategy() {

        return queryLookupStrategy;
    }


    /**
     * Sets the strategy of how to lookup a query to execute finders.
     * 
     * @param queryLookupStrategy the createFinderQueries to set
     */
    public void setQueryLookupStrategy(
            final QueryLookupStrategy queryLookupStrategy) {

        this.queryLookupStrategy =
                null == queryLookupStrategy ? QueryLookupStrategy.getDefault()
                        : queryLookupStrategy;
    }


    /**
     * Returns a DAO instance for the given interface.
     * 
     * @param <T>
     * @param daoInterface
     * @return
     */
    public <T extends GenericDao<?, ?>> T getDao(Class<T> daoInterface) {

        return getDao(daoInterface, null);
    }


    /**
     * Returns a DAO instance for the given interface backed by an instance
     * providing implementation logic for custom logic.
     * 
     * @param <T>
     * @param daoInterface
     * @param customDaoImplementation
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends GenericDao<?, ?>> T getDao(Class<T> daoInterface,
            Object customDaoImplementation) {

        validate(daoInterface, customDaoImplementation);

        try {
            // Instantiate generic dao
            GenericDaoSupport genericJpaDao = getDaoClass().newInstance();
            genericJpaDao.setEntityManager(entityManager);
            genericJpaDao.setDomainClass(ClassUtils
                    .getDomainClass(daoInterface));
            genericJpaDao.validate();

            // Create proxy
            ProxyFactory result = new ProxyFactory();
            result.setTarget(genericJpaDao);
            result.setInterfaces(new Class[] { daoInterface });

            prepare(result);

            result.addAdvice(new QueryExecuterMethodInterceptor(daoInterface,
                    customDaoImplementation, genericJpaDao));

            return (T) result.getProxy();
        } catch (InstantiationException e) {
            throw new IllegalStateException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }


    /**
     * Callback method to prepare the given {@link ProxyFactory} to e.g. add
     * further interceptors. The {@link QueryExecuterMethodInterceptor} will be
     * added <em>after</em> this method was called, so all interceptors or
     * advisors added in this method will kick in before it.
     * 
     * @see GenericDaoFactoryBean#prepare(ProxyFactory)
     * @param proxyFactory
     */
    protected void prepare(ProxyFactory proxyFactory) {

    }


    /**
     * Determines the base class for the DAO to be created by checking the
     * {@link EntityManager}'s concrete type. If no well known type can be
     * detected {@link #DEFAULT_DAO_CLASS} will be returned.
     * <p>
     * Protected to eventually customize base class - see #102 for details.
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    protected Class<? extends GenericJpaDao> getDaoClass() {

        return GenericJpaDao.class;
    }


    /**
     * Returns if the configured DAO interface has custom methods, that might
     * have to be delegated to a custom DAO implementation. This is used to
     * verify DAO configuration.
     * 
     * @return
     */
    private boolean hasCustomMethod(
            Class<? extends GenericDao<?, ?>> daoInterface) {

        boolean hasCustomMethod = false;

        // No detection required if no typing interface was configured
        if (ClassUtils.isGenericDaoInterface(daoInterface)) {
            return false;
        }

        for (Method method : daoInterface.getMethods()) {

            if (isCustomMethod(method, daoInterface)
                    && !isBaseClassMethod(method, daoInterface)) {
                return true;
            }
        }

        return hasCustomMethod;
    }


    /**
     * Returns whether the given method is considered to be a DAO base class
     * method.
     * 
     * @param method
     * @return
     */
    private boolean isBaseClassMethod(Method method, Class<?> daoInterface) {

        Assert.notNull(method);

        if (method.getDeclaringClass().isAssignableFrom(getDaoClass())) {
            return true;
        }

        return !method.equals(getBaseClassMethod(method, daoInterface));
    }


    /**
     * Returns the base class method that is backing the given method. This can
     * be necessary if a DAO interface redeclares a method in {@link GenericDao}
     * (e.g. for transaction behaviour customization). Returns the method itself
     * if the base class does not implement the given method.
     * 
     * @param method
     * @return
     */
    private Method getBaseClassMethod(Method method, Class<?> daoInterface) {

        Assert.notNull(method);

        Method result = methodCache.get(method);

        if (null != result) {
            return result;
        }

        result = getBaseClassMethodFor(method, getDaoClass(), daoInterface);
        methodCache.put(method, result);

        return result;
    }


    /**
     * Returns whether the given method is a custom DAO method.
     * 
     * @param method
     * @param daoInterface
     * @return
     */
    private boolean isCustomMethod(Method method, Class<?> daoInterface) {

        Class<?> declaringClass = method.getDeclaringClass();

        boolean isQueryMethod = declaringClass.equals(daoInterface);
        boolean isHadesDaoInterface =
                ClassUtils.isGenericDaoInterface(declaringClass);
        boolean isBaseClassMethod = isBaseClassMethod(method, daoInterface);

        return !(isHadesDaoInterface || isBaseClassMethod || isQueryMethod);
    }


    /**
     * Returns all
     * 
     * @param daoInterface
     * @return
     */
    private Iterable<Method> getFinderMethods(Class<?> daoInterface) {

        Set<Method> result = new HashSet<Method>();

        for (Method method : daoInterface.getDeclaredMethods()) {
            if (!isCustomMethod(method, daoInterface)
                    && !isBaseClassMethod(method, daoInterface)) {
                result.add(method);
            }
        }

        return result;
    }


    /**
     * Validates the given DAO interface.
     * 
     * @param daoInterface
     */
    private void validate(Class<?> daoInterface) {

        Assert.notNull(daoInterface);

        Class<?> domainClass = ClassUtils.getDomainClass(daoInterface);

        if (null == domainClass) {
            throw new IllegalArgumentException(
                    "Could not retrieve domain class from interface. Make sure it extends GenericDao.");
        }
    }


    /**
     * Validates the given DAO interface as well as the given custom
     * implementation.
     * 
     * @param daoInterface
     * @param customDaoImplementation
     */
    protected void validate(Class<? extends GenericDao<?, ?>> daoInterface,
            Object customDaoImplementation) {

        validate(daoInterface);

        if (null == customDaoImplementation && hasCustomMethod(daoInterface)) {

            throw new IllegalArgumentException(
                    String
                            .format(
                                    "You have custom methods in %s but not provided a custom implementation!",
                                    daoInterface));
        }
    }

    /**
     * This {@code MethodInterceptor} intercepts calls to methods of the custom
     * implementation and delegates the to it if configured. Furthermore it
     * resolves method calls to finders and triggers execution of them. You can
     * rely on having a custom dao implementation instance set if this returns
     * true.
     * 
     * @author Oliver Gierke
     */
    private class QueryExecuterMethodInterceptor implements MethodInterceptor {

        private final Map<Method, HadesQuery> queries =
                new ConcurrentHashMap<Method, HadesQuery>();

        private final Object customDaoImplementation;
        private final Class<?> daoInterface;
        private final GenericDaoSupport<?> dao;


        /**
         * Creates a new {@link QueryExecuterMethodInterceptor}. Builds a model
         * of {@link QueryMethod}s to be invoked on execution of DAO interface
         * methods.
         */
        public QueryExecuterMethodInterceptor(Class<?> daoInterface,
                Object customDaoImplementation, GenericDaoSupport<?> dao) {

            this.daoInterface = daoInterface;
            this.customDaoImplementation = customDaoImplementation;
            this.dao = dao;

            for (Method method : getFinderMethods(daoInterface)) {

                Class<?> domainClass = ClassUtils.getDomainClass(daoInterface);
                QueryExtractor extractor =
                        PersistenceProvider.fromEntityManager(entityManager);

                QueryMethod finder =
                        new QueryMethod(method, domainClass, entityManager,
                                extractor);

                queries.put(method, queryLookupStrategy.resolveQuery(finder));
            }
        }


        /*
         * (non-Javadoc)
         * 
         * @see
         * org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance
         * .intercept.MethodInvocation)
         */
        public Object invoke(final MethodInvocation invocation)
                throws Throwable {

            Method method = invocation.getMethod();

            if (isCustomMethodInvocation(invocation)) {

                makeAccessible(method);
                return executeMethodOn(customDaoImplementation, method,
                        invocation.getArguments());
            }

            if (hasQueryFor(method)) {
                return queries.get(method).execute(invocation.getArguments());
            }

            // Lookup actual method as it might be redeclared in the interface
            // and we have to use the dao instance nevertheless
            // Method actualMethod = getBaseClassMethod(method);
            Method actualMethod = getBaseClassMethod(method, daoInterface);
            return executeMethodOn(dao, actualMethod, invocation.getArguments());
        }


        /**
         * Executes the given method on the given target. Correctly unwraps
         * exceptions not caused by the reflection magic.
         * 
         * @param target
         * @param method
         * @param parameters
         * @return
         * @throws Throwable
         */
        private Object executeMethodOn(Object target, Method method,
                Object[] parameters) throws Throwable {

            try {
                return method.invoke(target, parameters);
            } catch (Exception e) {
                ClassUtils.unwrapReflectionException(e);
            }

            throw new IllegalStateException("Should not occur!");
        }


        /**
         * Returns whether we know of a query to execute for the given
         * {@link Method};
         * 
         * @param method
         * @return
         */
        private boolean hasQueryFor(final Method method) {

            return queries.containsKey(method);
        }


        /**
         * Returns whether the given {@link MethodInvocation} is considered to
         * be targeted as an invocation of a custom method.
         * 
         * @param method
         * @return
         */
        private boolean isCustomMethodInvocation(
                final MethodInvocation invocation) {

            if (null == customDaoImplementation) {
                return false;
            }

            return isCustomMethod(invocation.getMethod(), daoInterface);
        }
    }
}

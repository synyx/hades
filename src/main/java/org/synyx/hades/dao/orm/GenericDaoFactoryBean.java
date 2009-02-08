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

package org.synyx.hades.dao.orm;

import java.lang.reflect.Method;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.synyx.hades.core.QueryLookupStrategy;
import org.synyx.hades.dao.ExtendedGenericDao;
import org.synyx.hades.dao.FinderExecuter;
import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.domain.Persistable;


/**
 * Factory bean to create instances of a given DAO interface. Creates a proxy
 * implementing the configured DAO interface and apply an advice handing the
 * control to the {@code FinderExecuter} when a method beginning with "find" is
 * called.
 * <p>
 * E.g. if you define a method {@code findByName} on an interface extending
 * {@code GenericDao<User, Integer>} the advice will try to call a named query
 * named {@code User.findByName}. The advice can distinguish between calls for
 * lists or a single instance by checking the return value of the defined
 * method.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 * @author Eberhard Wolff
 * @param <T> the type of the entity to handle by the DAO
 */
public class GenericDaoFactoryBean<T extends Persistable<?>> implements
        FactoryBean, InitializingBean {

    @SuppressWarnings("unchecked")
    private static final Class<GenericJpaDao> DEFAULT_DAO_CLASS =
            GenericJpaDao.class;

    private Class<? extends GenericDao<T, ?>> daoInterface;
    private Class<T> domainClass;
    private Object customDaoImplementation;
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    private Class<? extends AbstractJpaFinder> daoClass;
    private boolean configureManually;

    private QueryLookupStrategy queryLookupStrategy =
            AbstractJpaFinder.DEFAULT_QUERY_LOOKUP_STRATEGY;
    private String finderPrefix = AbstractJpaFinder.DEFAULT_FINDER_PREFIX;


    public static <T extends Persistable<?>, D extends GenericDao<T, ?>> GenericDaoFactoryBean<T> create(
            Class<T> domainClass,
            Class<? extends GenericDao<T, ?>> daoInterface, EntityManager em) {

        GenericDaoFactoryBean<T> factory = new GenericDaoFactoryBean<T>();
        factory.setDomainClass(domainClass);
        factory.setDaoInterface(daoInterface);
        factory.setEntityManager(em);

        return factory;
    }


    /**
     * Setter to inject the dao interface to implement. Defaults to
     * {@link GenericDao}.
     * 
     * @param daoInterface the daoInterface to set
     */
    @Required
    public void setDaoInterface(
            final Class<? extends GenericDao<T, ?>> daoInterface) {

        Assert.notNull(daoInterface);
        Assert.isAssignable(GenericDao.class, daoInterface,
                "DAO interface has to implement at least GenericDao!");

        this.daoInterface = daoInterface;
    }


    /**
     * Setter to inject a custom DAO implementation. This class needs
     * 
     * @param customDaoImplementation the customDaoImplementation to set
     */
    public void setCustomDaoImplementation(Object customDaoImplementation) {

        this.customDaoImplementation = customDaoImplementation;
    }


    /**
     * Setter to inject the domain class to manage.
     * 
     * @param domainClass the domainClass to set
     */
    @Required
    public void setDomainClass(final Class<T> domainClass) {

        Assert.notNull(domainClass);
        Assert.isAssignable(Persistable.class, domainClass,
                "Domain class has to implement at least Persistable!");

        this.domainClass = domainClass;
    }


    /**
     * Setter to inject a custom DAO base class. If this setter is not called
     * the factory will use {@link GenericJpaDao} as implementation base class
     * as default.
     * 
     * @param daoClass the daoClass to set
     */
    @SuppressWarnings("unchecked")
    public <D extends AbstractJpaFinder<T, ?>> void setDaoClass(
            final Class<D> daoClass) {

        if (null == daoClass) {

            this.daoClass = DEFAULT_DAO_CLASS;
            this.configureManually = false;
            return;
        }

        Assert.isAssignable(AbstractJpaFinder.class, daoClass,
                "DAO base class has to extend AbstractJpaFinder!");

        this.daoClass = (Class<AbstractJpaFinder<T, ?>>) daoClass;
        this.configureManually = true;
    }


    /**
     * Sets the strategy of how to lookup a query to execute finders.
     * 
     * @param queryLookupStrategy the createFinderQueries to set
     */
    public void setQueryLookupStrategy(QueryLookupStrategy queryLookupStrategy) {

        this.queryLookupStrategy = queryLookupStrategy;
    }


    /**
     * Configures the method name prefix that triggers automatic finder
     * execution.
     * 
     * @see AbstractJpaFinder#setFinderPrefix(String)
     * @param finderPrefix
     */
    public void setFinderPrefix(String finderPrefix) {

        this.finderPrefix = finderPrefix;
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


    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    @SuppressWarnings("unchecked")
    public Object getObject() throws Exception {

        // Instantiate generic dao
        AbstractJpaFinder<T, ?> genericJpaDao = daoClass.newInstance();
        genericJpaDao.setEntityManager(entityManager);
        genericJpaDao.setDomainClass(domainClass);
        genericJpaDao.setCreateFinderQueries(queryLookupStrategy);
        genericJpaDao.setFinderPrefix(finderPrefix);

        genericJpaDao.afterPropertiesSet();

        // Create proxy
        ProxyFactory result = new ProxyFactory();
        result.setTarget(genericJpaDao);
        result.setInterfaces(new Class[] { daoInterface });
        result.addAdvice(new DelegatingMethodInterceptor());

        return result.getProxy();
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    public Class<?> getObjectType() {

        return daoInterface;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#isSingleton()
     */
    public boolean isSingleton() {

        return true;
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {

        Assert.notNull(daoInterface);
        Assert.notNull(domainClass);

        if (null == customDaoImplementation && hasCustomMethod()) {

            throw new BeanCreationException(
                    String
                            .format(
                                    "You have custom methods in %s but not provided a custom implementation!",
                                    daoInterface));
        }

        if (!configureManually) {
            this.daoClass = autoDetectDaoClass();
        }

        if (isExtendedDaoInterface()
                && !ExtendedGenericDao.class.isAssignableFrom(daoClass)) {

            throw new BeanCreationException(
                    "If you want to create ExtendedGenericDao instances you "
                            + "have to provide an implementation base class that "
                            + "implements this interface!");
        }
    }


    /**
     * Returns if the configured DAO interface has custom methods, that might
     * have to be delegated to a custom DAO implementation. This is used to
     * verify DAO configuration.
     * 
     * @return
     */
    private boolean hasCustomMethod() {

        boolean hasCustomMethod = false;

        // No detection required if no typing interface was configured
        if (GenericDao.class.equals(daoInterface)) {
            return false;
        }

        for (Method method : daoInterface.getMethods()) {

            // Skip finder methods
            if (method.getName().startsWith(finderPrefix)) {
                continue;
            }

            // Skip methods of super interfaces
            if (!method.getDeclaringClass().equals(daoInterface)) {
                continue;
            }

            hasCustomMethod = true;
            break;

        }

        return hasCustomMethod;
    }


    /**
     * Determines the base class for the DAO to be created by checking the
     * {@link EntityManager}'s concrete type. If no well known type can be
     * detected {@link #DEFAULT_DAO_CLASS} will be returned.
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    private Class<? extends GenericJpaDao> autoDetectDaoClass() {

        if (isEntityManagerOfType(entityManager,
                "org.hibernate.ejb.HibernateEntityManager")) {
            return GenericHibernateJpaDao.class;
        }

        if (isEntityManagerOfType(entityManager,
                "org.eclipse.persistence.jpa.JpaEntityManager")) {
            return GenericEclipseLinkJpaDao.class;
        }

        return DEFAULT_DAO_CLASS;
    }


    /**
     * Returns whether the given {@link EntityManager} is of the given type.
     * 
     * @param em
     * @param type the fully qualified expected {@link EntityManager} type.
     * @return
     */
    @SuppressWarnings("unchecked")
    private boolean isEntityManagerOfType(EntityManager em, String type) {

        try {

            Class<? extends EntityManager> emType =
                    (Class<? extends EntityManager>) Class.forName(type);

            emType.cast(em);

            return true;

        } catch (ClassNotFoundException e) {
            return false;
        } catch (ClassCastException e) {
            return false;
        }
    }


    /**
     * Returns if the DAO bean to be created shall implement
     * {@link ExtendedGenericDao}.
     * 
     * @return
     */
    private boolean isExtendedDaoInterface() {

        return ExtendedGenericDao.class.isAssignableFrom(daoInterface);
    }

    /**
     * This {@code MethodInterceptor} intercepts calls to methods of the custom
     * implementation and delegates the to it if configured. Furthermore it
     * resolves method calls to finders and triggers execution of them. You can
     * rely on having a custom dao implementation instance set if this returns
     * true.
     * 
     * @author Oliver Gierke - gierke@synyx.de
     */
    private class DelegatingMethodInterceptor implements MethodInterceptor {

        /**
         * Returns if the given call is a call to a method of the custom
         * implementation.
         * 
         * @param invocation
         * @return
         */
        private boolean isCallToCustomMethod(MethodInvocation invocation) {

            if (null == customDaoImplementation) {
                return false;
            }

            Class<?> declaringClass =
                    invocation.getMethod().getDeclaringClass();

            return declaringClass.isInstance(customDaoImplementation);
        }


        /*
         * (non-Javadoc)
         * 
         * @see
         * org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance
         * .intercept.MethodInvocation)
         */
        @SuppressWarnings("unchecked")
        public Object invoke(final MethodInvocation invocation)
                throws Throwable {

            if (isCallToCustomMethod(invocation)) {

                return invocation.getMethod().invoke(customDaoImplementation,
                        invocation.getArguments());
            }

            Method method = invocation.getMethod();

            if (method.getName().startsWith(finderPrefix)) {

                FinderExecuter<T> target =
                        (FinderExecuter<T>) invocation.getThis();

                Class<?> returnType = invocation.getMethod().getReturnType();

                // Execute finder for single object if domain class type is
                // assignable to the methods return type, else finder for a
                // list
                // of objects
                if (ClassUtils.isAssignable(domainClass, returnType)) {
                    return target.executeObjectFinder(method, invocation
                            .getArguments());
                } else {
                    return target.executeFinder(method, invocation
                            .getArguments());
                }
            }

            return invocation.proceed();
        }
    }
}

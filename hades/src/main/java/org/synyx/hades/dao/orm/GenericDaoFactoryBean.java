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

import javax.persistence.EntityManager;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.support.PersistenceExceptionTranslationInterceptor;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.util.Assert;
import org.synyx.hades.dao.GenericDao;


/**
 * Special {@link GenericDaoFactory} adapter for Springs {@link FactoryBean}
 * interface to allow easy setup of DAO factories via Spring configuration.
 * 
 * @author Oliver Gierke
 * @author Eberhard Wolff
 * @param <T> the type of the DAO
 */
public class GenericDaoFactoryBean<T extends GenericDao<?, ?>> extends
        GenericDaoFactory implements FactoryBean<T>, InitializingBean,
        BeanFactoryAware {

    private Class<? extends T> daoInterface;
    private Object customDaoImplementation;

    private String transactionManagerName;

    private TransactionInterceptor transactionInterceptor;
    private PersistenceExceptionTranslationInterceptor petInterceptor;
    private String transactionManagerName = TxUtils.DEFAULT_TRANSACTION_MANAGER;


    /**
     * Creates a new {@link GenericDaoFactoryBean}.
     * 
     * @param <T>
     * @param daoInterface
     * @param em
     * @return
     */
    public static <T extends GenericDao<?, ?>> GenericDaoFactoryBean<T> create(
            Class<T> daoInterface, EntityManager em) {

        GenericDaoFactoryBean<T> factory = new GenericDaoFactoryBean<T>();
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
    public void setDaoInterface(final Class<T> daoInterface) {

        Assert.notNull(daoInterface);

        this.daoInterface = daoInterface;
    }


    /**
     * Setter to configure which transaction manager to be used. We have to use
     * the bean name explicitly as otherwise the qualifier of the
     * {@link org.springframework.transaction.annotation.Transactional}
     * annotation is used. By explicitly defining the transaction manager bean
     * name we favour let this one be the default one chosen.
     * 
     * @param transactionManager
     */
    public void setTransactionManager(String transactionManager) {

        this.transactionManagerName =
                transactionManager == null ? TxUtils.DEFAULT_TRANSACTION_MANAGER
                        : transactionManager;
    }


    /**
     * Setter to inject a custom DAO implementation. This class needs
     * 
     * @param customDaoImplementation the customDaoImplementation to set
     */
    public void setCustomDaoImplementation(Object customDaoImplementation) {

        this.customDaoImplementation = customDaoImplementation;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    public T getObject() {

        return getDao(daoInterface, customDaoImplementation);
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    @SuppressWarnings("unchecked")
    public Class<? extends T> getObjectType() {

        return (Class<? extends T>) (null == daoInterface ? GenericDao.class
                : daoInterface);
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
    public void afterPropertiesSet() {

        Assert.notNull(getEntityManager(), "EntityManager must not be null!");

        validate(daoInterface, customDaoImplementation);

    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org
     * .springframework.beans.factory.BeanFactory)
     */
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {

        this.petInterceptor = new PersistenceExceptionTranslationInterceptor();
        this.petInterceptor.setBeanFactory(beanFactory);
        this.petInterceptor.afterPropertiesSet();

        this.transactionInterceptor =
                new TransactionInterceptor(null,
                        new AnnotationTransactionAttributeSource());
        this.transactionInterceptor
                .setTransactionManagerBeanName(transactionManagerName);
        this.transactionInterceptor.setBeanFactory(beanFactory);
        this.transactionInterceptor.afterPropertiesSet();
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.synyx.hades.dao.orm.GenericDaoFactory#prepare(org.springframework
     * .aop.framework.ProxyFactory)
     */
    @Override
    protected void prepare(ProxyFactory factory) {

        if (petInterceptor != null) {
            factory.addAdvice(petInterceptor);
        }

        if (transactionInterceptor != null) {
            factory.addAdvice(transactionInterceptor);
        }
    }
}

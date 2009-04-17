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

import javax.persistence.EntityManager;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.synyx.hades.dao.GenericDao;


/**
 * Special {@link GenericDaoFactory} adapter for Springs {@link FactoryBean}
 * interface to allow easy setup of DAO factories via Spring configuration.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 * @author Eberhard Wolff
 * @param <T> the type of the DAO
 */
public class GenericDaoFactoryBean<T extends GenericDao<?, ?>> extends
        GenericDaoFactory implements FactoryBean, InitializingBean {

    private Class<? extends T> daoInterface;
    private Object customDaoImplementation;


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
    public void setDaoInterface(final Class<? extends T> daoInterface) {

        Assert.notNull(daoInterface);

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


    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    public Object getObject() throws Exception {

        return getDao(daoInterface, customDaoImplementation);
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

        Assert.notNull(getEntityManager(), "EntityManager must not be null!");

        validate(daoInterface, customDaoImplementation);
    }
}
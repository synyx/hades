/*
 * Copyright 2002-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.synyx.hades.dao.config;

import org.springframework.util.StringUtils;
import org.synyx.hades.dao.orm.QueryLookupStrategy;


/**
 * Interface defining access to the DAO configuration.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
abstract class DaoConfigContext {

    protected static final String DEFAULT_DAO_BEAN_POSTFIX = "Dao";
    protected static final String DEFAULT_DAO_INTERFACE_POSTFIX = "Dao";
    protected static final String DEFAULT_DAO_IMPL_POSTFIX = "DaoImpl";
    protected static final QueryLookupStrategy DEFAULT_CREATE_FINDER_QUERIES = QueryLookupStrategy.CREATE_IF_NOT_FOUND;


    /**
     * Returns the full qualified domain class name.
     * 
     * @param simpleName
     * @return
     */
    public String getDomainClassName(String simpleName) {

        return getEntityPackageName() + "."
                + StringUtils.capitalize(simpleName);
    }


    /**
     * Returns the bean name to be used for the DAO.
     * 
     * @param simpleName
     * @return
     */
    public String getBeanName(String simpleName) {

        return simpleName + getDaoNamePostfix();
    }


    /**
     * Returns the name of the DAO interface.
     * 
     * @param simpleName
     * @return
     */
    public String getInterfaceName(String simpleName) {

        return getDaoPackageName() + "." + StringUtils.capitalize(simpleName)
                + getDaoInterfacePostfix();
    }


    /**
     * Returns the full qualified class name of a possible custom DAO
     * implementation class to detect.
     * 
     * @param name
     * @return
     */
    public String getImplementationClassName(String name) {

        return getDaoPackageName() + "." + StringUtils.capitalize(name)
                + getDaoImplPostfix();
    }


    /**
     * Returns the strategy finder methods should be resolved.
     * 
     * @return
     */
    public abstract QueryLookupStrategy getFinderLookupStrategy();


    /**
     * Returns if the bean instances are configured manually.
     * 
     * @return
     */
    public abstract boolean configureManually();


    /**
     * Returns the DAO package name.
     * 
     * @return the daoPackageName
     */
    protected abstract String getDaoPackageName();


    /**
     * Returns the entity package name.
     * 
     * @return the entityPackageName
     */
    protected abstract String getEntityPackageName();


    /**
     * Returns the DAO interface postfix. This postfix is used to construct the
     * DAO interface name from the {@code dao} configuration elements. Defaults
     * to {@value #DEFAULT_DAO_BEAN_POSTFIX}.
     * 
     * @return the daoClassPostfix
     */
    protected abstract String getDaoInterfacePostfix();


    /**
     * Returns the DAO bean name postfix. Defaults to
     * {@value #DEFAULT_DAO_BEAN_POSTFIX}.
     * 
     * @return the daoNamePostfix
     */
    protected abstract String getDaoNamePostfix();


    /**
     * Returns the DAOs base class' name.
     * 
     * @return the daoBaseClassName
     */
    protected abstract String getDaoBaseClassName();


    /**
     * Returns the postfix used to lookup custom DAO implementations. Defaults
     * to {@value #DEFAULT_DAO_IMPL_POSTFIX}.
     * 
     * @return
     */
    protected abstract String getDaoImplPostfix();

}
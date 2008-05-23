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

/**
 * Interface defining access to the DAO configuration.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
interface DaoConfigContext {

    /**
     * Returns if the bean instances are configured manually.
     * 
     * @return
     */
    boolean configureManually();


    /**
     * Returns the DAO package name.
     * 
     * @return the daoPackageName
     */
    String getDaoPackageName();


    /**
     * Returns the entity package name.
     * 
     * @return the entityPackageName
     */
    String getEntityPackageName();


    /**
     * Returns the DAO class postfix.
     * 
     * @return the daoClassPostfix
     */
    String getDaoClassPostfix();


    /**
     * Returns the DAO bean name postfix.
     * 
     * @return the daoNamePostfix
     */
    String getDaoNamePostfix();


    /**
     * Returns the DAOs base class' name.
     * 
     * @return the daoBaseClassName
     */
    String getDaoBaseClassName();
}
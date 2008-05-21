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
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
    public abstract boolean configureManually();


    /**
     * Returns the DAO package name.
     * 
     * @return the daoPackageName
     */
    public abstract String getDaoPackageName();


    /**
     * Returns the entity package name.
     * 
     * @return the entityPackageName
     */
    public abstract String getEntityPackageName();


    /**
     * Returns the DAO class postfix.
     * 
     * @return the daoClassPostfix
     */
    public abstract String getDaoClassPostfix();


    /**
     * Returns the DAO bean name postfix.
     * 
     * @return the daoNamePostfix
     */
    public abstract String getDaoNamePostfix();


    /**
     * Returns the DAOs base class' name.
     * 
     * @return the daoBaseClassName
     */
    public abstract String getDaoBaseClassName();
}
package com.synyx.jpa.support.namespace;

import java.io.Serializable;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import com.synyx.jpa.support.GenericDao;
import com.synyx.jpa.support.GenericDaoFactoryBean;


/**
 * A factory bean getting a DAO and entity package name injected and thus
 * constructing class names from it.
 * <ul>
 * <li>DAO Interface: <code>${daoPackageName}.${name}${daoClassPostfix}</code></li>
 * <li>Entity class: <code>${entityPackageName}.${name}</code></li>
 * </ul>
 * <code>${daoClassPostfix}</code> defaults to <code>Dao</code> an thus is
 * not required.
 * 
 * @author Eberhard Wolff
 * @author Oliver Gierke
 */
public class NamespaceGenericDaoFactoryBean<T, PK extends Serializable> extends
        GenericDaoFactoryBean<T, PK> implements InitializingBean {

    private String daoPackageName;
    private String entityPackageName;

    private String daoClassPostfix = "Dao";
    private String name;


    /**
     * Sets the name of the dao package.
     * 
     * @param daoPackageName
     */
    @Required
    public void setDaoPackageName(String daoPackageName) {

        this.daoPackageName = daoPackageName;
    }


    /**
     * Sets the dao postfix.
     * 
     * @param daoClassPostfix
     */

    public void setDaoClassPostfix(String daoClassPostfix) {

        this.daoClassPostfix = daoClassPostfix;
    }


    /**
     * Sets the name of the entity package.
     * 
     * @param entityPackageName
     */
    @Required
    public void setEntityPackageName(String entityPackageName) {

        this.entityPackageName = entityPackageName;
    }


    /**
     * Sets the name of the bean.
     * 
     * @param name
     */
    @Required
    public void setName(String name) {

        this.name = name;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @SuppressWarnings("unchecked")
    public void afterPropertiesSet() throws Exception {

        String entityName = name.substring(0, 1).toUpperCase()
                + name.substring(1);

        Class<?> daoInterface = Class.forName(daoPackageName + "." + entityName
                + daoClassPostfix);

        // Check for extension of GenericDao
        if (!GenericDao.class.isAssignableFrom(daoInterface)) {
            throw new IllegalArgumentException(
                    "Dao interface hast to extend GenericDao.");
        }

        setDaoInterface((Class<GenericDao<T, PK>>) daoInterface);
        setEntityClass((Class<T>) Class.forName(entityPackageName + "."
                + entityName));
    }
}

package org.synyx.hades.dao.orm.support;

import java.io.Serializable;

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
import org.synyx.hades.dao.ExtendedGenericDao;
import org.synyx.hades.dao.FinderExecuter;
import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.dao.orm.AbstractJpaFinder;
import org.synyx.hades.dao.orm.GenericJpaDao;
import org.synyx.hades.domain.Persistable;


/**
 * Factory bean to create instances of a given DAO interface. Creates a proxy
 * implementing the configured DAO interface and apply an advice handing the
 * control to the <code>FinderExecuter</code> when a method beginning with
 * "find" is called.
 * <p>
 * E.g. if you define a method <code>findByName</code> on an interface
 * extending <code>GenericDao&lt;User, Integer&gt;</code> the advice will try
 * to call a named query named <code>User.findByName</code>.
 * 
 * @author Eberhard Wolff
 * @author Oliver Gierke
 */
@SuppressWarnings("unchecked")
public class GenericDaoFactoryBean<D extends AbstractJpaFinder<T, PK>, T extends Persistable<PK>, PK extends Serializable>
        implements FactoryBean, InitializingBean {

    public static final Class<?> DEFAULT_DAO_IMPLEMENTATION = GenericJpaDao.class;

    private Class<GenericDao<T, PK>> daoInterface;

    private Class<T> domainClass;
    private Class<D> daoClass = (Class<D>) DEFAULT_DAO_IMPLEMENTATION;

    private EntityManager entityManager;


    /**
     * Setter to inject the dao interface to implement.
     * 
     * @param daoInterface the daoInterface to set
     */
    @Required
    public void setDaoInterface(Class<GenericDao<T, PK>> daoInterface) {

        Assert.notNull(daoInterface);
        Assert.isAssignable(GenericDao.class, daoInterface,
                "DAO interface has to implement at least GenericDao!");

        this.daoInterface = daoInterface;
    }


    /**
     * Setter to inject the domain class to manage.
     * 
     * @param domainClass the domainClass to set
     */
    @Required
    public void setDomainClass(Class<T> domainClass) {

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
    public void setDaoClass(Class<D> daoClass) {

        Assert.notNull(daoClass);
        Assert.isAssignable(GenericDao.class, daoClass,
                "DAO base class has to implement at least GenericDao!");

        this.daoClass = daoClass;
    }


    /**
     * Setter to inject entity manager.
     * 
     * @param entityManager the entityManager to set
     */
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {

        this.entityManager = entityManager;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    public Object getObject() throws Exception {

        // Instantiate generic dao
        D genericJpaDao = daoClass.newInstance();
        genericJpaDao.setDomainClass(domainClass);
        genericJpaDao.setEntityManager(entityManager);

        // Create proxy
        ProxyFactory result = new ProxyFactory();
        result.setTarget(genericJpaDao);
        result.setInterfaces(new Class[] { daoInterface });

        // Add advice to intercept method calls to "find*"
        result.addAdvice(new MethodInterceptor() {

            /*
             * (non-Javadoc)
             * 
             * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
             */
            @SuppressWarnings("unchecked")
            public Object invoke(MethodInvocation invocation) throws Throwable {

                String methodName = invocation.getMethod().getName();

                if (methodName.startsWith("find")) {
                    FinderExecuter<T> target = (FinderExecuter<T>) invocation
                            .getThis();
                    return target.executeFinder(methodName, invocation
                            .getArguments());
                } else {

                    return invocation.proceed();
                }
            }
        });

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
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {

        Assert.notNull(daoInterface);
        Assert.notNull(domainClass);

        if (isExtendedDaoInterface()
                && !ExtendedGenericDao.class.isAssignableFrom(daoClass)) {
            throw new BeanCreationException(
                    "If you want to create ExtendedGenericDao instances you "
                            + "have to provide an implementation base class that "
                            + "implements this interface!");
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
}

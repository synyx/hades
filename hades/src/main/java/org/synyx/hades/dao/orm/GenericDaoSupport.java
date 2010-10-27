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

import static org.synyx.hades.dao.query.QueryUtils.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.util.ReflectionUtils.MethodCallback;
import org.synyx.hades.domain.Persistable;


/**
 * Abstract base class for generic DAOs.
 * 
 * @author Oliver Gierke
 * @param <T> the type of entity to be handled
 */
public abstract class GenericDaoSupport<T> {

    private EntityManager entityManager;
    private Class<T> domainClass;
    private IsNewAware isNewStrategy;


    /**
     * Returns the {@link EntityManager}.
     * 
     * @return
     */
    protected EntityManager getEntityManager() {

        return this.entityManager;
    }


    /**
     * Setter to inject {@code EntityManager}.
     * 
     * @param entityManager
     */
    @PersistenceContext
    public void setEntityManager(final EntityManager entityManager) {

        this.entityManager = entityManager;

        validate();
    }


    /**
     * Returns the domain class to handle.
     * 
     * @return the domain class
     */
    protected Class<T> getDomainClass() {

        return domainClass;
    }


    /**
     * Sets the domain class to handle.
     * 
     * @param domainClass the domain class to set
     */
    public void setDomainClass(final Class<T> domainClass) {

        this.domainClass = domainClass;
        createIsNewStrategy(domainClass);
    }


    /**
     * Returns the query string to retrieve all entities.
     * 
     * @return string to retrieve all entities
     */
    protected String getReadAllQueryString() {

        return getQueryString(READ_ALL_QUERY, getDomainClass());
    }


    /**
     * Returns the query string to delete all entities.
     * 
     * @return string to delete all entities
     */
    protected String getDeleteAllQueryString() {

        return getQueryString(DELETE_ALL_QUERY_STRING, getDomainClass());
    }


    /**
     * Returns the query string to count entities.
     * 
     * @return string to count entities
     */
    @Deprecated
    protected String getCountQueryString() {

        PersistenceProvider provider =
                PersistenceProvider.fromEntityManager(getEntityManager());
        String countQuery =
                String.format(COUNT_QUERY_STRING,
                        provider.getCountQueryPlaceholder(), "%s");

        return getQueryString(countQuery, getDomainClass());
    }


    /**
     * Returns the query to retrieve all entities.
     * 
     * @return the query to retrieve all entities.
     */
    protected TypedQuery<T> getReadAllQuery() {

        return getEntityManager().createQuery(getReadAllQueryString(),
                getDomainClass());
    }


    /**
     * Asserts that the {@code EntityManager} implementation being used by the
     * dao is an instance of the given type.
     * 
     * @param clazz
     * @throws IllegalArgumentException if the entity manager is not of the
     *             given type
     */
    protected void assertEntityManagerClass(Class<? extends EntityManager> clazz) {

        Assert.isInstanceOf(clazz, entityManager, String.format(
                "%s can only be used with %s implementation! "
                        + "Please check configuration or use %s instead!",
                getClass().getSimpleName(), clazz.getSimpleName(),
                GenericJpaDao.class.getSimpleName()));
    }


    /**
     * Callback method to validate the class setup.
     */
    public void validate() {

        if (null == entityManager) {
            throw new IllegalStateException("EntityManager must not be null!");
        }
    }


    /**
     * Return whether the given entity is to be regarded as new. Default
     * implementation will inspect the given domain class and use either
     * {@link PersistableEntityInformation} if the class implements
     * {@link Persistable} or {@link ReflectiveEntityInformation} otherwise.
     * 
     * @param entity
     * @return
     */
    protected void createIsNewStrategy(Class<?> domainClass) {

        if (Persistable.class.isAssignableFrom(domainClass)) {
            this.isNewStrategy = new PersistableEntityInformation();
        } else {
            this.isNewStrategy = new ReflectiveEntityInformation(domainClass);
        }
    }


    /**
     * Returns the strategy how to determine whether an entity is to be regarded
     * as new.
     * 
     * @return the isNewStrategy
     */
    protected IsNewAware getIsNewStrategy() {

        return isNewStrategy;
    }

    /**
     * Interface to abstract the ways to determine if the given entity is to be
     * considered as new.
     * 
     * @author Oliver Gierke
     */
    public interface IsNewAware {

        boolean isNew(Object entity);
    }

    /**
     * Interface to abstract the ways to retrieve the id of the given entity.
     * 
     * @author Oliver Gierke
     */
    public interface IdAware {

        Object getId(Object entity);
    }

    /**
     * Implementation of {@link IsNewAware} that assumes the entity handled
     * implements {@link Persistable} and uses {@link Persistable#isNew()} for
     * the {@link #isNew(Object)} check.
     * 
     * @author Oliver Gierke
     */
    public static class PersistableEntityInformation implements IsNewAware,
            IdAware {

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.synyx.hades.dao.orm.GenericJpaDao.IsNewStrategy#isNew(java.lang
         * .Object)
         */
        public boolean isNew(Object entity) {

            return ((Persistable<?>) entity).isNew();
        }


        /*
         * (non-Javadoc)
         * 
         * @see
         * org.synyx.hades.dao.orm.GenericDaoSupport.IdAware#getId(java.lang
         * .Object)
         */
        public Object getId(Object entity) {

            return ((Persistable<?>) entity).getId();
        }

    }

    /**
     * {@link IsNewAware} implementation that reflectively checks a
     * {@link Field} or {@link Method} annotated with {@link Id}.
     * 
     * @author Oliver Gierke
     */
    public static class ReflectiveEntityInformation implements IsNewAware,
            IdAware {

        @SuppressWarnings("unchecked")
        private static final List<Class<? extends Annotation>> ID_ANNOTATIONS =
                Arrays.asList(Id.class, EmbeddedId.class);

        private Field field;
        private Method method;


        /**
         * Creates a new {@link ReflectiveEntityInformation} by inspecting the
         * given class for a {@link Field} or {@link Method} for and {@link Id}
         * annotation.
         * 
         * @param domainClass not {@literal null}, must be annotated with
         *            {@link Entity} and carry an anootation defining the id
         *            property.
         */
        public ReflectiveEntityInformation(Class<?> domainClass) {

            Assert.notNull(domainClass);
            Assert.isTrue(domainClass.isAnnotationPresent(Entity.class),
                    "Given domain class was not annotated with @Entity!");

            ReflectionUtils.doWithFields(domainClass, new FieldCallback() {

                public void doWith(Field field) {

                    if (ReflectiveEntityInformation.this.field != null) {
                        return;
                    }

                    if (hasAnnotation(field, ID_ANNOTATIONS)) {
                        ReflectiveEntityInformation.this.field = field;
                    }
                }
            });

            if (field != null) {
                return;
            }

            ReflectionUtils.doWithMethods(domainClass, new MethodCallback() {

                public void doWith(Method method) {

                    if (ReflectiveEntityInformation.this.method != null) {
                        return;
                    }

                    if (hasAnnotation(method, ID_ANNOTATIONS)) {
                        ReflectiveEntityInformation.this.method = method;
                    }
                }
            });

            Assert.isTrue(this.field != null || this.method != null,
                    "No id method or field found!");
        }


        /**
         * Checks whether the given {@link AnnotatedElement} carries one of the
         * given {@link Annotation}s.
         * 
         * @param <A>
         * @param annotatedElement
         * @param annotations
         * @return
         */
        private <A extends Annotation> boolean hasAnnotation(
                AnnotatedElement annotatedElement,
                List<Class<? extends A>> annotations) {

            for (Class<? extends A> annotation : annotations) {

                if (annotatedElement.getAnnotation(annotation) != null) {
                    return true;
                }
            }

            return false;
        }


        /*
         * (non-Javadoc)
         * 
         * @see
         * org.synyx.hades.dao.orm.GenericJpaDao.IsNewStrategy#isNew(java.lang
         * .Object)
         */
        public boolean isNew(Object entity) {

            return getId(entity) == null;
        }


        /*
         * (non-Javadoc)
         * 
         * @see
         * org.synyx.hades.dao.orm.GenericDaoSupport.IdAware#getId(java.lang
         * .Object)
         */
        public Object getId(Object entity) {

            if (field != null) {
                ReflectionUtils.makeAccessible(field);
                return ReflectionUtils.getField(field, entity);
            }

            ReflectionUtils.makeAccessible(method);
            return ReflectionUtils.invokeMethod(method, entity);
        }
    }
}

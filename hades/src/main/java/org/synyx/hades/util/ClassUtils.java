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
package org.synyx.hades.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;

import org.springframework.util.StringUtils;
import org.synyx.hades.dao.ExtendedGenericDao;
import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.domain.Persistable;


/**
 * Utility class to work with classes.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 */
public abstract class ClassUtils {

    /**
     * Hades own DAO interfaces.
     */
    private static final List<String> HADES_DAO_INTERFACE_NAMES =
            Arrays.asList(GenericDao.class.getName(), ExtendedGenericDao.class
                    .getName());


    /**
     * Private constructor to prevent instantiation.
     */
    private ClassUtils() {

    }


    /**
     * Returns the domain class the given class is declared for. Will introspect
     * the given class for extensions of {@link GenericDao} or
     * {@link ExtendedGenericDao} and retrieve the {@link Persistable} type from
     * its generics declaration.
     * 
     * @param clazz
     * @return the domain class the given class is DAO for or {@code null} if
     *         none found.
     */
    @SuppressWarnings("unchecked")
    public static Class<? extends Persistable<?>> getDomainClass(Class<?> clazz) {

        return (Class<? extends Persistable<?>>) getGenericType(clazz, 0);
    }


    /**
     * Returns the id class the given class is declared for. Will introspect the
     * given class for extensions of {@link GenericDao} or
     * {@link ExtendedGenericDao} and retrieve the {@link Serializable} type
     * from its generics declaration.
     * 
     * @param clazz
     * @return the id class the given class is DAO for or {@code null} if none
     *         found.
     */
    @SuppressWarnings("unchecked")
    public static Class<? extends Serializable> getIdClass(Class<?> clazz) {

        return (Class<? extends Serializable>) getGenericType(clazz, 1);
    }


    /**
     * Returns the generic type with the given index from the given
     * {@link Class} if it implements {@link GenericDao} or
     * {@link ExtendedGenericDao}.
     * 
     * @param clazz
     * @param index
     * @return the domain class for index 0, the id class for index 1.
     */
    private static Class<?> getGenericType(Class<?> clazz, int index) {

        for (Type type : clazz.getGenericInterfaces()) {

            if (type instanceof ParameterizedType) {

                ParameterizedType parammeterizedType = (ParameterizedType) type;

                if (isGenericDao(parammeterizedType)) {

                    return (Class<?>) parammeterizedType
                            .getActualTypeArguments()[index];
                }
            }

            Class<?> result = getGenericType((Class<?>) type, index);

            if (null != result) {
                return result;
            }
        }

        return null;
    }


    /**
     * Returns whether a {@link ParameterizedType} is a {@link GenericDao} or
     * {@link ExtendedGenericDao}.
     * 
     * @param type
     * @return
     */
    private static boolean isGenericDao(ParameterizedType type) {

        boolean isExtendedGenericDao =
                type.getRawType().equals(ExtendedGenericDao.class);
        boolean isGenericDao = type.getRawType().equals(GenericDao.class);

        return isGenericDao || isExtendedGenericDao;
    }


    /**
     * Returns wthere the given type is a Hades DAO interface.
     * 
     * @param interfaze
     * @return
     */
    public static boolean isHadesDaoInterface(Class<?> interfaze) {

        return isHadesDaoInterface(interfaze.getName());
    }


    /**
     * Returns whether the given type name is a Hades DAO interface name.
     * 
     * @param interfaceName
     * @return
     */
    public static boolean isHadesDaoInterface(String interfaceName) {

        return HADES_DAO_INTERFACE_NAMES.contains(interfaceName);
    }


    /**
     * Returns whether the given {@link EntityManager} is of the given type.
     * 
     * @param em
     * @param type the fully qualified expected {@link EntityManager} type.
     * @return
     */
    @SuppressWarnings("unchecked")
    public static boolean isEntityManagerOfType(EntityManager em, String type) {

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
    public static boolean isExtendedDaoInterface(Class<?> daoInterface) {

        return ExtendedGenericDao.class.isAssignableFrom(daoInterface);
    }


    /**
     * Returns the number of occurences of the given type in the given
     * {@link Method}s parameters.
     * 
     * @param method
     * @param type
     * @return
     */
    public static int getNumberOfOccurences(Method method, Class<?> type) {

        int result = 0;
        for (Class<?> clazz : method.getParameterTypes()) {
            if (type.equals(clazz)) {
                result++;
            }
        }

        return result;
    }


    /**
     * Asserts the given {@link Method}'s return type to be one of the given
     * types.
     * 
     * @param method
     * @param types
     */
    public static void assertReturnType(Method method, Class<?>... types) {

        if (!Arrays.asList(types).contains(method.getReturnType())) {
            throw new IllegalStateException(
                    "Method has to have one of the following return types! "
                            + Arrays.toString(types));
        }
    }


    /**
     * Returns whether the given object is of one of the given types. Will
     * return {@literal false} for {@literal null}.
     * 
     * @param object
     * @param types
     * @return
     */
    public static boolean isOfType(Object object, Collection<Class<?>> types) {

        if (null == object) {
            return false;
        }

        for (Class<?> type : types) {
            if (type.isAssignableFrom(object.getClass())) {
                return true;
            }
        }

        return false;
    }


    /**
     * Returns whether the given {@link Method} has a parameter of the given
     * type.
     * 
     * @param method
     * @param type
     * @return
     */
    public static boolean hasParameterOfType(Method method, Class<?> type) {

        return Arrays.asList(method.getParameterTypes()).contains(type);
    }


    /**
     * Returns the name ot the entity represented by this class. Used to build
     * queries for that class.
     * 
     * @param domainClass
     * @return
     */
    public static String getEntityName(Class<?> domainClass) {

        Entity entity = domainClass.getAnnotation(Entity.class);
        boolean hasName = null != entity && StringUtils.hasText(entity.name());

        return hasName ? entity.name() : domainClass.getSimpleName();
    }


    /**
     * Helper method to extract the original exception that can possibly occur
     * during a reflection call.
     * 
     * @param ex
     * @throws Throwable
     */
    public static void unwrapReflectionException(Exception ex) throws Throwable {

        if (ex instanceof InvocationTargetException) {
            throw ((InvocationTargetException) ex).getTargetException();
        }

        throw ex;
    }
}

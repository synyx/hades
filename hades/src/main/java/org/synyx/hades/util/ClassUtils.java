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
package org.synyx.hades.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.EntityManager;

import org.springframework.util.StringUtils;
import org.synyx.hades.dao.GenericDao;


/**
 * Utility class to work with classes.
 * 
 * @author Oliver Gierke
 */
public abstract class ClassUtils {

    @SuppressWarnings("rawtypes")
    private static final TypeVariable<Class<GenericDao>>[] PARAMETERS =
            GenericDao.class.getTypeParameters();
    private static final String DOMAIN_TYPE_NAME = PARAMETERS[0].getName();
    private static final String ID_TYPE_NAME = PARAMETERS[1].getName();


    /**
     * Private constructor to prevent instantiation.
     */
    private ClassUtils() {

    }


    /**
     * Returns the domain class the given class is declared for. Will introspect
     * the given class for extensions of {@link GenericDao} or
     * {@link ExtendedGenericDao} and retrieve the domain class type from its
     * generics declaration.
     * 
     * @param clazz
     * @return the domain class the given class is DAO for or {@code null} if
     *         none found.
     */
    public static Class<?> getDomainClass(Class<?> clazz) {

        return getGenericType(clazz, 0);
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

                    Type result =
                            parammeterizedType.getActualTypeArguments()[index];

                    return (Class<?>) (result instanceof ParameterizedType ? ((ParameterizedType) result)
                            .getRawType()
                            : result);
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
     * Returns the domain class returned by the given {@link Method}. Will
     * extract the type from {@link Collection}s and
     * {@link org.synyx.hades.domain.Page} as well.
     * 
     * @param method
     * @return
     */
    public static Class<?> getReturnedDomainClass(Method method) {

        Type type = method.getGenericReturnType();

        if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type)
                    .getActualTypeArguments()[0];

        } else {
            return method.getReturnType();
        }
    }


    /**
     * Returns whether a {@link ParameterizedType} is a {@link GenericDao} or
     * {@link ExtendedGenericDao}.
     * 
     * @param type
     * @return
     */
    private static boolean isGenericDao(ParameterizedType type) {

        return GenericDao.class.isAssignableFrom((Class<?>) type.getRawType());
    }


    /**
     * Returns wthere the given type is the {@link GenericDao} interface.
     * 
     * @param interfaze
     * @return
     */
    public static boolean isGenericDaoInterface(Class<?> interfaze) {

        return GenericDao.class.equals(interfaze);
    }


    /**
     * Returns whether the given type name is a Hades DAO interface name.
     * 
     * @param interfaceName
     * @return
     */
    public static boolean isHadesDaoInterface(String interfaceName) {

        return GenericDao.class.getName().equals(interfaceName);
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
        } catch (NoClassDefFoundError e) {
            return false;
        }
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


    /**
     * Returns the given base class' method if the given method (declared in the
     * interface) was also declared at the base class. Returns the given method
     * if the given base class does not declare the method given. Takes generics
     * into account.
     * 
     * @param method
     * @param baseClass
     * @param daoInterface
     * @return
     */
    public static Method getBaseClassMethodFor(Method method,
            Class<?> baseClass, Class<?> daoInterface) {

        for (Method daoClassMethod : baseClass.getDeclaredMethods()) {

            // Wrong name
            if (!method.getName().equals(daoClassMethod.getName())) {
                continue;
            }

            // Wrong number of arguments
            if (!(method.getParameterTypes().length == daoClassMethod
                    .getParameterTypes().length)) {
                continue;
            }

            // Check whether all parameters match
            if (!parametersMatch(method, daoClassMethod, daoInterface)) {
                continue;
            }

            return daoClassMethod;
        }

        return method;
    }


    /**
     * Checks the given method's parameters to match the ones of the given base
     * class method. Matches generic arguments agains the ones bound in the
     * given DAO interface.
     * 
     * @param method
     * @param baseClassMethod
     * @param daoInterface
     * @return
     */
    private static boolean parametersMatch(Method method,
            Method baseClassMethod, Class<?> daoInterface) {

        Type[] genericTypes = baseClassMethod.getGenericParameterTypes();
        Class<?>[] types = baseClassMethod.getParameterTypes();
        Class<?>[] methodParameters = method.getParameterTypes();

        for (int i = 0; i < genericTypes.length; i++) {

            Type type = genericTypes[i];

            if (type instanceof TypeVariable<?>) {

                String name = ((TypeVariable<?>) type).getName();

                if (!matchesGenericType(name, methodParameters[i], daoInterface)) {
                    return false;
                }

            } else {

                if (!types[i].equals(methodParameters[i])) {
                    return false;
                }
            }
        }

        return true;
    }


    /**
     * Checks whether the given parameter type matches the generic type of the
     * given parameter. Thus when {@literal PK} is declared, the method ensures
     * that given method parameter is the primary key type declared in the given
     * DAO interface e.g.
     * 
     * @param name
     * @param parameterType
     * @param daoInterface
     * @return
     */
    private static boolean matchesGenericType(String name,
            Class<?> parameterType, Class<?> daoInterface) {

        Class<?> entityType = getDomainClass(daoInterface);
        Class<?> idClass = getIdClass(daoInterface);

        if (ID_TYPE_NAME.equals(name) && parameterType.equals(idClass)) {
            return true;
        }

        if (DOMAIN_TYPE_NAME.equals(name) && parameterType.equals(entityType)) {
            return true;
        }

        return false;
    }
}

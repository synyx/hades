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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

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

        for (Type type : clazz.getGenericInterfaces()) {

            if (type instanceof ParameterizedType) {

                ParameterizedType parammeterizedType = (ParameterizedType) type;

                if (isGenericDao(parammeterizedType)) {

                    return (Class<? extends Persistable<?>>) parammeterizedType
                            .getActualTypeArguments()[0];
                }
            }

            Class<? extends Persistable<?>> result =
                    getDomainClass((Class<?>) type);

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
}

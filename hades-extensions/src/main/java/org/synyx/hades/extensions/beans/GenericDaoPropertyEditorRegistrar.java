/*
 * Copyright 2008-2010 the original author or authors.
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
package org.synyx.hades.extensions.beans;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.util.ClassUtils;


/**
 * Simple helper class to use Hades DAOs to provide
 * {@link java.beans.PropertyEditor}s for domain classes. To get this working
 * configure a
 * {@link org.springframework.web.bind.support.ConfigurableWebBindingInitializer}
 * for your
 * {@link org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter}
 * and register the {@link GenericDaoPropertyEditorRegistrar} there: <code>
 * &lt;bean class="org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter"&gt;
 *   &lt;property name="webBindingInitializer"&gt;
 *     &lt;bean class="org.springframework.web.bind.support.ConfigurableWebBindingInitializer"&gt;
 *       &lt;property name="propertyEditorRegistrars"&gt;
 *         &lt;bean class="org.synyx.hades.extensions.beans.GenericDaoPropertyEditorRegistrar" /&gt;
 *       &lt;/property&gt;
 *     &lt;/bean&gt;
 *   &lt;/property&gt;
 * &lt;/bean&gt;
 * </code>
 * 
 * @author Oliver Gierke
 */

public class GenericDaoPropertyEditorRegistrar implements
        PropertyEditorRegistrar, ApplicationContextAware {

    private final Map<Class<?>, GenericDao<?, Serializable>> daoMap =
            new HashMap<Class<?>, GenericDao<?, Serializable>>();


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.beans.PropertyEditorRegistrar#registerCustomEditors
     * (org.springframework.beans.PropertyEditorRegistry)
     */
    public void registerCustomEditors(PropertyEditorRegistry registry) {

        for (Entry<Class<?>, GenericDao<?, Serializable>> entry : daoMap
                .entrySet()) {

            registry.registerCustomEditor(
                    entry.getKey(),
                    new DomainClassPropertyEditor<Serializable>(entry
                            .getValue(), registry));
        }
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.context.ApplicationContextAware#setApplicationContext
     * (org.springframework.context.ApplicationContext)
     */
    @SuppressWarnings("unchecked")
    public void setApplicationContext(ApplicationContext context) {

        @SuppressWarnings("rawtypes")
        Collection<GenericDao> daos =
                BeanFactoryUtils.beansOfTypeIncludingAncestors(context,
                        GenericDao.class).values();

        for (GenericDao<?, ?> dao : daos) {
            this.daoMap.put(getDomainClass(dao),
                    (GenericDao<?, Serializable>) dao);
        }
    }


    /**
     * Derives the domain class from the given DAO class.
     * 
     * @param dao
     * @return
     */
    private Class<?> getDomainClass(GenericDao<?, ?> dao) {

        return ClassUtils.getDomainClass(dao.getClass());
    }
}

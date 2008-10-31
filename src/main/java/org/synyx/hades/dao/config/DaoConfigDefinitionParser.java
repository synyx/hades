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

package org.synyx.hades.dao.config;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

import javax.persistence.Entity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.synyx.hades.dao.orm.support.GenericDaoFactoryBean;
import org.synyx.hades.domain.Persistable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Parser to create bean definitions for dao-config namespace. Registers bean
 * definitions for DAOs as well as {@code
 * PersistenceAnnotationBeanPostProcessor} and {@code
 * PersistenceExceptionTranslationPostProcessor} to transparently inject entity
 * manager factory instance and apply exception translation.
 * <p>
 * The definition parser allows two ways of configuration. Either it looks up
 * the manually defined DAO instances or scans the defined domain package for
 * candidates for DAOs.
 * 
 * @author Oliver Gierke - gierke@synyx.de
 * @author Eberhard Wolff
 */
public class DaoConfigDefinitionParser implements BeanDefinitionParser,
        BeanDefinitionDecorator {

    private static final Log log =
            LogFactory.getLog(DaoConfigDefinitionParser.class);

    private static final Class<?> PAB_POST_PROCESSOR =
            PersistenceAnnotationBeanPostProcessor.class;
    private static final Class<?> PET_POST_PROCESSOR =
            PersistenceExceptionTranslationPostProcessor.class;


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.beans.factory.xml.BeanDefinitionParser#parse(org.
     * w3c.dom.Element, org.springframework.beans.factory.xml.ParserContext)
     */
    public BeanDefinition parse(final Element element,
            final ParserContext parserContext) {

        try {

            DaoConfigContext configContext = new DaoConfigContext(element);
            configContext.validate();

            if (configContext.configureManually()) {
                doManualConfiguration(configContext, parserContext);
            } else {
                doAutoConfiguration(configContext, parserContext);
            }

        } catch (IllegalArgumentException e) {
            parserContext.getReaderContext().error(e.getMessage(),
                    parserContext.extractSource(element));
        }

        registerPostProcessors(parserContext);

        return null;
    }


    /**
     * Executes DAO auto configuration by scanning the provided entity package
     * for classes implementing {@code Persistable}.
     * 
     * @param configContext
     * @param parserContext
     */
    private void doAutoConfiguration(final DaoConfigContext configContext,
            final ParserContext parserContext) {

        if (log.isDebugEnabled()) {
            log.debug("Triggering auto DAO detection");
        }

        // Create scanner and apply filter
        ClassPathScanningCandidateComponentProvider provider =
                new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new PersistableTypeFilter());

        Set<BeanDefinition> beanDefinitions =
                provider.findCandidateComponents(configContext
                        .getEntityPackageName());

        for (BeanDefinition definition : beanDefinitions) {

            String id =
                    StringUtils.uncapitalize(ClassUtils.getShortName(definition
                            .getBeanClassName()));

            registerGenericDaoFactoryBean(parserContext, new DaoContext(id,
                    configContext));
        }
    }


    /**
     * Proceeds manual configuration by traversing child elements.
     * 
     * @param context
     * @param parserContext
     */
    private void doManualConfiguration(final DaoConfigContext context,
            final ParserContext parserContext) {

        if (log.isDebugEnabled()) {
            log.debug("Triggering manual DAO detection");
        }

        // Add dao declarations
        for (DaoContext daoContext : context.getDaoContexts()) {

            registerGenericDaoFactoryBean(parserContext, daoContext);
        }
    }


    /**
     * Registers a {@code GenericDaoFactoryBean} for a bean with the given name
     * and the provided configuration context. It is mainly used to construct
     * bean name, entity class name and DAO interface name.
     * 
     * @param parserContext
     * @param name
     * @param context
     */
    private void registerGenericDaoFactoryBean(
            final ParserContext parserContext, final DaoContext context) {

        BeanDefinitionBuilder beanDefinitionBuilder =
                BeanDefinitionBuilder
                        .rootBeanDefinition(GenericDaoFactoryBean.class);
        beanDefinitionBuilder.addPropertyValue("daoInterface", context
                .getInterfaceName());
        beanDefinitionBuilder.addPropertyValue("domainClass", context
                .getDomainClassName());
        beanDefinitionBuilder.addPropertyValue("queryLookupStrategy", context
                .getFinderLookupStrategy());
        beanDefinitionBuilder.addPropertyValue("daoClass", context
                .getDaoBaseClassName());
        beanDefinitionBuilder.addPropertyValue("finderPrefix", context
                .getFinderPrefix());

        String customImplementationBeanName =
                registerCustomImplementation(context, parserContext,
                        beanDefinitionBuilder);

        AbstractBeanDefinition beanDefinition =
                beanDefinitionBuilder.getBeanDefinition();
        beanDefinition.setSource(parserContext.extractSource(context
                .getElement()));

        if (log.isDebugEnabled()) {

            StringBuilder builder =
                    new StringBuilder("Registering Hades DAO: ");
            builder.append(context.getBeanName());
            builder.append(" - DAO interface: ");
            builder.append(context.getInterfaceName());
            builder.append(" - Implementation base class: ");
            builder.append(context.getDaoBaseClassName());
            builder.append(" - Custom implementation: ");
            builder.append(customImplementationBeanName);

            log.debug(builder.toString());
        }

        BeanComponentDefinition definition =
                new BeanComponentDefinition(beanDefinition, context
                        .getBeanName());

        parserContext.registerBeanComponent(definition);
    }


    /**
     * Registers a possibly available custom DAO implementation on the DAO bean.
     * Tries to find an already registered bean to reference or tries to detect
     * a custom implementation itself.
     * 
     * @param context
     * @param parserContext
     * @param beanDefinitionBuilder
     * @return the bean name of the custom implementation or {@code null} if
     *         none available
     */
    private String registerCustomImplementation(final DaoContext context,
            final ParserContext parserContext,
            final BeanDefinitionBuilder beanDefinitionBuilder) {

        String beanName = context.getImplementationBeanName();

        // Already a bean configured?
        if (parserContext.getRegistry().containsBeanDefinition(
                context.getImplementationBeanName())) {

            beanDefinitionBuilder.addPropertyReference(
                    "customDaoImplementation", beanName);

            return beanName;
        }

        // Autodetect implementation
        if (context.autodetectCustomImplementation()) {

            BeanDefinition beanDefinition = detectCustomImplementation(context);

            if (null == beanDefinition) {
                return null;
            }

            if (log.isDebugEnabled()) {
                log.debug("Registering custom DAO implementation: "
                        + context.getImplementationBeanName() + " "
                        + context.getImplementationClassName());
            }

            parserContext.registerBeanComponent(new BeanComponentDefinition(
                    beanDefinition, beanName));

        } else {

            beanName = context.getCustomImplementationRef();
        }

        beanDefinitionBuilder.addPropertyReference("customDaoImplementation",
                beanName);

        return beanName;
    }


    /**
     * Tries to detect a custom implementation for a DAO bean by classpath
     * scanning.
     * 
     * @param context
     * @return the {@code BeanDefinition} of the custom implementation or null
     *         if none found
     */
    private BeanDefinition detectCustomImplementation(final DaoContext context) {

        // Build pattern to lookup implementation class
        Pattern pattern = Pattern.compile(context.getImplementationClassName());

        // Build classpath scanner and lookup bean definition
        ClassPathScanningCandidateComponentProvider provider =
                new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new RegexPatternTypeFilter(pattern));
        Set<BeanDefinition> definitions =
                provider.findCandidateComponents(context.getDaoPackageName());

        return 0 == definitions.size() ? null : definitions.iterator().next();
    }


    /**
     * Registers necessary (Bean)PostProcessor instances if they have not
     * already been registered.
     * 
     * @param parserContext
     */
    private void registerPostProcessors(final ParserContext parserContext) {

        BeanDefinitionRegistry registry = parserContext.getRegistry();

        // Create PersistenceAnnotationPostProcessor definition
        if (!registry.containsBeanDefinition(PAB_POST_PROCESSOR.getName())) {

            BeanDefinition definition =
                    BeanDefinitionBuilder
                            .rootBeanDefinition(PAB_POST_PROCESSOR)
                            .getBeanDefinition();

            registry.registerBeanDefinition(definition.getBeanClassName(),
                    definition);
        }

        // Create PersistenceExceptionTranslationPostProcessor definition
        if (!registry.containsBeanDefinition(PET_POST_PROCESSOR.getName())) {

            BeanDefinition definition =
                    BeanDefinitionBuilder.rootBeanDefinition(
                            PersistenceExceptionTranslationPostProcessor.class)
                            .getBeanDefinition();

            registry.registerBeanDefinition(definition.getBeanClassName(),
                    definition);
        }
    }

    /**
     * {@code TypeFilter} implementation that detects classes that implement
     * {@code Persistable} and are annotated with {@link Entity}.
     * 
     * @author Oliver Gierke - gierke@synyx.de
     */
    static class PersistableTypeFilter implements TypeFilter {

        private static final AnnotationTypeFilter ENTITY_ANNOTATION_FILTER =
                new AnnotationTypeFilter(Entity.class);
        private static final AssignableTypeFilter PERSISTABLE_FILTER =
                new AssignableTypeFilter(Persistable.class);


        /*
         * (non-Javadoc)
         * 
         * @see
         * org.springframework.core.type.filter.TypeFilter#match(org.springframework
         * .core.type.classreading.MetadataReader,
         * org.springframework.core.type.classreading.MetadataReaderFactory)
         */
        public boolean match(final MetadataReader metadataReader,
                final MetadataReaderFactory metadataReaderFactory)
                throws IOException {

            // Matches on correct type AND annotation
            return PERSISTABLE_FILTER.match(metadataReader,
                    metadataReaderFactory)
                    && ENTITY_ANNOTATION_FILTER.match(metadataReader,
                            metadataReaderFactory);
        }
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.beans.factory.xml.BeanDefinitionDecorator#decorate
     * (org.w3c.dom.Node,
     * org.springframework.beans.factory.config.BeanDefinitionHolder,
     * org.springframework.beans.factory.xml.ParserContext)
     */
    public BeanDefinitionHolder decorate(Node node,
            BeanDefinitionHolder definition, ParserContext parserContext) {

        return definition;
    }
}

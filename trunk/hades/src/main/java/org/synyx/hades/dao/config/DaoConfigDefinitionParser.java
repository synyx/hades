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

import static org.synyx.hades.util.ClassUtils.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.synyx.hades.dao.GenericDao;
import org.w3c.dom.Element;


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
public class DaoConfigDefinitionParser implements BeanDefinitionParser {

    private static final String FACTORY_CLASS =
            "org.synyx.hades.dao.orm.GenericDaoFactoryBean";

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

        ResourceLoader resourceLoader =
                parserContext.getReaderContext().getResourceLoader();

        // Detect available DAO interfaces
        Set<String> daoInterfaces =
                getDaoInterfacesForAutoConfig(configContext, resourceLoader);

        for (String daoInterface : daoInterfaces) {

            registerGenericDaoFactoryBean(parserContext, DaoContext
                    .fromInterfaceName(daoInterface, configContext));
        }
    }


    private Set<String> getDaoInterfacesForAutoConfig(
            final DaoConfigContext configContext, final ResourceLoader loader) {

        ClassPathScanningCandidateComponentProvider scanner =
                new AbstractClassesAwareComponentProvider(
                        new InterfaceTypeFilter(GenericDao.class));
        scanner.setResourceLoader(loader);

        Set<BeanDefinition> findCandidateComponents =
                scanner.findCandidateComponents(configContext
                        .getDaoBasePackageName());

        Set<String> interfaceNames = new HashSet<String>();
        for (BeanDefinition definition : findCandidateComponents) {
            interfaceNames.add(definition.getBeanClassName());
        }

        return interfaceNames;
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
                BeanDefinitionBuilder.rootBeanDefinition(FACTORY_CLASS);

        beanDefinitionBuilder.addPropertyValue("daoInterface", context
                .getInterfaceName());
        beanDefinitionBuilder.addPropertyValue("queryLookupStrategy", context
                .getFinderLookupStrategy());
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
                provider.findCandidateComponents(context
                        .getDaoBasePackageName());

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
     * Custom {@link ClassPathScanningCandidateComponentProvider} that does not
     * skip abstract classes or interfaces.
     * 
     * @author Oliver Gierke - gierke@synyx.de
     */
    static class AbstractClassesAwareComponentProvider extends
            ClassPathScanningCandidateComponentProvider {

        /**
         * Creates a new {@link AbstractClassesAwareComponentProvider}.
         * 
         * @param filter to be added as include filter
         */
        public AbstractClassesAwareComponentProvider(TypeFilter filter) {

            super(false);
            addIncludeFilter(filter);
        }


        /*
         * (non-Javadoc)
         * 
         * @seeorg.springframework.context.annotation.
         * ClassPathScanningCandidateComponentProvider
         * #isCandidateComponent(org.springframework
         * .beans.factory.annotation.AnnotatedBeanDefinition)
         */
        @Override
        protected boolean isCandidateComponent(
                AnnotatedBeanDefinition beanDefinition) {

            boolean isNonHadesInterfaces =
                    !isHadesDaoInterface(beanDefinition.getBeanClassName());
            boolean isTopLevelType =
                    !beanDefinition.getMetadata().hasEnclosingClass();

            return isNonHadesInterfaces && isTopLevelType;
        }
    }

    /**
     * {@link TypeFilter} that only matches interfaces. Thus setting this up
     * makes only sense providing an interface type as {@code targetType}.
     * 
     * @author Oliver Gierke - gierke@synyx.de
     */
    static class InterfaceTypeFilter extends AssignableTypeFilter {

        /**
         * Creates a new {@link InterfaceTypeFilter}.
         * 
         * @param targetType
         */
        public InterfaceTypeFilter(Class<?> targetType) {

            super(targetType);
        }


        /*
         * (non-Javadoc)
         * 
         * @seeorg.springframework.core.type.filter.
         * AbstractTypeHierarchyTraversingFilter
         * #match(org.springframework.core.type.classreading.MetadataReader,
         * org.springframework.core.type.classreading.MetadataReaderFactory)
         */
        @Override
        public boolean match(MetadataReader metadataReader,
                MetadataReaderFactory metadataReaderFactory) throws IOException {

            return metadataReader.getClassMetadata().isInterface()
                    && super.match(metadataReader, metadataReaderFactory);
        }
    }
}

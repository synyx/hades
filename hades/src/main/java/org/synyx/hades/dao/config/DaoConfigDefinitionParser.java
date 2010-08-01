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

package org.synyx.hades.dao.config;

import static org.springframework.beans.factory.support.BeanDefinitionReaderUtils.*;
import static org.synyx.hades.util.ClassUtils.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.ReaderContext;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.synyx.hades.dao.GenericDao;
import org.synyx.hades.dao.NoDaoBean;
import org.w3c.dom.Element;


/**
 * Parser to create bean definitions for dao-config namespace. Registers bean
 * definitions for DAOs as well as
 * {@code PersistenceAnnotationBeanPostProcessor} and
 * {@code PersistenceExceptionTranslationPostProcessor} to transparently inject
 * entity manager factory instance and apply exception translation.
 * <p>
 * The definition parser allows two ways of configuration. Either it looks up
 * the manually defined DAO instances or scans the defined domain package for
 * candidates for DAOs.
 * 
 * @author Oliver Gierke
 * @author Eberhard Wolff
 * @author Gil Markham
 */
class DaoConfigDefinitionParser implements BeanDefinitionParser {

    private static final Logger LOG = LoggerFactory
            .getLogger(DaoConfigDefinitionParser.class);

    private static final Class<?> PAB_POST_PROCESSOR =
            PersistenceAnnotationBeanPostProcessor.class;
    private static final Class<?> PET_POST_PROCESSOR =
            PersistenceExceptionTranslationPostProcessor.class;
    private static final String DAO_INTERFACE_POST_PROCESSOR =
            "org.synyx.hades.dao.orm.DaoInterfaceAwareBeanPostProcessor";


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

        if (LOG.isDebugEnabled()) {
            LOG.debug("Triggering auto DAO detection");
        }

        ResourceLoader resourceLoader =
                parserContext.getReaderContext().getResourceLoader();

        // Detect available DAO interfaces
        Set<String> daoInterfaces =
                getDaoInterfacesForAutoConfig(configContext, resourceLoader,
                        parserContext.getReaderContext());

        for (String daoInterface : daoInterfaces) {

            registerGenericDaoFactoryBean(parserContext,
                    DaoContext.fromInterfaceName(daoInterface, configContext));
        }
    }


    private Set<String> getDaoInterfacesForAutoConfig(
            final DaoConfigContext configContext, final ResourceLoader loader,
            final ReaderContext readerContext) {

        ClassPathScanningCandidateComponentProvider scanner =
                new GenericDaoComponentProvider();
        scanner.setResourceLoader(loader);

        TypeFilterParser parser =
                new TypeFilterParser(loader.getClassLoader(), readerContext);
        parser.parseFilters(configContext.getElement(), scanner);

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

        if (LOG.isDebugEnabled()) {
            LOG.debug("Triggering manual DAO detection");
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

        Object beanSource = parserContext.extractSource(context.getElement());

        BeanDefinitionBuilder beanDefinitionBuilder =
                BeanDefinitionBuilder.rootBeanDefinition(context
                        .getDaoFactoryClassName());

        beanDefinitionBuilder.addPropertyValue("daoInterface",
                context.getInterfaceName());
        beanDefinitionBuilder.addPropertyValue("queryLookupStrategy",
                context.getQueryLookupStrategy());

        String entityManagerRef = context.getEntityManagerFactoryRef();

        if (null != entityManagerRef) {
            beanDefinitionBuilder.addPropertyValue(
                    "entityManager",
                    getEntityManagerBeanDefinitionFor(entityManagerRef,
                            beanSource));
        }

        beanDefinitionBuilder.addPropertyValue("transactionManager",
                context.getTransactionManagerRef());

        String customImplementationBeanName =
                registerCustomImplementation(context, parserContext,
                        beanDefinitionBuilder);

        AbstractBeanDefinition beanDefinition =
                beanDefinitionBuilder.getBeanDefinition();
        beanDefinition.setSource(beanSource);

        if (LOG.isDebugEnabled()) {

            StringBuilder builder =
                    new StringBuilder("Registering Hades DAO: ");
            builder.append(context.getBeanName());
            builder.append(" - DAO interface: ");
            builder.append(context.getInterfaceName());
            builder.append(" - Factory: ");
            builder.append(context.getDaoFactoryClassName());
            builder.append(" - Custom implementation: ");
            builder.append(customImplementationBeanName);

            LOG.debug(builder.toString());
        }

        BeanComponentDefinition definition =
                new BeanComponentDefinition(beanDefinition,
                        context.getBeanName());

        parserContext.registerBeanComponent(definition);
    }


    /**
     * Creates an anonymous factory to extract the actual
     * {@link javax.persistence.EntityManager} from the
     * {@link javax.persistence.EntityManagerFactory} bean name reference.
     * 
     * @param entityManagerFactoryBeanName
     * @param source
     * @return
     */
    private BeanDefinition getEntityManagerBeanDefinitionFor(
            String entityManagerFactoryBeanName, Object source) {

        BeanDefinitionBuilder builder =
                BeanDefinitionBuilder
                        .rootBeanDefinition("org.springframework.orm.jpa.SharedEntityManagerCreator");
        builder.setFactoryMethod("createSharedEntityManager");
        builder.addConstructorArgReference(entityManagerFactoryBeanName);

        AbstractBeanDefinition bean = builder.getRawBeanDefinition();
        bean.setSource(source);

        return bean;
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
        if (parserContext.getRegistry().containsBeanDefinition(beanName)) {

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

            LOG.debug("Registering custom DAO implementation: %s %s",
                    context.getImplementationBeanName(),
                    beanDefinition.getBeanClassName());

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
        Pattern pattern =
                Pattern.compile(".*" + context.getImplementationClassName());

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
        if (!hasBean(PAB_POST_PROCESSOR, registry)) {

            BeanDefinition definition =
                    BeanDefinitionBuilder
                            .rootBeanDefinition(PAB_POST_PROCESSOR)
                            .getBeanDefinition();

            registry.registerBeanDefinition(
                    generateBeanName(definition, registry), definition);
        }

        // Create PersistenceExceptionTranslationPostProcessor definition
        if (!hasBean(PET_POST_PROCESSOR, registry)) {

            BeanDefinition definition =
                    BeanDefinitionBuilder
                            .rootBeanDefinition(PET_POST_PROCESSOR)
                            .getBeanDefinition();

            registry.registerBeanDefinition(
                    generateBeanName(definition, registry), definition);
        }

        BeanDefinition definition =
                BeanDefinitionBuilder.rootBeanDefinition(
                        DAO_INTERFACE_POST_PROCESSOR).getBeanDefinition();

        registry.registerBeanDefinition(generateBeanName(definition, registry),
                definition);
    }


    private boolean hasBean(Class<?> clazz, BeanDefinitionRegistry registry) {

        String name =
                String.format("%s%s0", clazz.getName(),
                        GENERATED_BEAN_NAME_SEPARATOR);
        return registry.containsBeanDefinition(name);
    }

    /**
     * Custom {@link ClassPathScanningCandidateComponentProvider} scanning for
     * interfaces extending {@link GenericDao}. Skips interfaces annotated with
     * {@link NoDaoBean}.
     * 
     * @author Oliver Gierke
     */
    static class GenericDaoComponentProvider extends
            ClassPathScanningCandidateComponentProvider {

        /**
         * Creates a new {@link GenericDaoComponentProvider}.
         */
        public GenericDaoComponentProvider() {

            super(false);
            addIncludeFilter(new InterfaceTypeFilter(GenericDao.class));
            addExcludeFilter(new AnnotationTypeFilter(NoDaoBean.class));
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

        /**
         * {@link org.springframework.core.type.filter.TypeFilter} that only
         * matches interfaces. Thus setting this up makes only sense providing
         * an interface type as {@code targetType}.
         * 
         * @author Oliver Gierke
         */
        private static class InterfaceTypeFilter extends AssignableTypeFilter {

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
                    MetadataReaderFactory metadataReaderFactory)
                    throws IOException {

                return metadataReader.getClassMetadata().isInterface()
                        && super.match(metadataReader, metadataReaderFactory);
            }
        }
    }
}

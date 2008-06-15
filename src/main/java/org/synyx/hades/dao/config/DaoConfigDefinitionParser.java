/*
 * Copyright 2002-2008 the original author or authors.
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

package org.synyx.hades.dao.config;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

import javax.persistence.Entity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
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
import org.w3c.dom.NodeList;


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
 * @author Oliver Gierke - gierke@synyx.de
 * @author Eberhard Wolff
 */
public class DaoConfigDefinitionParser implements BeanDefinitionParser {

    private static final Log log = LogFactory
            .getLog(DaoConfigDefinitionParser.class);

    private static final Class<?> PAB_POST_PROCESSOR = PersistenceAnnotationBeanPostProcessor.class;
    private static final Class<?> PET_POST_PROCESSOR = PersistenceExceptionTranslationPostProcessor.class;


    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.xml.BeanDefinitionParser#parse(org.w3c.dom.Element,
     *      org.springframework.beans.factory.xml.ParserContext)
     */
    public BeanDefinition parse(final Element element,
            final ParserContext parserContext) {

        XmlDaoConfigContext configContext = new XmlDaoConfigContext(element);

        if (configContext.configureManually()) {
            doManualConfiguration(configContext, parserContext);
        } else {
            doAutoConfiguration(configContext, parserContext);
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
    private void doAutoConfiguration(final XmlDaoConfigContext configContext,
            final ParserContext parserContext) {

        if (log.isDebugEnabled()) {
            log.debug("Triggering auto DAO detection");
        }

        // Create scanner and apply filter
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(
                false);
        provider.addIncludeFilter(new PersistableTypeFilter());

        Set<BeanDefinition> beanDefinitions = provider
                .findCandidateComponents(configContext.getEntityPackageName());

        for (BeanDefinition definition : beanDefinitions) {

            // Retrieve bean name by altering first letter of the bean class
            // name to lowercase
            String beanName = StringUtils.uncapitalize(ClassUtils
                    .getShortName(definition.getBeanClassName()));

            registerGenericDaoFactoryBean(parserContext, beanName,
                    configContext);
        }
    }


    /**
     * Proceeds manual configuration by traversing child elements.
     * 
     * @param context
     * @param parserContext
     */
    private void doManualConfiguration(final XmlDaoConfigContext context,
            final ParserContext parserContext) {

        if (log.isDebugEnabled()) {
            log.debug("Triggering manual DAO detection");
        }

        NodeList childNodes = context.getChildNodes();

        // Add dao declarations
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {

                Element childElement = (Element) childNode;
                String name = childElement.getAttribute("name");

                registerGenericDaoFactoryBean(parserContext, name, context);
            }
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
            final ParserContext parserContext, final String name,
            final XmlDaoConfigContext context) {

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
                .rootBeanDefinition(GenericDaoFactoryBean.class);
        beanDefinitionBuilder.addPropertyValue("daoInterface", context
                .getInterfaceName(name));
        beanDefinitionBuilder.addPropertyValue("domainClass", context
                .getDomainClassName(name));
        beanDefinitionBuilder.addPropertyValue("createFinderQueries", context
                .getFinderLookupStrategy());

        if (StringUtils.hasText(context.getDaoBaseClassName())) {
            beanDefinitionBuilder.addPropertyValue("daoClass", context
                    .getDaoBaseClassName());
        }

        BeanDefinition customImplementation = detectCustomImplementation(name,
                context, parserContext);

        if (null != customImplementation) {

            String implementationBeanName = name + context.getDaoImplPostfix();

            parserContext.registerBeanComponent(new BeanComponentDefinition(
                    customImplementation, implementationBeanName));
            beanDefinitionBuilder.addPropertyReference(
                    "customDaoImplementation", implementationBeanName);
        }

        if (log.isDebugEnabled()) {

            StringBuilder builder = new StringBuilder("Registering Hades DAO: ");
            builder.append(context.getBeanName(name));
            builder.append(" - DAO interface: ");
            builder.append(context.getInterfaceName(name));
            builder.append(" - Implementation base class: ");

            builder
                    .append(StringUtils.hasText(context.getDaoBaseClassName()) ? context
                            .getDaoBaseClassName()
                            : GenericDaoFactoryBean.DEFAULT_DAO_IMPLEMENTATION);
        }

        AbstractBeanDefinition beanDefinition = beanDefinitionBuilder
                .getBeanDefinition();
        beanDefinition.setSource(context.getRootNode());

        BeanComponentDefinition definition = new BeanComponentDefinition(
                beanDefinition, name + context.getDaoNamePostfix());

        parserContext.registerBeanComponent(definition);
    }


    /**
     * Looks up a possibly available custom DAO implementation and returns it,
     * if found.
     * 
     * @param name
     * @param context
     * @param parserContext
     * @return the {@code BeanDefinition} of the custom implementation or null
     *         if none found
     */
    private BeanDefinition detectCustomImplementation(final String name,
            final XmlDaoConfigContext context, final ParserContext parserContext) {

        // Build pattern to lookup implementation class
        Pattern pattern = Pattern.compile(context
                .getImplementationClassName(name));

        // Build classpath scanner and lookup bean definition
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(
                false);
        provider.addIncludeFilter(new RegexPatternTypeFilter(pattern));
        Set<BeanDefinition> definitions = provider
                .findCandidateComponents(context.getDaoPackageName());

        // Return first found
        return (definitions.size() == 0) ? null : definitions.iterator().next();
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

            BeanDefinition definition = BeanDefinitionBuilder
                    .rootBeanDefinition(PAB_POST_PROCESSOR).getBeanDefinition();

            registry.registerBeanDefinition(definition.getBeanClassName(),
                    definition);
        }

        // Create PersistenceExceptionTranslationPostProcessor definition
        if (!registry.containsBeanDefinition(PET_POST_PROCESSOR.getName())) {

            BeanDefinition definition = BeanDefinitionBuilder
                    .rootBeanDefinition(
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

        private static final AnnotationTypeFilter ENTITY_ANNOTATION_FILTER = new AnnotationTypeFilter(
                Entity.class);
        private static final AssignableTypeFilter PERSISTABLE_FILTER = new AssignableTypeFilter(
                Persistable.class);


        /*
         * (non-Javadoc)
         * 
         * @see org.springframework.core.type.filter.TypeFilter#match(org.springframework.core.type.classreading.MetadataReader,
         *      org.springframework.core.type.classreading.MetadataReaderFactory)
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
}
